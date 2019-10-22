import java.util.function.DoubleFunction;

public class Matrix {

    public int rows, cols;
    public double[][] data;

    public Matrix(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                 data[r][c] = 0;
            }
        }
    }

    public void scale(double n){
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] *= n;
            }
        }
    }

    public void mult(Matrix m) throws IllegalArgumentException{
        try {
            Matrix result = mult(this, m);
            rows = result.rows;
            cols = result.cols;
            data = result.data;
        }
        catch(IllegalArgumentException e){
            throw e;
        }
    }

    public static Matrix mult(Matrix a, Matrix b) throws IllegalArgumentException{
        if(a.cols != b.rows) throw new IllegalArgumentException();

        Matrix result = new Matrix(a.rows, b.cols);

        for(int r = 0; r < result.rows; r++) {
            for (int c = 0; c < result.cols; c++) {

                double sum = 0;
                for(int d = 0; d < a.cols; d++){
                    sum += a.data[r][d] * b.data[d][c];
                }

                result.data[r][c] = sum;
            }
        }

        return result;
    }

    public static Matrix transpose(Matrix m){
        Matrix result = new Matrix(m.cols, m.rows);

        for(int r = 0; r < m.rows; r++){
            for(int c = 0; c < m.cols; c++){
                result.data[c][r] = m.data[r][c];
            }
        }

        return result;
    }

    public void add(double n){
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] += n;
            }
        }
    }

    public void add(Matrix m) throws IllegalArgumentException{
        if(m.rows != rows || m.cols != cols) throw new IllegalArgumentException();

        double[][] other = m.data;
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] += other[r][c];
            }
        }
    }

    public static Matrix subtract(Matrix a, Matrix b){
        Matrix result = new Matrix(a.rows, a.cols);
        for(int r = 0; r < result.rows; r++){
            for(int c = 0; c < result.cols; c++){
                result.data[r][c] = a.data[r][c] - b.data[r][c];
            }
        }

        return result;
    }

    public void randomize(){
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] = (Math.random() * 2) - 1;
            }
        }
    }

    public static Matrix fromArray(double[] arr){
        Matrix m = new Matrix(arr.length, 1);

        for(int i = 0; i < arr.length; i++){
            m.data[i][0] = arr[i];
        }

        return m;
    }

    public double[] toArray(){
        double[] arr = new double[rows];
        for(int i = 0; i < rows; i++) {
            arr[i] = data[i][0];
        }

        return arr;
    }

    public void map(DoubleFunction<Double> func){
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                data[r][c] = func.apply(data[r][c]);
            }
        }
    }

    public static Matrix map(Matrix m, DoubleFunction<Double> func){
        m.map(func);
        return m;
    }

    public void print(){
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < cols; c++){
                System.out.print(data[r][c] + " ");
            }
            System.out.println("\n");
        }
    }
}
