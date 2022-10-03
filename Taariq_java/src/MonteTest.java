import java.util.Scanner;




public class MonteTest{
  
  public static void main(String[] args)
  {
      Board game = new Board();
      method1(game);
  }
  public static void method1(Board game)
  {
      Scanner in = new Scanner(System.in);
      int s = in.nextInt();
      long time = 2000;
      if (s == 1)
      {
         PureMonte diviner = new PureMonte(game, time);
         diviner.run();
         int[] move = diviner.getBestMove();
         System.out.println(move[0] + " " + move[1]);
         game.play(move);

      }
      
      while(true)
      {
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game = game.play_next(tmp);
         PureMonte diviner = new PureMonte(game, time);
         diviner.run();
         int[] move = diviner.getBestMove();
         System.out.println(move[0] + " " + move[1]);
         game.play(move);
      }
   }
   public static void method2(Board game)
   {
     System.out.println(game);
      while(!game.game_over)
      {
         PureMonte diviner = new PureMonte(game, (long)2000);
         diviner.run();
         int[] move = diviner.getBestMove();
          for(Node i: diviner.root.children)
         {
         System.out.println(i);
         }
         System.out.println(move[0] + " " + move[1]);  
         System.out.println("select: " + (PureMonte.select/1000000));
         System.out.println("expand: " + (PureMonte.expand/1000000));
         System.out.println("rollout: " + (PureMonte.rollout/1000000));
         System.out.println("backup: " + (PureMonte.backup/1000000)); 
         System.out.println("Total iterations:" + PureMonte.N);
         game.play(move);
         System.out.println(game);
      }
   }
   public static void method3(Board game)
   {
      int threads = 5;
     System.out.println(game);
      while(!game.game_over)
      {
         ThreadedMonte diviners = new ThreadedMonte(game, (long) 2000, threads);
         diviners.runThreads();
         int[] move = diviners.getBestMove();
         
         System.out.println(diviners);
         
         System.out.println(move[0] + " " + move[1]);  
       //  System.out.println("select: " + (PureMonte.select/1000000));
       //  System.out.println("expand: " + (PureMonte.expand/1000000));
       //  System.out.println("rollout: " + (PureMonte.rollout/1000000));
       //  System.out.println("backup: " + (PureMonte.backup/1000000)); 
         game.play(move);
         System.out.println(game);
      }
      System.out.println("Done");

  }
   public static void method4(Board game)
   {
      Scanner in = new Scanner(System.in);
      int threads = 5;
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