import java.io.*;
import org.encog.neural.networks.BasicNetwork;
import static org.encog.persist.EncogDirectoryPersistence.*;
import java.util.Scanner;

public class AIMonteTest{
  
  public static void main(String[] args)
  {
      Board game = new Board();
      String filename = "AIs/" + args[0] +".eg";
      method1(game, filename);
  }
  public static void method1(Board game, String filename)
  {
      BasicNetwork network = (BasicNetwork) loadObject(new File(filename));
      Scanner in = new Scanner(System.in);
      int s = in.nextInt();
      long time = 2000;
      if (s == 1)
      {
         play(game, network);
      }
      
      while(true)
      {
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game = game.play_next(tmp);
         play(game, network);      
      }
   }
   public static void method2(Board game, String filename)
   {
     long time = (long)2000;
     BasicNetwork network = (BasicNetwork) loadObject(new File(filename));
     System.out.println(network);
     System.out.println(game);
      while(!game.game_over)
      {
         AIMonte diviner = new AIMonte(game, time, network);
         diviner.run();
         int[] move = diviner.getBestMove();
          for(AINode i: diviner.root.children)
         {
         System.out.println(i);
         }
         System.out.println(move[0] + " " + move[1]);  
         /*System.out.println("select: " + (AIMonte.select/1000000));
         System.out.println("expand: " + (AIMonte.expand/1000000));
         System.out.println("rollout: " + (AIMonte.rollout/1000000));
         System.out.println("backup: " + (AIMonte.backup/1000000)); 
         System.out.println("Total iterations:" + AIMonte.N);*/
         game.play(move);
         System.out.println(game);
      }
   }
   
   public static void play(Board game, BasicNetwork network)
   {
      long time = 2000;
      if (64 - game.black_squares - game.white_squares > 12)
         {
            AIMonte diviner = new AIMonte(game, time, network);
            diviner.run();
            int[] move = diviner.getBestMove();
            System.out.println(move[0] + " " + move[1]);
            game.play(move);
         }
         else
         {
            AlphaBeta searcher = new AlphaBeta(game, "searcher");
            searcher.run();
            System.out.println(searcher.move[0] + " " + searcher.move[1]);
            game.play(searcher.move);
         }

   }
   
}