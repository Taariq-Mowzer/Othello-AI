import java.io.*;
import org.encog.neural.networks.BasicNetwork;
import static org.encog.persist.EncogDirectoryPersistence.*;





public class DataGen
{
   public static void main(String[] args)
   {
      generateData(args[0], args[1]);  
   }
   
   
   public static void generateData(String file, String AI)
   {
      KeyBoardInterrupt checker = new KeyBoardInterrupt("thready", "stop");
      checker.start();
      //System.out.println(checker.getAlive());
      int n = 5;
      Gen[] threads = new Gen[n];
      BasicNetwork base = (BasicNetwork) loadObject(new File("AIs/"+ AI + ".eg"));
          
       for(int i = 0; i < n; i++)
      {
         threads[i] = new Gen("gen" + i, (BasicNetwork)base.clone());
         threads[i].start();      
      }
      
      while(checker.getAlive())
      {
          try{
      Thread.sleep(100);
      }
      catch(InterruptedException e){}
      }
      
      for(int i = 0; i < n; i++)
      {
         threads[i].interrupt();
         System.out.println(threads[i].isInterrupted());
      }
      
      System.out.println("Interrupted");
      int rows = 0;
      for(int i = 0; i < n; i++)
      {
         rows += threads[i].data.size();
      }
      System.out.println(rows);
      Appender transcriber = new Appender("EpochData/" + file + ".txt");
      int col = threads[0].data.get(0).length;
      int col1 = 130;
      for(int i = 0; i < n; i++)
      {
         double[] r = new double[col];
         for(int j = 0; j < threads[i].data.size(); j++)
         {
           transcriber.writeLine(threads[i].data.get(j));
         }
      }
      transcriber.close();
      System.out.println("Done writing");
   }

}