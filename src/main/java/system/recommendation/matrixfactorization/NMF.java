package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class NMF extends MatrixFactorization implements Chromosome {
    private final SplittableRandom rand = new SplittableRandom();

    public NMF(RatingService<User, Movie> userService, int features, double learningRate, double stdDev) {
        super(userService, features, learningRate, stdDev);
    }

    public NMF(double[][] users, double[][] movies, double learningRate, RatingService<User, Movie> userService) {
        super(users,movies,learningRate,userService);
    }

    @Override
    public double[][] getPredictedRatings() {
        return multiplyFactorizedMatrices();
    }

    @Override
    protected void gd_step() {
        double[][] old_users = users.clone();
        double[][] old_movies = movies.clone();

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                double rating = entry.getValue();
                double predicted = vectorMultiplication(old_users[u], old_movies[mid]);
                double ratingToPredicted = rating/predicted;
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += learningRate*old_movies[mid][f]*ratingToPredicted;
                    movies[mid][f] += learningRate*old_users[u][f]*ratingToPredicted;

                    users[u][f] -= learningRate*old_movies[mid][f];
                    movies[mid][f] -= learningRate*old_users[u][f];
                }
            }
        }
    }

    @Override
    public void mutate(double chance) {
        if(rand.nextDouble() >= chance)return;
        gd_step();
    }

    @Override
    public double fitness() {
        double fit = 0;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                double rating = entry.getValue();
                int mid = entry.getKey() - 1;
                double predicted = vectorMultiplication(users[u], movies[mid]);
                fit += rating*Math.log(rating/predicted) - rating+predicted;
            }
        }

        return fit;
    }

    @Override
    public Chromosome copy() {
        return new NMF(users.clone(), movies.clone(), learningRate,userService);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        double[][] pusers = ((NMF) p2).getUsers();
        double[][] pmovies = ((NMF) p2).getMovies();
        double[][] u1 = users.clone();
        double[][] m1 = pmovies.clone();
        double[][] u2 = pusers.clone();
        double[][] m2 = movies.clone();

        return List.of(new NMF(u1,m1,learningRate,userService),new NMF(u2,m2,learningRate,userService));
    }
}
