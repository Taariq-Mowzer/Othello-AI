import org.encog.Encog ;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.flat.FlatNetwork;
import org.encog.neural.flat.FlatLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import static org.encog.persist.EncogDirectoryPersistence.*;
import java.io.*;

public class HelloWorldFlat
{
 public static double XOR_INPUT[][] = {{0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}, {1.0, 1.0}};
 public static double XOR_IDEAL[][] = {{0.0}, {1.0}, {1.0}, {0.0}};
 
 public static void main(String[] args)
 {
   FlatLayer[] layers = new FlatLayer[3];
   layers[0] = new BasicLayer(null, true,  2);
   layers[1] = new BasicLayer(new ActivationSigmoid(), true, 3);
   layers[2] = new BasicLayer(new ActivationSigmoid(), false, 1);
   String filename = "saveTest.eg";
   FlatNetwork network = new FlatNetwork(layers);
   
   //loads in network
   //network = (BasicNetwork) loadObject(new File(filename));
   
   MLDataSet trainingSet = new BasicMLDataSet (XOR_INPUT, XOR_IDEAL);
   final ResilientPropagation train = new ResilientPropagation (network, trainingSet);
   int epoch = 1;
   do
   {
      train.iteration();
      System.out.println("Epoch #" + epoch + "  Error : " + train.getError()) ;
      epoch ++;
   } while(train.getError() > 0.01);
   train.finishTraining();
   System.out.println("Neural Network Results: " );
   for(MLDataPair pair: trainingSet)
   {
      final MLData output = network.compute(pair.getInput());
      System.out.println(pair.getInput().getData(0) + ", " + pair.getInput().getData(1)
      + ", actual= " + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
   }
   Encog.getInstance().shutdown();
   
   //saves network
   saveObject(new File(filename), network);
 }  
}