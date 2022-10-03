import org.encog.neural.networks.BasicNetwork;
import java.util.concurrent.*;
import java.util.Random;




public class AILockMonte extends LockMonte
{
   private BasicNetwork network;
   AILockMonte(Board game, int threads, long time, BasicNetwork network)
   {
      super(game, threads, time);
      this.network = network;
   }
   
   public void start()
   {
      for(int i = 0; i < threads; i++)
      {
         clutter[i] = new AILockCrawler(root, time, network);
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
      if(root.children.length > 0){
         move = root.children[0].move;
      }
      double sum = 0.0;
      
      for (LockNode i: root.children){
        
            sum += Math.pow((double)i.n.get(), 1/t);
               
      }
      double r = rand.nextDouble();
      //System.out.println("sum: " + sum);
      //System.out.println("r: " + r);
      double s = 0.0;
       for (LockNode i: root.children){
            //System.out.println("pow: " + Math.pow((double)i.visits, 1/t));
            s += Math.pow((double)i.n.get(), 1/t);
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

class AILockCrawler extends LockCrawler
{
   private BasicNetwork network;
   public AILockCrawler(LockNode root, long time, BasicNetwork network)
   {
      super(root, time);
      this.network = (BasicNetwork)network.clone();
   }
   
   
   public int Rollout(LockNode cur)
   {
      if (cur.board.game_over)
      {
         if (cur.board.black_squares > cur.board.white_squares)
         {
            return 1*LockCrawler.scale;
         }
         else if(cur.board.black_squares == cur.board.white_squares)
         {
            return 0;
         }
         return -LockCrawler.scale;
     }
     else
     {
         double[] ans = new double[1];
         network.compute(cur.board.toIn(), ans);
         //System.out.println(ans[0]*2 -1.0);
         //System.out.println((int)((ans[0]*2 -1.0)*LockCrawler.scale));
         
         return (int)((ans[0]*2 -1.0)*LockCrawler.scale);
     }

   }
}