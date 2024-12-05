package system.recommendation.matrixfactorization;

import system.recommendation.QualityMeasure;
import system.recommendation.Utils;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class MatrixFactorization{
    protected final double regularization;
    protected double learningRate;
    protected double[][] users;
    protected double[][] movies;
    protected final RatingService<User, Movie> userService;
    protected final SplittableRandom rand = new SplittableRandom();

    public MatrixFactorization(RatingService<User,Movie> userService, int features, double learningRate, double regularization, boolean nonNegative, double stdDev) {
        int u = userService.getEntityMap().size();
        int m = userService.getItemMap().size();
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.users = new double[u][features];
        this.movies = new double[m][features];
        this.userService = userService;

        for (int i = 0; i < u; i++) {
            users[i] = initLatentFeatures(features, nonNegative,stdDev);
        }

        for (int i = 0; i < m; i++) {
            movies[i] = initLatentFeatures(features, nonNegative,stdDev);
        }
    }

    public MatrixFactorization(RatingService<User,Movie> userService, int features, double learningRate,double stdDev){
        this(userService, features, learningRate, 0, true, stdDev);
    }

    public MatrixFactorization(double[][] users, double[][] movies, double learningRate, double regularization, RatingService<User,Movie> userService){
        this.users = users;
        this.movies = movies;
        this.regularization = regularization;
        this.learningRate = learningRate;
        this.userService = userService;
    }

    public MatrixFactorization(double[][] users, double[][] movies, double learningRate, RatingService<User,Movie> userService){
        this.users = users;
        this.movies = movies;
        this.learningRate = learningRate;
        this.userService = userService;
        this.regularization = 0;
    }

    public double[][] getMovies() {
        return movies;
    }

    public double[][] getUsers() {
        return users;
    }

    public abstract double[][] getPredictedRatings();
    protected abstract void gd_step();

    public void gd(int epochs){
        for(int i = 0; i < epochs; i++) {
            gd_step();
            System.out.println(i);
        }
    }

    protected void regularizationGradient(double[][] old_users, double[][] old_movies, double gradientWeight){
        double weight = gradientWeight*learningRate*regularization;
        for(int i = 0; i< movies.length; i++){
            for(int f = 0; f < movies[0].length; f++){
                movies[i][f] -= weight*old_movies[i][f];
            }
        }
    }

    protected void euclideanGradient(double[][] old_users, double[][] old_movies, double gradientWeight){
        for(int u = 0; u < users.length; u++){
            User entity = userService.getEntity(u+1);
            Map<Integer, Double> ratings = entity.getRatings();
            for(int f = 0; f < users[0].length; f++){
                for(Map.Entry<Integer, Double> rating : ratings.entrySet()){
                    double r = rating.getValue();
                    int m = rating.getKey()-1;
                    double predicted = vectorMultiplication(users[u],movies[m]);
                    double weight = gradientWeight*2*learningRate;
                    users[u][f] += weight*old_movies[m][f]*(r-predicted);
                    movies[m][f] += weight*old_users[u][f]*(r-predicted);
                }
                users[u][f] -= learningRate*regularization*old_users[u][f];
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

        return loss*this.regularization/2;
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

    private double[] initLatentFeatures(int k, boolean nonNegative, double stdDev) {
        SplittableRandom random = new SplittableRandom();
        double[] latentFeatures = new double[k];
        double bound = 1/Math.sqrt(k);
        for(int i = 0; i < k; i++){
            latentFeatures[i] = random.nextDouble();
            if(nonNegative){
                latentFeatures[i] = Math.abs(latentFeatures[i]);
            }
        }
        return latentFeatures;
    }
}
