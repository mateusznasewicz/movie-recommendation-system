package system.recommendation.matrixfactorization;

public class MatrixFactorization{
    private final double[][] U;
    private final double[][] V;
    private final double[][] baseMatrix;

    public MatrixFactorization(double[][] baseMatrix, int features) {
        int users = baseMatrix.length;
        int movies = baseMatrix[0].length;

        this.U = new double[users][features];
        this.V = new double[movies][features];
        this.baseMatrix = baseMatrix;
    }

    private void sgd(){

    }

    private double[] vectorMultiplication(){

    }
}
