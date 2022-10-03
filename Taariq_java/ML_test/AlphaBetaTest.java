





public class AlphaBetaTest
{
   public static void main(String[] args)
   {
      Board game = new Board();
      method1(game);
   }
   
   public static void method1(Board game)
   {
      System.out.println(game);
      while(!game.game_over)
      {
         if(play(game) == 1) return;
         System.out.println("black = " + game.black_squares + ", white = " + game.white_squares);
         System.out.println(game);
      }  
   }
   
   public static int play(Board game)
   {
      long time = 2000;
      if (game.black_squares + game.white_squares < 45){
         time = 200l;
      }
      int threads = 5;
      AlphaBeta searcher = new AlphaBeta(game, "searcher");
      searcher.start();
      ThreadedMonte diviners = new ThreadedMonte(game, time, threads);
      diviners.runThreads();
      searcher.interrupt();
      int[] move = diviners.getBestMove();
      System.out.println("Hash time1: " + searcher.hashTime1/1000000);
      System.out.println("Hash time2: " + searcher.hashTime2/1000000);
      if (searcher.finished)
      {
         System.out.println("Search finished with " + (64 - game.black_squares - game.white_squares) + " moves left");
         move = searcher.move;
         System.out.println("Expected score of " + searcher.score);
         return 1;
      }       
      System.out.println(move[0] + " " + move[1]);
      game.play(move);
      return 0; 
   }
}