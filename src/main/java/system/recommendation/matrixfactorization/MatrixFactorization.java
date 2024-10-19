package system.recommendation.matrixfactorization;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;

public class MatrixFactorization{
    private final Map<Integer, User> users;
    private final Map<Integer, Movie> movies;
    private final double learningRate;
    private final double regularization;
    private final int epochs;

    public MatrixFactorization(DatasetLoader datasetLoader, double learningRate, double regularization, int epochs) {
        this.users = datasetLoader.getUsers();
        this.movies = datasetLoader.getMovies();
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.epochs = epochs;
    }

    public void sgd(){
        System.out.println("SGD start");
        for(int i = 0; i < this.epochs; i++){
            System.out.println("Epoch " + i);
            users.forEach((_, user) -> {
                Map<Integer, Double> ratings = user.getRatings();
                ratings.forEach((mid, rating) -> {
                    Movie movie = movies.get(mid);
                    double[] uf = user.getLatentFeatures();
                    double[] mf = movie.getLatentFeatures();
                    double e = rating - vectorMultiplication(uf, mf);
                    updateLatentFeatures(e, uf, mf);
                    updateLatentFeatures(e, mf, uf);
                });
            });
        }
        System.out.println("SGD done");
    }

    private void updateLatentFeatures(double error, double[] f1, double[] f2){
        double[] c1 = vectorMultiplication(f1, this.regularization);
        double[] c2 = vectorMultiplication(f2, error);

        for(int i = 0; i < c2.length; i++){
            c2[i] = c2[i] - c1[i];
        }

        c2 = vectorMultiplication(c2, this.learningRate);

        for(int i = 0; i < f1.length; i++){
            f1[i] = f1[i] + c2[i];
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

    private double[] vectorMultiplication(double[] f1, double a) {
        double[] c = f1.clone();
        for(int i = 0; i < f1.length; i++){
            c[i] = c[i] * a;
        }
        return c;
    }
}
