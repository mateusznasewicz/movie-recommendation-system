package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class RMF extends MatrixFactorization {

    public RMF(RatingService<User> userService, int k, double learningRate, double regularization) {
        super(userService, k, learningRate, regularization);
    }

    @Override
    public double[][] getPredictedRatings() {
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                predicted[i][j] = vectorMultiplication(users[i], movies[j]);
            }
        }
        return predicted;
    }

    @Override
    protected void sgd_step() {
        double[][] old_users = users.clone();
        double[][] old_movies = movies.clone();

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                double rating = entry.getValue();
                double e = rating - vectorMultiplication(old_users[u], old_movies[mid]);
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += learningRate*e*old_movies[mid][f];
                    movies[mid][f] += learningRate*e*old_users[u][f];
                }
            }
        }

        //regularization part
        regularizationGradient(old_movies,old_users);
    }
}
