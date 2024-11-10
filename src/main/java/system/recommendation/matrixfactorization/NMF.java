package system.recommendation.matrixfactorization;

import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class NMF extends MatrixFactorization {
    public NMF(RatingService<User, Movie> userService, int features, double learningRate) {
        super(userService, features, learningRate);
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
    protected double calcLoss() {
        return 0;
    }
}
