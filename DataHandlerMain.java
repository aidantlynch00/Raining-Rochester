import java.util.ArrayList;

public class DataHandlerMain{

    public static void main(String[] args) {

        ArrayList<DataPuller> pullers = new ArrayList<>();
        ArrayList<DataBatch> batches = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            DataPuller puller = new DataPuller("data/batches/x_batch_" + i + ".txt",
                                               "data/batches/y_batch_" + i + ".txt");
            puller.start();
            pullers.add(puller);
        }

        for (DataPuller puller : pullers) {
            try{
                puller.join();
                batches.add(puller.getBatch());
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        NeuralNetwork nn = new NeuralNetwork(9, 3, 11, 1, "SIGMOID");

        for (DataBatch batch : batches.subList(0, batches.size() - 1)) {
            while (batch.hasNext()) {
                DataPair pair = batch.next();
                nn.train(pair.getX(), pair.getY());
            }
        }

        int correct = 0;
        double threshold = .25;
        DataBatch test = batches.get(batches.size() - 1);

        while (test.hasNext()) {
            DataPair pair = test.next();
            double result = nn.feedForward(pair.getX())[0];
            double answer = pair.getY()[0];
            
            System.out.println(result + " - " + answer + " = " + Math.abs(result - answer));
            if (Math.abs(result - answer) <= threshold) correct++;
        }

        System.out.println("Percent correct: " + correct / test.getPairs().size());
    }
}