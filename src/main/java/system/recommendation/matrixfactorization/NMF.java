package system.recommendation.matrixfactorization;

import system.recommendation.Utils;
import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SplittableRandom;

public class NMF extends MatrixFactorization {
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
        double[][] old_users = Utils.deepCopy(users);
        double[][] old_movies = Utils.deepCopy(movies);
        euclideanGradient(old_users,old_movies,1);
    }

    private void divergenceAdditive(double[][] old_users, double[][] old_movies, double gradientWeight) {
        double weight = gradientWeight*learningRate;
        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                double rating = entry.getValue();
                double predicted = vectorMultiplication(old_users[u], old_movies[mid]);
                double ratingToPredicted = rating/predicted;
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += weight*old_movies[mid][f]*ratingToPredicted - weight*old_movies[mid][f];
                    movies[mid][f] += weight*old_users[u][f]*ratingToPredicted - weight*old_users[u][f];
                }
            }
        }
    }
}
