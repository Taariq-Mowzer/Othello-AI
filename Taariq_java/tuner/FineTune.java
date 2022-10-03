






public class FineTune
{
   public static void main(String[] args)
   {
      int M = 2;
      Double[] C = {1.0, 1.4};
      double[][] score = new double[M][2];
      int n = 0;
      int R = 10;
      for(int k = 0; k < R; k++){
      for(int i = 0; i < M; i++)
      {
         for(int j = 0; j < M; j++){
            if(i != j){
               n += 1;
               int x = play_game(C[i], C[j]);
               if (x == 1){
                  //i beat j
                  score[i][0] += 1;
               }
               else if (x == -1){
                  score[j][1] += 1;
               }
               else{
                  score[i][0] += 0.5;
                  score[j][1] += 0.5;
               }
               System.out.println(n + "/" + (R*M*(M -1)));
            }
         }
      }
      }
      for(int i = 0; i < M; i++)
      {
         System.out.println(C[i] + ": (" + score[i][0] + ", " + score[i][1] + ")");   
      }


}
   
   public static int play_game(double c1, double c2)
   {
      Board game = new Board();
      while(!game.game_over)
      {
         if(game.turn == 1) play(game, c1);
         else play(game, c2);
      }
      if (game.black_squares > game.white_squares)
      {
         return 1;
      }
      else if (game.black_squares < game.white_squares)
      {
         return -1;
      }
      else return 0;
   }
   
   public static void play(Board game, double c)
   {
      int threads = 5;
      ThreadedMonte diviners = new ThreadedMonte(game, (long)1600, threads, c);
      diviners.runThreads();
      Integer[] move = diviners.getBestMove();    
      diviners = null;     
      System.out.println(move[0] + " " + move[1]);
      game.play(move); 
   }
}