import java.io.*;
import org.encog.neural.networks.BasicNetwork;
import static org.encog.persist.EncogDirectoryPersistence.*;
import java.util.Random;






public class Infras
{
   
   public static void main(String[] args)
   {
      tourn(args[0], args[1], args[2]);
   }
   
   public static void play(Board game, long time, BasicNetwork network)
   {
      Random rand = new Random(System.nanoTime());
      if (64 - game.black_squares - game.white_squares > (11 - rand.nextInt(2)))
         {
            AIMonte diviner = new AIMonte(game, time, network);
            diviner.run();
            int[] move = diviner.getBestMove();
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
   
   public static int verse(BasicNetwork black, BasicNetwork white, long time)
   {
      Board game = new Board();
      
      while(!game.game_over)
      {
         if (game.turn == (byte)1)
         {
            play(game, time, black);
         }
         else{
            play(game, time, white);
         }
      }
      if(game.black_squares > game.white_squares)
      {
         return 1;
      }
      else if(game.black_squares < game.white_squares)
      {
         return -1;
      }
      return 0;
   }
   
   public static void tourn(String black, String white, String am)
   {
         BasicNetwork network1 = (BasicNetwork) loadObject(new File("AIs/" + black + ".eg"));
         BasicNetwork network2 = (BasicNetwork) loadObject(new File("AIs/" + white + ".eg"));
         int M = Integer.parseInt(am);
         int[] first = {0, 0};
         int[] second = {0, 0};
         long time = 100;
         for(int i = 0; i < M; i++)
         {
            int x = verse(network1, network2, time);
            if (x == 1)
               first[0]++;
           else if (x == -1)
               second[1]++;
           System.out.println(i + 1 + "/" + 2*M);
         }
         for(int i = 0; i < M; i++)
         {
            int x = verse(network2, network1, time);
            if (x == 1)
               second[0]++;
           else if (x == -1)
               first[1]++;
           System.out.println(i + M + 1 + "/" + 2*M);
         }
         
         System.out.println(black + ": (" + first[0] + ", " + first[1] + ")");
         System.out.println(white + ": (" + second[0] + ", " + second[1] + ")");

   }
   
}