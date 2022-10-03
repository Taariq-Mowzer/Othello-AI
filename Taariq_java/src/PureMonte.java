import java.util.ArrayList;
import java.util.Random;




public class PureMonte implements Runnable{
   public Node root;
   private long time;
   static long select = 0;
   static long expand = 0;
   static long rollout = 0;
   static long backup = 0;
   static long N = 0;
   public PureMonte(Board board, long time)
   {
      root = new Node(board.clone());
      this.time = time;
   }
   
   public void go(int iterations)
   {
      Crawler worker = new Crawler(root);
      Node t;
      double v;
      long start;
      for(int i = 0; i < iterations; i++)
      {
           N++;
           start = System.nanoTime();
           t = worker.Select();
           select += (System.nanoTime() - start);
           start = System.nanoTime();
           t = worker.Expand(t);
           expand += (System.nanoTime() - start);
           start = System.nanoTime();
           v = worker.Rollout(t);
           rollout += (System.nanoTime() - start);
           start = System.nanoTime();
           worker.Backup(t, v);
           backup += (System.nanoTime() - start);
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
      int[] move = {-1, -1};
      if(root.children.size() > 0){
         move = root.children.get(0).move;
      }
      double n = 0.0;
      
      for (Node i: root.children){
         if(i.visits > n){
            n = i.visits;
            move = i.move;
         }
      }
      return move;
   }
   
   
}


class Node extends Board
{
   public Node parent;
   public Board board;
   ArrayList<Node> children;
   public double wins;
   final static double c = 1.0;
   public double visits;
   public double score;
   public boolean fully;
   public int turn;
   public int[] move;
   public Node(Node parent, Board board, int[] move)
   {
      children = new ArrayList<Node>();
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
      if (parent.visits < 3)
      {
         score = Math.abs(wins)/visits; 
      }
      else{
         score =  Math.abs(wins)/visits + c*Math.sqrt(Math.log(parent.visits)/visits);
      }
      return score;
   }
   
   public static int[] deadMove(){
      int[] tmp = {-1, -1};
      return tmp;
   }
   
   public Node(Board board)
   {
      this(null, board, deadMove());
   }
}

class Crawler{
   public Node root;
   
   public Crawler(Node root){
      this.root = root;
   }
   
   public Node Select()
   {
      Node cur = root;
      while(cur.fully)
      {
         double m = -1;
         Node ncur = null;
         for(Node i: cur.children)
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

   
   public Node Expand(Node cur)
   {
      if(cur.board.game_over) cur.fully = true;
    
      if (cur.fully) return cur;
      int[] move = cur.board.moves.get(cur.children.size());

      Board childBoard = cur.board.play_next(move);
      Node child = new Node(cur, childBoard, move);
      cur.children.add(child);
      if (cur.children.size() == cur.board.moves.size()){
         cur.fully = true;
      }
      return child;
       
   }
   
   public double Rollout(Node cur)
   {
      Random rand =  new Random(System.nanoTime());
      Board roller = cur.board.clone();
      while(!roller.game_over)
      {
         roller.play(roller.moves.get(rand.nextInt(roller.moves.size())));
      }
      if (roller.black_squares > roller.white_squares)
      {
         return 0.9 + 0.1*roller.black_squares/64.0;
      }
      else if(roller.black_squares == roller.white_squares)
      {
         return 0.0;
      }
      return -0.9 - 0.1*roller.white_squares/64.0;
   }
   
   public void Backup(Node cur, double v)
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