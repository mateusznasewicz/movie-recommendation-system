package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.List;
import java.util.Map;

public class RMF extends MatrixFactorization implements Chromosome, Particle {

    public RMF(RatingService<User, Movie> userService, int k, double learningRate, double regularization) {
        super(userService, k, learningRate, regularization, false);
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
                double e = rating - vectorMultiplication(old_users[u], old_movies[mid]);
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += learningRate*e*old_movies[mid][f];
                    movies[mid][f] += learningRate*e*old_users[u][f];
                }
            }
        }

        //regularization part
        regularizationGradient(old_users,old_movies,1);
    }

    @Override
    protected double calcLoss() {
        return 0;
    }

    @Override
    public void mutate(double chance) {

    }

    @Override
    public double fitness() {
        return 0;
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        return List.of();
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {

    }

    @Override
    public double getLoss() {
        return 0;
    }
}
