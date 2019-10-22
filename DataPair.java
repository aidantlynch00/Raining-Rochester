import java.util.Arrays;

public class DataPair {

    private double[] x;
    private double[] y;

    public DataPair(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + Arrays.toString(x) + ", " + Arrays.toString(y) + ")";
    }
}