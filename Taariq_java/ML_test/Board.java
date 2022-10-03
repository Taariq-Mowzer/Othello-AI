import java.util.ArrayList;





public class Board
{

   final static int board_length = 8;
   final static byte black = (byte)1;
   final static byte white = (byte)-1;
   final static byte empty = (byte)0;
   final static int[][] dir = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
   
   private byte[][] board;
   
   public ArrayList <int[]> moves;
   public int black_squares;
   public int white_squares;
   public byte turn; //1 for black; -1 for white
   public boolean game_over;
   
   public Board(byte[][] board, byte turn, boolean GEN)
   {
      this.turn = turn;
      this.board = board;
      if (GEN) gen();  
   }
   public Board(byte[][] board, byte turn)
   {
      this(board, turn, true);
   }
   public Board()
   {
            this(base_board(), (byte)1, true);
   }
   public Board clone()
   {
      byte[][] base = new byte[board_length][board_length];
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
         {
            base[i][j] = board[i][j];
         }
      }
      Board ans = new Board(base, turn, false);
      ans.black_squares = black_squares;
      ans.white_squares = white_squares;
      ans.game_over = game_over;
      ans.moves = new ArrayList<int[]>();
      for(int[] i: moves)
      {
         int[] m = new int[2];
         m[0] = i[0];
         m[1] = i[1];
         ans.moves.add(m);
      }
      return ans;
   }   
   private static byte[][] base_board()
   {
      byte[][] tmp = new byte[board_length][board_length];
      //3 3, 4 4 white; 3 4, 4 3 black;
      tmp[3][3] = white;
      tmp[3][4] = black;
      tmp[4][3] = black;
      tmp[4][4] = white;
      return tmp;
   }  
   
   public void gen()
   {
      //System.out.println("generating");
      black_squares = 0;
      white_squares = 0;
      moves = new ArrayList<int[]>();
      int[] tmp = new int[2];
      for(int i = 0; i < board_length; i++)
      {
         tmp[0] = i;
         for(int j = 0; j < board_length; j++)
         {
            tmp[1] = j;
           if (board[i][j] == black){
               black_squares++;
           }
           else if (board[i][j] == white){
             white_squares++;
           }
           else if(valid(tmp)){
              int[] another = new int[2];
              another[0] = tmp[0];
              another[1] = tmp[1];
              moves.add(another);
            }
         }
      }
      if (moves.size() == 0)
      {
         int[] another = new int[2];
         another[0] = -1;
         another[1] = -1;

         moves.add(another);
         this.turn = (byte)-this.turn;
         game_over = true;
         for(int i = 0; i < board_length; i++)
         {
            tmp[0] = i;
            for(int j = 0; j < board_length; j++)
            {
               tmp[1] = j;
               if (valid(tmp))
               {
                  //System.out.println("Detected as valid" + tmp[0] + " " + tmp[1]);
                  game_over = false;
                  break;
               }
            }
            if (!game_over)
            {
               break;
            }
         }
         this.turn = (byte)-this.turn;
      }
      else{
         game_over = false;
      }

   }
   
   private boolean valid(int[] x)
   {
      if (board[x[0]][x[1]] != empty) return false;
      byte opponent = (byte)(-1*turn);
      for (int i = 0; i < 8; i++)
      {
         int[] cur = {x[0], x[1]};
         cur[0] += dir[i][0];
         cur[1] += dir[i][1];
         
         if (-1 < cur[0] && cur[0] < board_length && -1 < cur[1] && cur[1] < board_length && board[cur[0]][cur[1]] != opponent){
            continue;
         }
         cur[0] += dir[i][0];
         cur[1] += dir[i][1];
         while (-1 < cur[0] && cur[0] < board_length && -1 < cur[1] && cur[1] < board_length)
         {
            if (board[cur[0]][cur[1]] == empty)
            {
               break;
            }
            else if (board[cur[0]][cur[1]] == turn)
            {
               return true;
            }
            cur[0] += dir[i][0];
            cur[1] += dir[i][1];
         }  
      }
      return false;
   }
   public Board play_next(int[] x)
   {
      byte[][] mem = new byte[board_length][board_length];
      
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
         {
            mem[i][j] = board[i][j];
         }
      }
      play(x, false);
      Board ans = new Board(this.board, this.turn);
      this.board = mem;
      this.turn = (byte)-this.turn;
      return ans;      
   }   
   
   public void play(int[] x){
      play(x, true);
   }
   
   public void play(int[] x, boolean GEN)
   {
      //assumes x is valid
      byte opponent = (byte)(-1*turn);
      if (x[0] == -1)
      {
        // System.out.println("turn:" + turn);
         turn = opponent;
        // System.out.println("turn:" + turn);
        if (GEN)
         gen();
         return;
      }
      board[x[0]][x[1]] = turn;
      for (int i = 0; i < 8; i++)
      {
         int[] cur = {x[0], x[1]};
         cur[0] += dir[i][0];
         cur[1] += dir[i][1];
         
         if (-1 < cur[0] && cur[0] < board_length && -1 < cur[1] && cur[1] < board_length && board[cur[0]][cur[1]] != opponent){
            continue;
         }
         cur[0] += dir[i][0];
         cur[1] += dir[i][1];
         while (-1 < cur[0] && cur[0] < board_length && -1 < cur[1] && cur[1] < board_length)
         {
            
             if (board[cur[0]][cur[1]] == empty)
            {
               break;
            }
            else if (board[cur[0]][cur[1]] == turn)
            {
               cur[0] -= dir[i][0];
               cur[1] -= dir[i][1];

               while(cur[0] != x[0] || cur[1] != x[1])
               {
                  board[cur[0]][cur[1]] = turn;
                  cur[0] -= dir[i][0];
                  cur[1] -= dir[i][1];
               }
               break;
            }
            cur[0] += dir[i][0];
            cur[1] += dir[i][1];
         }  
      }
      //System.out.println("here");
      turn = opponent;
      if(GEN) gen();

   }
   
   public String hashString()
   {
      StringBuilder s = new StringBuilder(71);
      s.append(turn + "" + black_squares + "" +  white_squares);
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
            {
            if(board[i][j] == black) s.append("x");
            else if (board[i][j] == white) s.append("o");
            else s.append(".");
         }
      }
       
      return s.toString();

   }
   
   public int hashCode()
   {
      int prime = 97;
      int hash = 0;
      int roll = 1;
      if(turn == 1){
         hash -= 1;
      }
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
            {
            if(board[i][j] == black) hash += roll*2;
            else if (board[i][j] == white) hash += roll;
            roll *= prime;
         }
      }
       
      return hash;
   }
   
   public boolean equals(Object obj)
   {
      if(this == obj)
         return true;
      if(obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Board other = (Board) obj;
      if(other.turn != this.turn || other.black_squares != this.black_squares || other.white_squares != this.white_squares)
         return false;
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
         {
            if(other.board[i][j] != this.board[i][j])
            {
               return false;
            }
         }
      }
      return true;
   }
   
   public String toString()
   {
      StringBuilder s = new StringBuilder(136);
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++)
            {
            if(board[i][j] == black) s.append("x ");
            else if (board[i][j] == white) s.append("o ");
            else s.append(". ");
         }
         s.append("\n");
      }
       
      return s.toString();
   }
   
   public double[] toIn()
   {
      //Input for a NN
      double[] ans = new double[130];
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++){
            if (board[i][j] == black)
            {
               ans[8*i + j] = 1.0;
            }
            else if (board[i][j] == white)
            {
               ans[64 + 8*i + j] = 1.0;
            }
         }
      }
      if (turn == (byte)1){
         ans[128] = 1.0;
      }
      else{
         ans[129] = 1.0;
      }
      return ans;

   }
   
   public double[] toNet()
   {
      double[] ans = new double[131];
      ans[130] = 10.0;
      for(int i = 0; i < board_length; i++)
      {
         for(int j = 0; j < board_length; j++){
            if (board[i][j] == black)
            {
               ans[8*i + j] = 1.0;
            }
            else if (board[i][j] == white)
            {
               ans[64 + 8*i + j] = 1.0;
            }
         }
      }
      if (turn == (byte)1){
         ans[128] = 1.0;
      }
      else{
         ans[129] = 1.0;
      }
      return ans;
   }
   
   
}