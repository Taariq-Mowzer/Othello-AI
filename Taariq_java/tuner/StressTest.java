import java.util.Random;



public class StressTest{
   public static void main(String[] args){
      byte[][] b = {
      {0,0,0,-1,-1,-1,0,-1},
      {0, -1, 0, -1, 1, -1, -1, -1},
      {1, 1, 1, -1, 1, 1, -1, -1},
      {0, -1, 1, -1, 1, 1, 1, 0},
      {0, -1, -1, 1,1,1,1,1},
      {0,0,-1,-1,1,1,1,0},
      {0,0,1,1,1,1,0,0},
      {0,1,1,1,0,-1,0,0},
      };
      byte[][] c = {
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, -1, -1, -1, 0, 0},
      {0, 0, 0, 1, 1, 1, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0},
      };
      Random rand =  new Random(System.currentTimeMillis());
      Board test = new Board(c, (byte)1);
      long start_time = System.nanoTime();
      for(int i = 0; i < 300000; i++){
         test.play_next(test.moves.get(0));
      }
      //for (int i = 0; i < 10000000; i++){
         //Board test = new Board(b, (byte)1);
      //   rand.nextInt(100);
      //}
     // PureMonte diviner = new PureMonte(test);
     // diviner.run(10000);
     // Integer[] Move = diviner.getBestMove();
     // for(Node i: diviner.root.children)
     // {
      //   System.out.println(i);
      //}
      //System.out.println(Move[0] + " " + Move[1]);
      long end_time = System.nanoTime();
      System.out.println((end_time - start_time)/1000000);
   }
}