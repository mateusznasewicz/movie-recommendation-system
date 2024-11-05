package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.*;

public abstract class MatrixFactorization implements Particle{
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
        double weight = gradientWeight*learningRate;
        for(int i = 0; i< users.length; i++){
            for(int f = 0; f < users[0].length; f++){
                users[i][f] -= weight*regularization*old_users[i][f];
            }
        }
        for(int i = 0; i< movies.length; i++){
            for(int f = 0; f < movies[0].length; f++){
                movies[i][f] -= weight*regularization*old_movies[i][f];
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
