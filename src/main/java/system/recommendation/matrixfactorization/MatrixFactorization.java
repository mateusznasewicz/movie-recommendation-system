package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.*;

public abstract class MatrixFactorization{
    protected final double regularization;
    protected final double learningRate;
    protected  double[][] users;
    protected  double[][] movies;
    protected final RatingService<User> userService;

    public MatrixFactorization(RatingService<User> userService, int features, double learningRate, double regularization) {
        int u = userService.getUsers().size();
        int m = userService.getMovies().size();
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.users = new double[u][features];
        this.movies = new double[m][features];
        this.userService = userService;

        for (int i = 0; i < u; i++) {
            users[i] = initLatentFeatures(features);
        }

        for (int i = 0; i < m; i++) {
            movies[i] = initLatentFeatures(features);
        }
    }

    public double[][] getPredictedRatings(){
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                predicted[i][j] = vectorMultiplication(users[i], movies[j]);
            }
        }
        return predicted;
    }

    protected abstract void sgd_step();
    protected abstract void calcLoss();

    public void sgd(int epochs){
        for(int i = 0; i < epochs; i++) {
            System.out.println("EPOCH " + i);
            sgd_step();
            //calcLoss();
        }
    }

    public static double vectorMultiplication(double[] f1, double[] f2) {
        if(f1.length != f2.length) return Double.MIN_VALUE;
        double sum = 0;

        for(int i = 0; i < f1.length; i++){
            sum += f1[i] * f2[i];
        }

        return sum;
    }

    public static double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        double[][] result = new double[rowsA][colsA];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for(int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    public static double[][] multiplyMatrix(double[][] matrix, double a){
        int rowsA = matrix.length;
        int colsA = matrix[0].length;

        double[][] result = new double[rowsA][colsA];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsA; j++) {
                result[i][j] += matrix[i][j] * a;
            }
        }

        return result;
    }

    public static double[][] addMatrices(double[][] matrixA, double[][] matrixB) {
        int rows = matrixA.length;
        int cols = matrixA[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }

        return result;
    }

    public static double[][] subMatrices(double[][] matrixA, double[][] matrixB) {
        int rows = matrixA.length;
        int cols = matrixA[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }

        return result;
    }

    private double[] initLatentFeatures(int k) {
        double[] latentFeatures = new double[k];
        for(int i = 0; i < k; i++){
            Random random = new Random();
            double mean = 0.0;
            double stdDev = 0.01;
            latentFeatures[i] = mean + stdDev * random.nextGaussian();
        }
        return latentFeatures;
    }
}
