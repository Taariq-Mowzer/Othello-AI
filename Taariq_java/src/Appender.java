import java.io.*;




public class Appender
{
   private String fileName;
   private FileWriter fw;
   private BufferedWriter bw;
   public Appender(String s)
   {
      fileName = s;
      open();
   }
   
   public void open()
   {
      try
      {
         fw = new FileWriter(fileName, true);
         bw = new BufferedWriter(fw);
      }
      catch(IOException e)
      {
         System.out.println("IOExcpetion writing");
      }
      
   }
   
   public void writeLine(String s)
   {
      try
      {
      bw.write(s);
      bw.newLine();
      }
      catch(IOException e){}
   }
   
   public void writeLine(double[] d)
   {
      try
      {
      for(double i: d)
      {
         bw.write(i + " ");
      }
      bw.newLine();
      }
      catch(IOException e){}
   }
   
   public void close()
   {
       try
      {
      bw.close();
      }
      catch(IOException e){}

   }
      
}