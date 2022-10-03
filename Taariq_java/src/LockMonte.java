import java.util.concurrent.atomic.*;
import java.util.Random;
import java.util.concurrent.*;






public class LockMonte
{
   int threads;
   LockCrawler[] clutter;
   LockNode root;
   long time;
   
   public LockMonte(Board game, int threads, long time)
   {
      this.threads = threads;
      clutter = new LockCrawler[threads];
      int[] tmp = {-1, -1};
      root = new LockNode(null, game, tmp);
      this.time = time;
   }
   
   public void start()
   {
      for(int i = 0; i < threads; i++)
      {
         clutter[i] = new LockCrawler(root, time);
      }
      ExecutorService executor = Executors.newFixedThreadPool(threads);
      
       for(LockCrawler i: clutter)
      {
         executor.execute(i);
      }
      executor.shutdown();
      try{
      Thread.sleep(time - 5);
      }
      catch(InterruptedException e){} 
   }
   
   public int[] getBestMove()
   {
      int[] move = {-1, -1};
      if(root.children.length > 0){
         move = root.children[0].move;
      }
      int n = 0;
      
      for (LockNode i: root.children){
         if(i.n.get() > n){
            n = i.n.get();
            move = i.move;
         }
      }
      return move;
   }

   
}




class LockNode
{
   public int turn;
   public LockNode parent;
   public AtomicInteger w;
   public AtomicInteger n;
   public int[] move;
   public LockNode[] children;
   public Board board;
   public AtomicInteger ExpandedChildren;
   public AtomicBoolean isFullyExpanded;
   public AtomicInteger FinishedChildren;
   
   
   public LockNode(LockNode parent, Board game, int[] move)
   {
      this.parent = parent;
      this.board = game;
      turn = this.board.turn;
      this.move = move;
      children = new LockNode[game.moves.size()];
      w = new AtomicInteger(0);
      n = new AtomicInteger(0);    
      ExpandedChildren = new AtomicInteger(0);
      FinishedChildren = new AtomicInteger(0);
      isFullyExpanded = new AtomicBoolean(false);
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder();
     
      for(LockNode i: children)
      {
         s.append("move: (" + i.move[0] + ", " + i.move[1] + "), w = " + i.w.get()/LockCrawler.scale + " n = " + i.n.get()/LockCrawler.scale  + " winrate = " + ((double) i.w.get())/ i.n.get() +"\n");
      }
     
      s.append(board.toString());
      
      return s.toString();
   }
   
}

class LockCrawler implements Runnable
{
   private static final double c = 1.0; 
   public static final int scale = 1000;
   public LockNode root;
   public long time;
   private Random rand;
   
   public LockCrawler(LockNode root, long time)
   {
      this.root = root;
      rand = new Random(System.nanoTime());
      this.time = time;
   }
   
   public void run()
   {
      long start = System.nanoTime();
      while ((System.nanoTime() - start)/1000000 < time - 5){
         go(100);
      }

   }
   
   public void go(int iterations)
   {
      LockNode t;
      int v;
      for(int i = 0; i < iterations; i++)
      {
         t = Select(root);
         //System.out.println("Select Done");
         t = Expand(t);
         //System.out.println("Expand Done");
         v = Rollout(t);
         //System.out.println("Rollout Done");
         Backup(t, v); 
         //System.out.println("Backup Done");
      }
   }
   
   private LockNode Select(LockNode start)
   {
      //virtual loss
      LockNode cur = start;
      
      while (cur.isFullyExpanded.get() && !cur.board.game_over)
      {
         double maxScore = -10.0;
         LockNode nex = cur;
         
         for(LockNode i: cur.children)
         {
            if (i == null)
            {
               System.out.println("Massive problem");
            }
            double w = i.w.get();
            double nc = i.n.get();
            double n = cur.n.get();
            double score = ((double)w)/nc;
            if (n > 2.0)
               score += c*Math.sqrt(Math.log(n/scale)/(nc/scale));
            if(score > maxScore)
            {
               maxScore = score;
               nex = i;
            }
            //System.out.println(score);
         }
         //virtual loss
         //cur.w.addAndGet(-scale/2);
         
         cur = nex;
      }
      return cur;
   }
   
   private LockNode Select()
   {
      return Select(root);  
   }
   
   
   private LockNode Expand(LockNode cur)
   {
      if(cur.board.game_over){
         cur.isFullyExpanded.set(true);
         return cur;
     }
         
      int ind = -1;
      
      do
      {
         ind = cur.ExpandedChildren.get();
         if (ind >= cur.children.length)
            return cur; 
      } while(!cur.ExpandedChildren.compareAndSet(ind, ind + 1));
      
      int[] move = cur.board.moves.get(ind);
      
      cur.children[ind] = new LockNode(cur, cur.board.play_next(move), move);
      //System.out.println(cur.children[ind]);
      int fin = -1;
      
      do
      {
         fin = cur.FinishedChildren.get();
      }while(!cur.FinishedChildren.compareAndSet(fin, fin + 1));
      
      if (fin + 1 == cur.children.length)
      {
         cur.isFullyExpanded.set(true);
         //System.out.println("FullyExpanded");
      }
      
      return cur.children[ind];
   }
   
   public int Rollout(LockNode cur)
   {
      Random rand =  new Random(System.nanoTime());
      Board roller = cur.board.clone();
      while(!roller.game_over)
      {
         roller.play(roller.moves.get(rand.nextInt(roller.moves.size())));
      }
      if (roller.black_squares > roller.white_squares)
      {
         return (int)(scale*(0.9 + 0.1*roller.black_squares/64.0));
      }
      else if(roller.black_squares == roller.white_squares)
      {
         return 0;
      }
      return (int)(scale*(-0.9 - 0.1*roller.white_squares/64.0));
   }
   
   private void Backup(LockNode cur, int v)
   {
      while(cur != null)
      {
         //cur.w.addAndGet(scale/2);
         cur.n.addAndGet(scale);
         if(v < 0 && cur.turn > 0){
            cur.w.addAndGet(-v);
         }
         else if(v > 0 && cur.turn < 0)
         {
            cur.w.addAndGet(v);
         }
         cur = cur.parent;   
      }

   }

   
   
}