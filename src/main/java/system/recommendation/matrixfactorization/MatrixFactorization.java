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

    public abstract double[][] getPredictedRatings();
    protected abstract void sgd_step();

    public void sgd(int epochs){
        for(int i = 0; i < epochs; i++) {
            System.out.println("EPOCH " + i);
            sgd_step();
            //calcLoss();
        }
    }

    protected void regularizationGradient(double[][] old_users, double[][] old_movies){
        for(int i = 0; i< users.length; i++){
            for(int f = 0; f < users[0].length; f++){
                users[i][f] -= learningRate*regularization*old_users[i][f];
            }
        }
        for(int i = 0; i< movies.length; i++){
            for(int f = 0; f < movies[0].length; f++){
                movies[i][f] -= learningRate*regularization*old_movies[i][f];
            }
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
