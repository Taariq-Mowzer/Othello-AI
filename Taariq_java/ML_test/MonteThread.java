import java.util.Scanner;




public class MonteThread{
  
  public static void main(String[] args)
  {
      Board game = new Board();
      method(game);
  }
   public static void method(Board game)
   {
      Scanner in = new Scanner(System.in);
      int threads = 6;
      int s = in.nextInt();
      if (s == 1)
      {
         ThreadedMonte diviners = new ThreadedMonte(game, (long)2000, threads);
         diviners.runThreads();
         int[] move = diviners.getBestMove();    
         diviners = null;     
         System.out.println(move[0] + " " + move[1]);
         game.play(move); 
      }
      
      while(true)
      {
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game = game.play_next(tmp);
         ThreadedMonte diviners = new ThreadedMonte(game, (long)2000, threads);
         diviners.runThreads();
         int[] move = diviners.getBestMove();         
         System.out.println(move[0] + " " + move[1]);
         game.play(move); 
      }
  }

}