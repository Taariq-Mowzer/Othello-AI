import java.util.ArrayList;
import java.util.concurrent.*;


public class ThreadedMonte
{
   public ArrayList<PureMonte> roots;
   public ArrayList<int[]> moves;
   public ArrayList<double[]> values;
   public long time;
   public ThreadedMonte(Board board, long time, int threads)
   {
      roots = new ArrayList<PureMonte>();
      this.time = time;
      for(int i = 0; i < threads; i++)
      {
         roots.add(new PureMonte(board, (long)(time - 20)));
      }
   }
   
   public void runThreads()
   {
      ExecutorService executor = Executors.newFixedThreadPool(roots.size());
      
      for(PureMonte i: roots)
      {
         executor.execute(i);
      }
      executor.shutdown();
      try{
      Thread.sleep(time - 5);
      }
      catch(InterruptedException e){}
   }
   
   public void merge(Node cur)
   {
      for(int i = 0; i < moves.size(); i++){
         if(cur.move[0] == moves.get(i)[0] && cur.move[1] == moves.get(i)[1])
         {
            values.get(i)[0] += cur.visits;
            values.get(i)[1] += cur.wins;
            return;
         }
      }
      moves.add(cur.move.clone());
      double[] tmp = {cur.visits, cur.wins};
      values.add(tmp);
   }
   
   public int[] getBestMove()
   {
      moves = new ArrayList<int[]>();
      values = new ArrayList<double[]>();
      for(PureMonte i: roots)
      {
         for(Node j: i.root.children)
         {
            merge(j);
         }
      }
      
      int[] bestMove = moves.get(0);
      double winRate = -2.0;
      
      for(int i = 0; i < moves.size(); i++)
      {
         double curWinRate = Math.abs(values.get(i)[1])/values.get(i)[0];
         if (curWinRate > winRate)
         {
            winRate = curWinRate;
            bestMove = moves.get(i);
         }
      }
      return bestMove;
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder();
      double sum = 0;
      for(int i = 0; i < moves.size(); i++)
      {
         sum += values.get(i)[0];
         s.append("move = {" + moves.get(i)[0] + ", " + moves.get(i)[1] + "} visits = " 
         + values.get(i)[0] + ", wins = " + values.get(i)[1] + ", winRate = " + Math.abs(values.get(i)[1]/values.get(i)[0]) + "\n" );
      }
      s.append("Total = "+ sum  +"\n");
   return s.toString();
  } 
}