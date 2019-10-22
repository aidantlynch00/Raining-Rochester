import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataPuller extends Thread {

    private String xFile, yFile;
    private DataBatch batch;

    public DataPuller(String xFile, String yFile){
        this.xFile = xFile;
        this.yFile = yFile;
    }

    @Override
    public void run() {
        try{
            readFiles(xFile, yFile);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFiles(String xFile, String yFile) throws FileNotFoundException, IOException {
        ArrayList<double[]> xList = new ArrayList<>();
        ArrayList<double[]> yList = new ArrayList<>();
        ArrayList<DataPair> pairs = new ArrayList<>();

        BufferedReader xbr = new BufferedReader(new FileReader(new File(xFile)));
        String xLine;
        while((xLine = xbr.readLine()) != null) {
            String[] numberStrings = xLine.split(", ");
            double[] numbers = new double[numberStrings.length];

            for (int i = 0; i < numberStrings.length; i++) {
                numbers[i] = Double.parseDouble(numberStrings[i]);
            }

            xList.add(numbers);
        }

        BufferedReader ybr = new BufferedReader(new FileReader(new File(yFile)));
        String yLine;
        while((yLine = ybr.readLine()) != null) {
            double[] y = {Double.parseDouble(yLine)};
            yList.add(y);
        }

        for (int i = 0; i < xList.size(); i++) {
            DataPair pair = new DataPair(xList.get(i), yList.get(i));
            pairs.add(pair);
        }

        batch = new DataBatch(pairs);

        xbr.close();
        ybr.close();
    }

    public synchronized DataBatch getBatch() {
        return batch;
    }
}