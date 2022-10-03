import org.encog.neural.networks.BasicNetwork;
import static org.encog.persist.EncogDirectoryPersistence.*;
import java.util.Scanner;
import java.io.*;


public class TaariqLearning
{
   private static boolean alphaBeta = false;
   
   public static void main(String[] args)
   {
      String filename = "AIs/" + args[0] +".eg";
      BasicNetwork network = (BasicNetwork) loadObject(new File(filename));
      Board game = new Board();
   
       
      Scanner in = new Scanner(System.in);
      int s = in.nextInt();
      if (s == 1)
      {
         findMove(game, network);
      }
       while(true)
      {
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game = game.play_next(tmp);
         findMove(game, network);
      }
    
      /*System.out.println(game);
      while(!game.game_over)
      {
         System.out.println(game);

         findMove(game, network);
         System.out.println(game);
      }*/



   }
   
   public static void findMove(Board game, BasicNetwork network)
   {
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
         long time = 2000;
         int threads = 6;
         AlphaBeta searcher = null;
         boolean searcherActivated = (64 - game.black_squares - game.white_squares) < 16;
         if (searcherActivated)
         {
            threads--;
            searcher = new AlphaBeta(game, "searcher");
            searcher.start();
         }
         AILockMonte diviners = new AILockMonte(game, threads, time, network);
         diviners.start();
         //System.out.println(diviners.root);
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