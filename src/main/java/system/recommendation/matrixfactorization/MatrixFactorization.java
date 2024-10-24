package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.*;

public class MatrixFactorization extends Chromosome{
    private final double regularization;
    private final double learningRate;
    private final double[][] users;
    private final double[][] movies;
    private final RatingService<User> userService;

    public MatrixFactorization(RatingService<User> userService, int k, double learningRate, double regularization) {
        int u = userService.getUsers().size();
        int m = userService.getMovies().size();
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.users = new double[u][k];
        this.movies = new double[m][k];
        this.userService = userService;

        for (int i = 0; i < u; i++) {
            users[i] = initLatentFeatures(k);
        }

        for (int i = 0; i < m; i++) {
            movies[i] = initLatentFeatures(k);
        }
    }

    public MatrixFactorization(double[][] users, double[][] movies, double learningRate, double regularization, RatingService<User> userService){
        this.users = users;
        this.movies = movies;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.userService = userService;
    }

    public double[][] getMovies(){
        return movies;
    }

    public double[][] getUsers(){
        return users;
    }

    public double[][] getPredictedRating(){
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                predicted[i][j] = vectorMultiplication(users[i], movies[j]);
            }
        }
        return predicted;
    }

    private void sgd_step(){
        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey();
                double rating = entry.getValue();
                double[] uf = this.users[u];
                double[] mf = this.movies[mid-1];
                double e = rating - vectorMultiplication(uf, mf);
                for(int k = 0; k < uf.length; k++){
                    double uk = uf[k] + this.learningRate*(e*mf[k]-this.regularization*uf[k]);
                    double mk = mf[k] + this.learningRate*(e*uf[k]-this.regularization*mf[k]);
                    uf[k] = uk;
                    mf[k] = mk;
                }
            }
        }
    }

    public void sgd(int epochs){
        for(int i = 0; i < epochs; i++) {
            System.out.println("EPOCH " + i);
            sgd_step();
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

    @Override
    protected double _fitness() {
        double sum = 0;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey();
                double rating = entry.getValue();
                double[] uf = this.users[u];
                double[] mf = this.movies[mid-1];
                sum +=  Math.pow(rating - vectorMultiplication(uf, mf),2);
            }
        }

        return sum;
    }

    @Override
    public Chromosome mutate() {
        double[][] u = this.users.clone();
        double[][] m = this.movies.clone();
        MatrixFactorization mutated = new MatrixFactorization(u,m,learningRate,regularization,userService);
        mutated.sgd_step();
        return mutated;
    }

    @Override
    public List<Chromosome> crossover(Chromosome p){
        Random random = new Random();
        int usize = this.users.length;
        int msize = this.movies.length;
        int k = this.users[0].length;
        int u = random.nextInt(this.users.length);
        int m = random.nextInt(this.movies.length);

        double[][] pusers = ((MatrixFactorization) p).getUsers();
        double[][] pmovies = ((MatrixFactorization) p).getMovies();

        double[][] u1 = new double[usize][k];
        double[][] m1 = new double[msize][k];
        double[][] u2 = new double[usize][k];
        double[][] m2 = new double[msize][k];

        for(int i = 0; i < u + 1; i++){
            u1[i] = this.users[i].clone();
            u2[i] = pusers[i].clone();
        }

        for(int i = u + 1; i < this.users.length; i++){
            u1[i] = pusers[i].clone();
            u2[i] = this.users[i].clone();
        }

        for(int i = 0; i < m; i++){
            m1[i] = pmovies[i].clone();
            m2[i] = this.movies[i].clone();
        }

        for(int i = m; i < this.movies.length; i++){
            m1[i] = this.movies[i].clone();
            m2[i] = pmovies[i].clone();
        }

        return List.of(new MatrixFactorization(u1,m1,learningRate,regularization,userService),new MatrixFactorization(u2,m2,learningRate,regularization,userService));
    }
}
