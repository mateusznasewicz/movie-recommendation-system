package system.recommendation.matrixfactorization;

import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.*;

public abstract class MatrixFactorization implements Particle{
    protected final double regularization;
    protected final double learningRate;
    protected  double[][] users;
    protected  double[][] movies;
    protected final RatingService<User, Movie> userService;

    public MatrixFactorization(RatingService<User,Movie> userService, int features, double learningRate, double regularization, boolean nonNegative) {
        int u = userService.getEntityMap().size();
        int m = userService.getItemMap().size();
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.users = new double[u][features];
        this.movies = new double[m][features];
        this.userService = userService;

        for (int i = 0; i < u; i++) {
            users[i] = initLatentFeatures(features, nonNegative);
        }

        for (int i = 0; i < m; i++) {
            movies[i] = initLatentFeatures(features, nonNegative);
        }
    }

    public MatrixFactorization(RatingService<User,Movie> userService, int features, double learningRate){
        this(userService, features, learningRate, 0, true);
    }

    public abstract double[][] getPredictedRatings();
    protected abstract void gd_step();
    protected abstract double calcLoss();

    @Override
    public double getLoss() {
        return regularizationLoss() + calcLoss();
    }

    public void gd(int epochs){
        for(int i = 0; i < epochs; i++) {
            System.out.println("EPOCH " + i);
            gd_step();
        }
    }

    protected void regularizationGradient(double[][] old_users, double[][] old_movies, double gradientWeight){
        double weight = gradientWeight*learningRate*regularization;
        for(int i = 0; i< users.length; i++){
            for(int f = 0; f < users[0].length; f++){
                users[i][f] -= weight*old_users[i][f];
            }
        }
        for(int i = 0; i< movies.length; i++){
            for(int f = 0; f < movies[0].length; f++){
                movies[i][f] -= weight*old_movies[i][f];
            }
        }
    }

    protected double regularizationLoss(){
        double loss = 0;

        for (double[] m : movies) {
            for (double val : m) {
                loss += val * val;
            }
        }

        for (double[] u : users) {
            for (double val : u) {
                loss += val * val;
            }
        }

        return loss*this.regularization;
    }

    public static double vectorMultiplication(double[] f1, double[] f2) {
        if(f1.length != f2.length) return Double.MIN_VALUE;
        double sum = 0;

        for(int i = 0; i < f1.length; i++){
            sum += f1[i] * f2[i];
        }

        return sum;
    }

    protected double[][] multiplyFactorizedMatrices(){
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                predicted[i][j] = vectorMultiplication(users[i], movies[j]);
            }
        }
        return predicted;
    }

    private double[] initLatentFeatures(int k, boolean nonNegative) {
        double[] latentFeatures = new double[k];
        for(int i = 0; i < k; i++){
            Random random = new Random();
            double mean = 0.0;
            double stdDev = 0.01;
            latentFeatures[i] = mean + stdDev * random.nextGaussian();
            if(nonNegative){
                latentFeatures[i] = Math.abs(latentFeatures[i]);
            }
        }
        return latentFeatures;
    }
}
