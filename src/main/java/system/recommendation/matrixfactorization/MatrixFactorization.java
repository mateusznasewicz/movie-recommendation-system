package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.*;

public abstract class MatrixFactorization{
    protected final double regularization;
    protected final double learningRate;
    protected final double[][] users;
    protected final double[][] movies;
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
            //System.out.println("EPOCH " + i);
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
