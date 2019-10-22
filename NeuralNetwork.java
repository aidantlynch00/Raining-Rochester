public class NeuralNetwork {

    public static final int SIGMOID = 0, TANH = 1, RELU = 2, THRESHOLD = 3;
    public int activationType;

    public static double learningRate = .1;

    private static int inputWeightsIndex, outputWeightsIndex;
    public int numInputs, numHiddenLayers, numHiddenNodesPerLayer, numOutputs;
    private Matrix[] weights, biases, hiddenLayerValues;


    public NeuralNetwork(int numInputs, int numHiddenLayers, int numHiddenNodesPerLayer, int numOutputs, String type){
        this.numInputs = numInputs;
        this.numHiddenLayers = numHiddenLayers;
        this.numHiddenNodesPerLayer = numHiddenNodesPerLayer;
        this.numOutputs = numOutputs;
        inputWeightsIndex = 0;
        outputWeightsIndex = numHiddenLayers;

        weights = new Matrix[numHiddenLayers + 1];
        biases  = new Matrix[numHiddenLayers + 1];
        hiddenLayerValues = new Matrix[numHiddenLayers];

        weights[inputWeightsIndex] = new Matrix(numHiddenNodesPerLayer, numInputs);
        for(int i = 1; i < weights.length - 1; i++){ //starting with the first true hidden layer, going until the output weights
            weights[i] = new Matrix(numHiddenNodesPerLayer, numHiddenNodesPerLayer);
        }
        weights[outputWeightsIndex] = new Matrix(numOutputs, numHiddenNodesPerLayer);

        for(int i = 0; i < biases.length - 1; i++){
            biases[i] = new Matrix(numHiddenNodesPerLayer, 1);
        }
        biases[outputWeightsIndex] = new Matrix(numOutputs, 1);

        for(Matrix m : weights){
            m.randomize();
        }

        for(Matrix m : biases){
            m.randomize();
        }

        if(type.equals("SIGMOID")){
            activationType = SIGMOID;
        }
        else if(type.equals("TANH")){
            activationType = TANH;
        }
        else if(type.equals("RELU")){
            activationType = RELU;
        }
        else if(type.equals("THRESHOLD")){
            activationType = THRESHOLD;
        }
    }

    public double[] feedForward(double[] input){

        Matrix inputLayer = Matrix.fromArray(input);
        hiddenLayerValues[0] = Matrix.mult(weights[inputWeightsIndex], inputLayer);
        hiddenLayerValues[0].add(biases[inputWeightsIndex]);
        hiddenLayerValues[0].map(this::activationFunction);

        for(int i = 1; i < hiddenLayerValues.length; i++){
            hiddenLayerValues[i] = Matrix.mult(weights[i], hiddenLayerValues[i - 1]);
            hiddenLayerValues[i].add(biases[i]);
            hiddenLayerValues[i].map(this::activationFunction);
        }

        Matrix outputs = Matrix.mult(weights[outputWeightsIndex], hiddenLayerValues[numHiddenLayers - 1]);
        outputs.add(biases[outputWeightsIndex]);
        outputs.map(this::activationFunction);
        return outputs.toArray();
    }

    public void train(double[] inputs, double[] targetOutputs){
        //Backpropagating error
        double[] observedOutputs = feedForward(inputs);
        //System.out.println("OBSERVED OUTPUTS: " + observedOutputs[0]);

        Matrix output_errors = Matrix.subtract(Matrix.fromArray(targetOutputs), Matrix.fromArray(observedOutputs));
        Matrix[] weights_transposed = new Matrix[weights.length];

        for(int i = 0; i < weights_transposed.length; i++){
            weights_transposed[i] = Matrix.transpose(weights[i]);
        }

        Matrix[] hidden_errors = new Matrix[hiddenLayerValues.length];
        hidden_errors[hidden_errors.length - 1] = Matrix.mult(weights_transposed[outputWeightsIndex], output_errors);
        for(int i = hidden_errors.length - 2; i >= 0; i--){
            hidden_errors[i] = Matrix.mult(weights_transposed[i + 1], hidden_errors[i + 1]);
        }

        //System.out.println("HIDDEN ERRORS");
        for(int i = 0; i < hidden_errors.length; i++){
           // hidden_errors[i].print();
        }
        //Changing weights with gradient descent

        //init change in weights to 0
        Matrix[] deltaWeights = new Matrix[weights.length];
        for(int i = 0; i < deltaWeights.length; i++){
            deltaWeights[i] = new Matrix(weights[i].rows, weights[i].cols);
            for(int r = 0; r < deltaWeights[i].rows; r++){
                for(int c = 0; c < deltaWeights[i].cols; c++){
                    deltaWeights[i].data[r][c] = 0;
                }
            }
        }

        Matrix outputs = Matrix.fromArray(observedOutputs);

        for(int i = 0; i < deltaWeights.length; i++){

            for(int r = 0; r < deltaWeights[i].rows; r++){
                for(int c = 0; c < deltaWeights[i].cols; c++){

                    double error, layerDerivative;
                    Matrix previousT;
                    if(i == deltaWeights.length - 1){
                        error = output_errors.data[r][0];
                        //System.out.println("LAYER VALUE: " + outputs.data[r][0]);
                        layerDerivative = dsigmoid_alreadyAdjusted(outputs.data[r][0]);
                        previousT = Matrix.transpose(hiddenLayerValues[i - 1]);
                    }
                    else if(i == 0){
                        error = hidden_errors[i].data[r][0];
                        //System.out.println("LAYER VALUE: " + hiddenLayerValues[i].data[r][0]);
                        layerDerivative = dsigmoid_alreadyAdjusted(hiddenLayerValues[i].data[r][0]);
                        previousT = Matrix.transpose(Matrix.fromArray(inputs));
                    }
                    else{
                        error = hidden_errors[i].data[r][0];
                        //System.out.println("LAYER VALUE: " + hiddenLayerValues[i].data[r][0]);
                        layerDerivative = dsigmoid_alreadyAdjusted(hiddenLayerValues[i].data[r][0]);
                        previousT = Matrix.transpose(hiddenLayerValues[i - 1]);
                    }

                    //System.out.println("LEARNING RATE: " + learningRate + ", ERROR: " + error + ", LAYER VALUE DERIVATIVE: " + layerDerivative);
                    deltaWeights[i].data[r][c] = learningRate * error * layerDerivative * previousT.data[0][c];
                }
            }
        }

        //System.out.println("DELTA WEIGHTS");
        for(int i = 0; i < weights.length; i++) {
            //deltaWeights[i].print();
            //System.out.println();
            weights[i].add(deltaWeights[i]);
        }
    }

    private double activationFunction(double x){
        if(activationType == SIGMOID)
            return sigmoid(x);
        else if(activationType == TANH)
            return tanh(x);
        else if(activationType == RELU)
            return ReLU(x);
        else
            return threshold(x);
    }

    /*
    private double acivationFunctionDerivative(double x){
        if(activationType == SIGMOID)
            return dsigmoid(x);
        else if(activationType == TANH)
            return dtanh(x);
        else if(activationType == RELU)
            return dReLU(x);
        else
            return dthreshold(x);
    }
    */

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    private double dsigmoid(double x) {
        double y = sigmoid(x);
        return y * (1 - y);
    }
    private double dsigmoid_alreadyAdjusted(double x){
        return x * (1 - x);
    }



    private double tanh(double x){
        return Math.tanh(x);
    }
    /*
    private double dtanh(double x){

    }
    private double dtanh_alreadyAdjusted(double x){

    }*/



    private double ReLU(double x){
        return Math.max(x, 0);
    }
    /*
    private double dReLU(double x){

    }
    private double dReLU_alreadyAdjusted(double x){

    }*/



    private double threshold(double x) { return (x <= 0)? 0 : 1; }
    /*
    private double dthreshold(double x){

    }
    private double dthreshold_alreadyAdjusted(double x){

    }*/
}




/*
    This is a pile of unused code that was either for reference or did not work
    RIP Neural Network reject code


        for(int i = 0; i < deltaWeights.length; i++) {
            Matrix layerDerivative, layerErrors, previousT;
            if(i == 0){
                layerDerivative = Matrix.map(hiddenLayerValues[i], this::dsigmoid_alreadyAdjusted);
                layerErrors = hidden_errors[i];
                previousT = Matrix.transpose( Matrix.fromArray(inputs) );
            }
            else if(i == deltaWeights.length - 1){
                layerDerivative = Matrix.map(outputs, this::dsigmoid_alreadyAdjusted);
                layerErrors = output_errors;
                previousT = Matrix.transpose(hiddenLayerValues[i - 1]);
            }
            else{
                layerDerivative = Matrix.map(hiddenLayerValues[i], this::dsigmoid_alreadyAdjusted);
                layerErrors = hidden_errors[i];
                previousT = Matrix.transpose(hiddenLayerValues[i - 1]);
            }

            layerDerivative.mult(layerErrors);
            layerDerivative.scale(learningRate);
            deltaWeights[i] = Matrix.mult(layerDerivative, previousT);
            weights[i].add(deltaWeights[i]);
        }



        Matrix input_layer = Matrix.fromArray(input);
        Matrix hidden_layer = Matrix.mult(weights_input2hidden, input_layer);
        hidden_layer.add(hidden_bias);
        hidden_layer.map(this::activationFunction);

        Matrix outputs = Matrix.mult(weights_hidden2output, hidden_layer);
        outputs.add(output_bias);
        outputs.map(this::activationFunction);

        return outputs.toArray();


        weights_input2hidden = new Matrix(this.num_hidden, this.num_inputs);
        weights_hidden2output = new Matrix(this.num_outputs, this.num_hidden);

        weights_input2hidden.randomize();
        weights_hidden2output.randomize();

        hidden_bias = new Matrix(num_hidden, 1);
        output_bias = new Matrix(num_outputs, 1);
        hidden_bias.randomize();
        output_bias.randomize();


 */
