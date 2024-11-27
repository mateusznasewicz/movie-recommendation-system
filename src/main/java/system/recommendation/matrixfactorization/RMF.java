package system.recommendation.matrixfactorization;

import system.recommendation.Utils;
import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.*;

public class RMF extends MatrixFactorization implements Chromosome, Particle {

    private double[][] distMatrix = null;
    private double[] totalDist = null;

    public RMF(RatingService<User, Movie> userService, int k, double learningRate, double regularization,double stdDev) {
        super(userService, k, learningRate, regularization, false, stdDev);
    }

    public RMF(double[][] users, double[][] movies, double learningRate, double regularization, RatingService<User, Movie> userService,double[][] distMatrix, double[] totalDist) {
        super(users,movies,learningRate,regularization,userService);
        this.distMatrix = distMatrix;
        this.totalDist = totalDist;
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
        double[][] old_users = Utils.deepCopy(users);
        double[][] old_movies = Utils.deepCopy(movies);
        regularizationGradient(old_users,old_movies,1);
        euclideanGradient(old_users, old_movies,1);
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
        return e;
    }

    @Override
    public Chromosome copy() {
        return new RMF(Utils.deepCopy(users),Utils.deepCopy(movies),learningRate,regularization,userService,distMatrix,totalDist);
    }

    @Override
    public Particle copyParticle() {
        return new RMF(Utils.deepCopy(users),Utils.deepCopy(movies),learningRate,regularization,userService);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        RMF parent2 = (RMF) p2;
        double[][] p2users = parent2.getUsers();
        double[][] p2movies = parent2.getMovies();
        int k = p2users[0].length;

        double[][] u1 = new double[users.length][k];
        double[][] m1 = new double[movies.length][k];
        double[][] u2 = new double[users.length][k];
        double[][] m2 = new double[movies.length][k];

        for(int u = 0; u < users.length; u++){
            for(int f = 0; f < k; f++){
                u1[u][f] = weight*users[u][f] + (1-weight)*p2users[u][f];
                u2[u][f] = weight*p2users[u][f] + (1-weight)*users[u][f];
            }
        }

        for(int m = 0; m < movies.length; m++){
            for(int f = 0; f < k; f++){
                m1[m][f] = weight*movies[m][f] + (1-weight)*p2movies[m][f];
                m2[m][f] = weight*p2movies[m][f] + (1-weight)*movies[m][f];
            }
        }

        var c1 = new RMF(u1,m1,learningRate,regularization,userService,distMatrix,totalDist);
        var c2 = new RMF(u2,m2,learningRate,regularization,userService,distMatrix,totalDist);
        return List.of(c1,c2);
    }

    @Override
    public double[][] getChromosome() {
        return movies;
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {
        double[][] old_movies = Utils.deepCopy(movies);
        double[][] old_users = Utils.deepCopy(users);
        RMF best = (RMF) bestParticle;

        euclideanGradient(old_users,old_movies,1);
        regularizationGradient(old_users,old_movies,1);

        double weight = learningRate;
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
    }

    @Override
    public double getLoss() {
        return fitness() + regularizationLoss();
    }
}
