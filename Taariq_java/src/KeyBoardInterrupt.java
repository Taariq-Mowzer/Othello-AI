import java.util.Scanner;




public class KeyBoardInterrupt implements Runnable
{
   private Thread t;
   private String threadName;
   private String ruptor;
   public boolean alive;
   
   public KeyBoardInterrupt(String name, String ruptor)
   {
      alive = true;
      threadName = name;
      this.ruptor = ruptor;
   }
   public synchronized boolean getAlive()
   {
      return alive;
   }
   public synchronized void setAlive(boolean b)
   {  
      alive = b;
   }
   
   
   public void run()
   {
      Scanner scan = new Scanner(System.in);
      boolean flag = true;
      while (flag)
      {
         String tmp = scan.nextLine();
         if (ruptor.equals(tmp.trim())){
            flag = false;
            setAlive(false);
         }
      }
   }
   
   
   public void start()
   {
      if (t == null) 
      {
         t = new Thread (this, threadName);
         t.start ();
      }

   }
}