package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.*;

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

        lossGradient(old_users, old_movies,1);
        regularizationGradient(old_users,old_movies,1);
    }

    private void lossGradient(double[][] old_users, double[][] old_movies, double gradientWeight){
        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                double rating = entry.getValue();
                double e = rating - vectorMultiplication(old_users[u], old_movies[mid]);
                double weight = learningRate*e*gradientWeight;
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += weight*old_movies[mid][f];
                    movies[mid][f] += weight*old_users[u][f];
                }
            }
        }
    }

    @Override
    public void mutate(double chance) {
        
    }

    @Override
    public void memetic(double chance) {
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

        return e + regularizationLoss();
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
        double[][] u2 = pusers.clone();
        double[][] m1 = pmovies.clone();
        double[][] m2 = movies.clone();

        List<Chromosome> l = List.of(new RMF(u1,m1,learningRate,regularization,userService),new RMF(u2,m2,learningRate,regularization,userService));
        System.out.println(fitness() + "||" + p2.fitness() + "||" + l.get(0).fitness() + "||" + l.get(1).fitness());
        return l;
    }

    @Override
    public Particle copyParticle() {
        return new RMF(users.clone(),movies.clone(),learningRate,regularization,userService);
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {
        double[][] old_movies = movies.clone();
        double[][] old_users = users.clone();
        RMF best = (RMF) bestParticle;

        lossGradient(old_users,old_movies,gradientWeight);
        regularizationGradient(old_users,old_movies,gradientWeight);


        double weight = learningRate*(1-gradientWeight);
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
    }

    @Override
    public double getLoss() {
        return fitness();
    }
}
