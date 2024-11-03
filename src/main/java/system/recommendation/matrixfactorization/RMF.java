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
        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey();
                double rating = entry.getValue();
                double[] uf = this.users[u];
                double[] mf = this.movies[mid-1];
                double e = rating - vectorMultiplication(uf, mf);
                for(int f = 0; f < uf.length; f++){
                    double du = this.regularization*uf[f]-e*mf[f];
                    double dm = this.regularization*mf[f]-e*uf[f];
                    uf[f] -= this.learningRate*du;
                    mf[f] -= this.learningRate*dm;
                }
            }
        }
    }
}
