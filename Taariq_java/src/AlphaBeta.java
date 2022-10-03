import java.util.HashMap;







public class AlphaBeta implements Runnable
{
   final static byte maxxer = (byte) 1;
   private Thread t;
   private String threadName;
   private Board root;
   public boolean finished;
   public int[] move;
   public int score;
   private HashMap<Board, Integer> memory;

   
   public AlphaBeta(Board board, String name)
   {
      finished = false;
      root = board.clone();
      threadName = name;
      score = 65;
      memory = new HashMap<Board, Integer>(400000);

   }
   
   public void interrupt()
   {
      if (t != null)
         t.interrupt();
   }
   
   public boolean isAlive()
   {
      if (t != null)
         return t.isAlive();
      return false;
   }
   
   public boolean isInterrupted()
   {
      if (t != null)
         return t.isInterrupted();
      return true;

   }
   
   
   public void run()
   {
         score = alphaBeta(root, -65, 65);
         if (score != 65)
            finished = true;
   }
   
   private int alphaBeta(Board node, int alpha, int beta)
   {
      if(t == null || !t.isInterrupted()){
         Integer x = memory.get(node);
         if(x != null){
            return x;
         }
         int value = 0;
         if (node.game_over)
            return (node.black_squares - node.white_squares);
      
         if (node.turn == maxxer)
         {
            value = -65;
            
            for(int i = 0; i < node.moves.size(); i++)
            {
               value = Math.max(value, alphaBeta(node.play_next(node.moves.get(i)), alpha, beta));
               if (node == root)
               {
                  if(value > alpha)
                  {
                     move = node.moves.get(i);
                  }
               }
               alpha = Math.max(alpha, value);
               if (alpha >= beta)
                  break;
            }
         } 
         else
         {
            value = 65;
            
            for(int i = 0; i < node.moves.size(); i++)
            {  
               value = Math.min(value, alphaBeta(node.play_next(node.moves.get(i)), alpha, beta));
                if(node == root){
                  if(value < beta)
                    {
                       move = node.moves.get(i);
                    }
                } 
                beta = Math.min(beta, value);
                if (beta <= alpha)
                {
                  break;
                }
            }
         }
         memory.put(node, value);
         return value;
      }
      else{
         return 65;
      }
  }
   
   public void start()
   {
      if (t == null) 
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }
   
}