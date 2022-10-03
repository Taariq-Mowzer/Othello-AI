import java.util.ArrayList;
import java.util.Random;
import org.encog.neural.networks.BasicNetwork;



public class AIMonte implements Runnable{
   public AINode root;
   private long time;
   private BasicNetwork network;
   //static long select = 0;
   //static long expand = 0;
   //static long rollout = 0;
   //static long backup = 0;
   //static long N = 0;
   public AIMonte(Board board, long time, BasicNetwork network)
   {
      root = new AINode(board.clone());
      this.time = time;
      this.network = network;
   }
   
   public void go(int iterations)
   {
      AICrawler worker = new AICrawler(root, network);
      AINode t;
      double v;
      long start;
      for(int i = 0; i < iterations; i++)
      {
          // N++;
          // start = System.nanoTime();
           t = worker.Select();
          // select += (System.nanoTime() - start);
          // start = System.nanoTime();
           t = worker.Expand(t);
          // expand += (System.nanoTime() - start);
          // start = System.nanoTime();
           v = worker.Rollout(t);
          // rollout += (System.nanoTime() - start);
          // start = System.nanoTime();
           worker.Backup(t, v);
          // backup += (System.nanoTime() - start);
      }
   }
   
   public void run(){
      long start = System.nanoTime();
      while ((System.nanoTime() - start)/1000000 < time - 5){
         go(100);
      }
   }
   
   
   public int[] getBestMove()
   {
      Random rand = new Random(System.nanoTime());
      double t = 1.0;
      if (root.board.black_squares + root.board.white_squares > 20)
      {
         t = 0.05;
      }
      else if (root.board.black_squares + root.board.white_squares > 10)
      {
         t = 0.1;
      }
      int[] move = {-1, -1};
      if(root.children.size() > 0){
         move = root.children.get(0).move;
      }
      double sum = 0.0;
      
      for (AINode i: root.children){
        
            sum += Math.pow((double)i.visits, 1/t);
               
      }
      double r = rand.nextDouble();
      //System.out.println("sum: " + sum);
      //System.out.println("r: " + r);
      double s = 0.0;
       for (AINode i: root.children){
            //System.out.println("pow: " + Math.pow((double)i.visits, 1/t));
            s += Math.pow((double)i.visits, 1/t);
            //System.out.println("s: " + s/sum);
            if (r <= s/sum)
            {
               move = i.move; 
               break;
            }
               
      }

      return move;
   }
   
   
}


class AINode extends Board
{
   public AINode parent;
   public Board board;
   ArrayList<AINode> children;
   public double wins;
   final static double c = 1.0;
   public double visits;
   public double score;
   public boolean fully;
   public int turn;
   public int[] move;
   public AINode(AINode parent, Board board, int[] move)
   {
      children = new ArrayList<AINode>();
      this.board = board;
      this.parent = parent;
      this.turn = board.turn;
      this.move = new int[2];
      this.move[0] = move[0];
      this.move[1] = move[1];
      wins = 0.0;
      visits = 0.0;
      score = 0.0;
      fully = false;
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      s.append("m = {" + move[0] + ", " + move[1] + "}"+ " ") ;
      s.append("v = " + visits+ " ");
      s.append("w = " + wins + " ");
      s.append("s = " + score + " ");
      s.append("w/v = " + wins/visits + " ");
      return s.toString();
   }
   
   public double getScore()
   {
      if(parent == null)
      {
         return 0.0;
      }
      score =  Math.abs(wins)/visits + c*Math.sqrt(Math.log(parent.visits)/visits);
      return score;
   }
   
   public static int[] deadMove(){
      int[] tmp = {-1, -1};
      return tmp;
   }
   
   public AINode(Board board)
   {
      this(null, board, deadMove());
   }
}

class AICrawler{
   public AINode root;
   private BasicNetwork network;
   
   public AICrawler(AINode root, BasicNetwork network){
      this.root = root;
      this.network = network;
   }
   
   public AINode Select()
   {
      AINode cur = root;
      while(cur.fully)
      {
         double m = -1;
         AINode ncur = null;
         for(AINode i: cur.children)
         {
            if (i.getScore() > m)
            {
               m = i.score;
               ncur = i;
            }
         }
         if (ncur == null) break;
         cur = ncur;
      
      }
      return cur;
   }

   
   public AINode Expand(AINode cur)
   {
      if(cur.board.game_over) cur.fully = true;
    
      if (cur.fully) return cur;
      int[] move = cur.board.moves.get(cur.children.size());

      Board childBoard = cur.board.play_next(move);
      AINode child = new AINode(cur, childBoard, move);
      cur.children.add(child);
      if (cur.children.size() == cur.board.moves.size()){
         cur.fully = true;
      }
      return child;
       
   }
   
   public double Rollout(AINode cur)
   {
      if (cur.board.game_over)
      {
         if (cur.board.black_squares > cur.board.white_squares)
         {
            return 1.0;
         }
         else if(cur.board.black_squares == cur.board.white_squares)
         {
            return 0.0;
         }
         return -1.0;
     }
     else
     {
         double[] ans = new double[1];
         network.compute(cur.board.toIn(), ans);
         return ans[0]*2 -1.0;
     }
   }
   
   public void Backup(AINode cur, double v)
   {
      while(cur != null)
      {
         if(v < 0 && cur.turn > 0){
            cur.wins += v;
         }
         else if(v > 0 && cur.turn < 0)
         {
            cur.wins += v;
         }
         cur.visits += 1.0;
         cur = cur.parent;   
      }
   }
}