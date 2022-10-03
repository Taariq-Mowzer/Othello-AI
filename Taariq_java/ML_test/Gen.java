import java.util.Random;
import java.util.ArrayList;




public class Gen implements Runnable
{
   private Thread t;
   private String threadName;
   public ArrayList<double[]> data;
   private static int N = 0;
   private Random rand;
   public Gen(String name)
   {
      
      data = new ArrayList<double[]>();
      threadName = name;
   }
   
   private synchronized static void Count()
   {
      N++;
   }
   
   private synchronized static void Completed()
   {
      System.out.println("Completed simulations : " + N);
   }
   
   public synchronized boolean isInterrupted(){
      if (t == null){
         return true;
      }
      return t.isInterrupted();
   }
   
   public synchronized void interrupt()
   {
      if(t != null)
      {
         t.interrupt();
      }
   }
   
   
   public void run()
   {
      while (!isInterrupted()){
         genData();
      }
   }
   
   public void genData()
   {
      rand = new Random(System.nanoTime());
      int x = rand.nextInt(11);
      Board game = new Board();
      boolean flag = true;
      while (!game.game_over && !isInterrupted())
      {
         play(game);
         if (64 - game.black_squares - game.white_squares == x && flag)
         {
            flag = false;
            data.add(game.toNet());
            //System.out.println(game);
         }
      }
      //System.out.println(game);
      if (game.game_over)
      {
         int lastInd = data.size() - 1;
         if(game.black_squares > game.white_squares)
         {
            data.get(lastInd)[130] = 1.0;
         }
         else if(game.black_squares < game.white_squares)
         {
            data.get(lastInd)[130] = 0.0;
         }
         else{
            data.get(lastInd)[130] = 0.5;
         }
         Count();
         Completed();
      }
      
   }
   
   
   public void play(Board game)
   {
         if (64 - game.black_squares - game.white_squares > (11 - rand.nextInt(2)))
         {
            PureMonte diviner = new PureMonte(game, (long) 500);
            diviner.run();
            int[] move = diviner.getBestMove();
            //System.out.println(move[0] + " " + move[1]);
            game.play(move);
         }
         else
         {
            AlphaBeta searcher = new AlphaBeta(game, "searcher");
            searcher.run();
            //System.out.println(searcher.move[0] + " " + searcher.move[1]);
            game.play(searcher.move);
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