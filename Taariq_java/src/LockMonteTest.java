import java.util.Scanner;





public class LockMonteTest
{
   private static boolean alphaBeta = false;
   public static void main(String[] args)
   {
      method1();
   }
   
   public static void method1()
   {
      Board game = new Board();
      Scanner in = new Scanner(System.in);
      int s = in.nextInt();
      if (s == 1)
         play(game);
      
      while(true)
      {
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game.play(tmp);
         play(game); 
      }
      
      
   }
   
   public static void method2()
   {
      Board game = new Board();
      while(!game.game_over)
      {
         System.out.println(game);
         play(game);
      }
      System.out.println(game);
   }
   
   
   public static void play(Board game)
   {
      int threads = 6;
      long time = 2000;
      int[] move = new int[3];
      if(alphaBeta)
      {
         AlphaBeta searcher = new AlphaBeta(game, "searcher");
         searcher.run();
         //move = searcher.move;
         move[0] = searcher.move[0];
         move[1] = searcher.move[1];
         move[2] = searcher.score;
      }
       else
      {
         AlphaBeta searcher = null;
         boolean searcherActivated = (64 - game.black_squares - game.white_squares) < 16;
         if (searcherActivated)
         {
            threads--;
            searcher = new AlphaBeta(game, "searcher");
            searcher.start();
         }
         LockMonte diviners = new LockMonte(game, threads, time);
         diviners.start();
         if (searcherActivated)
            searcher.interrupt();
         if (searcherActivated && searcher.finished){
            alphaBeta = true;
            //move = searcher.move;
            move[0] = searcher.move[0];
            move[1] = searcher.move[1];
            move[2] = searcher.score;

         }
         else{
            //move = diviners.getBestMove();
            int[] Smove = diviners.getBestMove();
            move[0] = Smove[0];
            move[1] = Smove[1];
         }
      }
      System.out.println(move[0] + " " + move[1] + " " + move[2]);
      game.play(move);

   }
}