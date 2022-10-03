import java.io.*;
import java.util.Scanner;



public class Reader
{
   private String fileName;
   public double[][] input;
   public double [][] output;
   private Scanner scan;
   
   public Reader(String s, int inputLength, int outputLength)
   {
      fileName = s;
      try
      {
         File file = new File(fileName);
         
       
         
         BufferedReader reader = new BufferedReader(new FileReader(fileName));
         int lines = 0;
         while (reader.readLine() != null) lines++;
         reader.close();
         
         
         scan = new Scanner(file);
         
         input = new double[lines][inputLength];
         output = new double[lines][outputLength];
         for (int i = 0; i < lines; i++)
         {
             readLine(inputLength, outputLength, i);
         }
         scan.close();
      }
      catch(IOException e)
      {
         System.out.println("OExcpetion reading");
      }
   }
   
   public void readLine(int inputLength, int outputLength, int ind)
   {
      for(int i = 0; i < inputLength; i++)
      {
         input[ind][i] = scan.nextDouble();
      }
      for(int i = 0; i < outputLength; i++)
      {
         output[ind][i] = scan.nextDouble();
      }
   }
}