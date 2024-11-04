package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;
import java.util.Random;

public class MMMF extends MatrixFactorization {
    private double[][] margin;
    private final double[] discrete_ratings = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};

    public MMMF(RatingService<User> userService, int features, double learningRate, double regularization){
        super(userService, features, learningRate, regularization);
        this.margin = new double[users.length][discrete_ratings.length];
        Random random = new Random();
        for(int i = 0; i< users.length; i++){
            for(int j = 0; j < discrete_ratings.length; j++){
                margin[i][j] = random.nextGaussian();
            }
        }
    }

    @Override
    public double[][] getPredictedRatings() {
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                double p = vectorMultiplication(users[i], movies[j]);

                boolean changed = false;
                for(int k = 0; k < discrete_ratings.length; k++){
                    if(p < margin[i][k]){
                        p = discrete_ratings[k];
                        changed = true;
                        break;
                    }
                }

                if(!changed){
                    p = discrete_ratings[discrete_ratings.length-1];
                }

                predicted[i][j] = p;
            }
        }
        return predicted;
    }

    @Override
    protected void sgd_step() {
        double[][] old_margin = margin.clone();
        double[][] old_users = users.clone();
        double[][] old_movies = movies.clone();

        //Hinge loss part
        for(int i = 0; i< users.length; i++){
            for(int a = 0; a < discrete_ratings.length; a++){
                User user = userService.getEntity(i+1);
                Map<Integer, Double> ratings = user.getRatings();
                for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                    int mid = entry.getKey() - 1;
                    double rating = entry.getValue();
                    double predictedRating = vectorMultiplication(old_users[i],old_movies[mid]);
                    int t = (discrete_ratings[a] >= rating) ? 1 : -1;
                    double z = t*(old_margin[i][a] - predictedRating);
                    double h = SmoothedHingeLoss(z);
                    margin[i][a] -= learningRate*t*h;
                    for(int f = 0; f < discrete_ratings.length; f++){
                        users[i][f] += learningRate*t*h*old_movies[mid][f];
                        movies[mid][f] += learningRate*t*h*old_users[i][f];
                    }
                }
            }
        }

        //regularization part
        regularizationGradient(old_movies,old_users);
    }

    private double SmoothedHingeLoss(double z){
        if(z < 0){
            return -1;
        }

        if(z > 1){
            return 0;
        }

        return z - 1;
    }
}
