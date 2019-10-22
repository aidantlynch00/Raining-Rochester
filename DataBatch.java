import java.util.ArrayList;

public class DataBatch {

    ArrayList<DataPair> pairs;
    int index;

    public DataBatch(ArrayList<DataPair> pairs) {
        this.pairs = pairs;
        index = 0;
    }

    public boolean hasNext() {
        return index < pairs.size();
    }

    public DataPair next() {
        return pairs.get(index++);
    }

    public ArrayList<DataPair> getPairs() {
        return pairs;
    }
}