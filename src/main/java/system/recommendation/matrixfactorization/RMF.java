package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class RMF extends MatrixFactorization implements Chromosome, Particle {

    private final SplittableRandom rand = new SplittableRandom();

    public RMF(RatingService<User, Movie> userService, int k, double learningRate, double regularization,double stdDev) {
        super(userService, k, learningRate, regularization, false, stdDev);
    }

    public RMF(double[][] users, double[][] movies, double learningRate, double regularization, RatingService<User, Movie> userService) {
        super(users,movies,learningRate,regularization,userService);
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
    public void mutate(double chance) {
        if(rand.nextDouble() >= chance) return;
        gd_step();
    }

    @Override
    public double fitness() {
        double e = 0;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                double rating = entry.getValue();
                int mid = entry.getKey() - 1;
                double predicted = vectorMultiplication(users[u], movies[mid]);
                e += Math.pow(rating - predicted,2);
            }
        }

        return e;
    }

    @Override
    public Chromosome copy() {
        return new RMF(users.clone(),movies.clone(),learningRate,regularization,userService);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        double[][] pusers = ((RMF) p2).getUsers();
        double[][] pmovies = ((RMF) p2).getMovies();
        double[][] u1 = users.clone();
        double[][] m1 = pmovies.clone();
        double[][] u2 = pusers.clone();
        double[][] m2 = movies.clone();

        return List.of(new RMF(u1,m1,learningRate,regularization,userService),new RMF(u2,m2,learningRate,regularization,userService));
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {

    }

    @Override
    public double getLoss() {
        return 0;
    }
}
