import java.util.Locale;
import java.util.ArrayList;
import org.encog.Encog ;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.strategy.RegularizationStrategy;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import static org.encog.persist.EncogDirectoryPersistence.*;
import java.io.*;




public class AIMaker
{
   public static void main(String[] args)
   {
      Locale.setDefault(new Locale("en", "US"));
      int[] nn7 = {130, 130, 130,  1};
      double lambda = 0.0003;
      LearnTest(args[0] , nn7, 0.0000001, args[1]);
   }
   
   public static void LearnTest(String fileName, int[] nodes, double lambda, String AIName)
   {
      BasicNetwork network = new BasicNetwork();
      for(int i = 0; i < nodes.length; i++)
      {
         boolean bias = true;
         if (i == nodes.length - 1)
            bias = false;
         if (i != 0)
         network.addLayer(new BasicLayer(new ActivationSigmoid(), bias, nodes[i]));
         else
            network.addLayer(new BasicLayer(null, bias, nodes[i]));

      }
      network.getStructure().finalizeStructure();
      network.reset();

      
      int inLayers = nodes[0];
      int outLayers = nodes[nodes.length - 1];
      Reader raw = new Reader("TrainingData/" + fileName + ".txt", inLayers, outLayers);
      
      int cutoff = (int)(raw.input.length*0.8);
      double[][] MLInput =  new double[cutoff][inLayers];
      double[][] MLOutput = new double[cutoff][outLayers];
      double[][] CrossInput = new double[raw.input.length - cutoff][inLayers];
      double[][] CrossOutput =  new double[raw.input.length -cutoff][outLayers];
      
      
      int black = 0;
      int white = 0;

      System.out.println(cutoff);
      for(int i = 0; i < cutoff; i++)
      {
         for(int j = 0; j < inLayers; j++)
         {
            MLInput[i][j] = raw.input[i][j];
            
         }
         for(int j = 0; j < outLayers; j++)
         {
            MLOutput[i][j] = raw.output[i][j];
            if (raw.output[i][j] > 0.9){
               black++;
            }
            else if(raw.output[i][j] < 0.1){
               white++;
            }


         }
      }
      for(int i = cutoff; i < raw.input.length; i++)
      {
         for(int j = 0; j < inLayers; j++)
         {
            CrossInput[i - cutoff][j] = raw.input[i][j];
         }
         for(int j = 0; j < outLayers; j++)
         {
            CrossOutput[i - cutoff][j] = raw.output[i][j];
            if (raw.output[i][j] > 0.9){
               black++;
            }
            else if(raw.output[i][j] < 0.1){
               white++;
            }
         }

      }
     /* System.out.println("Training:");
      for(int i = 0; i < MLInput.length; i++)
      {
         System.out.println(MLInput[i][0] + " " + MLInput[i][1] + " : " + MLOutput[i][0]);
      }
      System.out.println("Cross:");
      for(int i = 0; i < CrossInput.length; i++)
      {
         System.out.println(CrossInput[i][0] + " " + CrossInput[i][1] + " : " + CrossOutput[i][0]);
      }*/

      MLDataSet trainingSet = new BasicMLDataSet (MLInput, MLOutput);
      MLDataSet crossSet = new BasicMLDataSet(CrossInput, CrossOutput);
      
      final ResilientPropagation train = new ResilientPropagation (network, trainingSet);
      RegularizationStrategy reg = new RegularizationStrategy(lambda);
      reg.init(train);
      
      
       BasicNetwork Bestnetwork = new BasicNetwork();

      double threshold = 0.01;
      
      
      double minimum = 10.0;
      int epoch = 1;
      boolean flag = true;
      int count = 0;
      double prevThreshold = 10.0;
      int epochMemory = -1;
      do
      {
      count++;
      reg.preIteration();
      train.iteration();
      reg.postIteration();
      System.out.println("Epoch #" + epoch + "  Error : " + train.getError()) ;
      epoch ++;
      double temp = network.calculateError(crossSet);
      if (temp < minimum){
         minimum = temp;
         Bestnetwork = (BasicNetwork)network.clone();
         if (Bestnetwork.calculateError(crossSet) - minimum > 0.001)
         {
            System.out.println("false");
         }
         System.out.println("New Best!");
         epochMemory = epoch;
         count = 0;
      }
      /*if (prevThreshold - train.getError() < threshold*2){
         count++;
      }
      else{
         prevThreshold = train.getError();
         count = 0;
      }*/
      if(count > 30){
         flag = false;
      }
      } while(train.getError() > threshold && flag);
      train.finishTraining();
      network = Bestnetwork;
      int total = 0;
      int correct = 0;
      for(MLDataPair pair: crossSet)
      {
         final MLData output = network.compute(pair.getInput());
       //System.out.println("actual= " + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
         total++;
         if (pair.getIdeal().getData(0) < 0.001 && output.getData(0) < 0.3)
         {
            correct++;
         }
         else if (pair.getIdeal().getData(0) > 1 - 0.001 && output.getData(0) > 0.7)
         {
            correct++;
         }
         else if (pair.getIdeal().getData(0) < 0.51 && 0.49 < pair.getIdeal().getData(0) && output.getData(0) < 0.7 && 0.3 < output.getData(0))
         {
            correct++;
         }
      }
      
      System.out.println("Cross-Validation Error: " + network.calculateError(crossSet));
      System.out.println("Maximum: " + minimum);
      System.out.println(correct + "/" + total);
      Encog.getInstance().shutdown();
      System.out.println(epochMemory);
      System.out.println(black + " " + white);
      saveObject(new File( "AIs/"+ AIName+ ".eg"), network);
   }

}