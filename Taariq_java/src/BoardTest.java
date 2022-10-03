import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.io.*;
import java.util.Scanner;


public class BoardTest{
   public static void main(String[] args){
      Board game = new Board();
     // print(test);
      System.out.println(game.hashCode());
      Random rand =  new Random(System.currentTimeMillis());
      //Random rand =  new Random(0);
      //while(!game.game_over){
       //  random_move(game,rand);
        // print(game);
      //}
      
      Scanner in = new Scanner(System.in);
      int s = in.nextInt();
      if (s == 1){
         random_move(game, rand);
      }
      while(true){
         int x = in.nextInt();
         int y = in.nextInt();
         int[] tmp = {x, y};
         game = game.play_next(tmp);
         //game.play(tmp);
         random_move(game,rand);
      }
   }
   
   public static void print(Board A){
      System.out.println(A);
      System.out.println("black = "+  A.black_squares + ", white = " + A.white_squares + ", turn = " + A.turn + ", game_over = " + A.game_over);
      System.out.print("Moves: ");
      for(int i = 0; i < A.moves.size(); i++){
         System.out.print("("+ A.moves.get(i)[0] + ", " + A.moves.get(i)[1] + ") ");
      }
      System.out.println();
   }
   
   public static void random_move(Board game, Random rand){
      try
         {
            TimeUnit.MILLISECONDS.sleep(200);
            int ind = rand.nextInt(game.moves.size());
            System.out.println(game.moves.get(ind)[0] + " " + game.moves.get(ind)[1]);
            game.play(game.moves.get(ind));
         }
         catch(InterruptedException ex)
         {
         }

   }
}