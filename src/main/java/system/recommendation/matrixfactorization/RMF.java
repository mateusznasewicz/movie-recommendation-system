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

    public RMF(RatingService<User, Movie> userService, int k, double learningRate, double regularization,double stdDev,double[][] distMatrix, double[] totalDist) {
        super(userService, k, learningRate, regularization, false, stdDev);
        this.distMatrix = distMatrix;
        this.totalDist = totalDist;
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
        if(rand.nextDouble() >= chance)  return;
        int u;
        do{
            u = rand.nextInt(users.length);
        }while(totalDist[u] == 0);

        double r = rand.nextDouble(totalDist[u]);
        double cumulativeDist = 0;

        for(int i = 0; i < distMatrix.length; i++){
            if(distMatrix[u][i] == 0)continue;
            cumulativeDist += 1.0 / distMatrix[u][i];
            if(cumulativeDist > r){
                double[] t = users[u];
                users[u] = users[i];
                users[i] = t;
                break;
            }
        }

        User user = userService.getEntity(u+1);
        Set<Integer> mIDs = user.getRatings().keySet();
        double min = Double.MAX_VALUE;
        int bestID = -1;
        int id1 = mIDs.iterator().next() - 1;
        double p1 =  vectorMultiplication(users[u],movies[id1]);

        for(int id2: mIDs){
            if(id1==id2-1)continue;
            double p2 =  vectorMultiplication(users[u],movies[id2-1]);
            double dist = Math.abs(p1-p2);
            if(dist < min){
                bestID = id2-1;
                min = dist;
            }
        }

        double[] t = movies[id1];
        movies[id1] = movies[bestID];
        movies[bestID] = t;
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
        return new RMF(Utils.deepCopy(users),Utils.deepCopy(movies),learningRate,regularization,userService,distMatrix,totalDist);
    }

    @Override
    public Particle copyParticle() {
        return new RMF(Utils.deepCopy(users),Utils.deepCopy(movies),learningRate,regularization,userService);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        RMF parent2 = (RMF) p2;
        double[][] pusers = parent2.getUsers();
        double[][] pmovies = parent2.getMovies();
        int k = pusers[0].length;

        double[][] u1 = new double[users.length][k];
        double[][] m1 = new double[movies.length][k];
        double[][] u2 = new double[users.length][k];
        double[][] m2 = new double[movies.length][k];

        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < k; j++){
                u1[i][j] = weight*users[i][j] + (1-weight)*pusers[i][j];
                u2[i][j] = (1-weight)*users[i][j] + weight*pusers[i][j];
            }
        }

        for(int i = 0; i < movies.length; i++){
            for(int j = 0; j < k; j++){
                m1[i][j] = weight*movies[i][j] + (1-weight)*pmovies[i][j];
                m2[i][j] = (1-weight)*movies[i][j] + weight*pmovies[i][j];
            }
        }

        var c1 = new RMF(u1,m1,learningRate,regularization,userService,distMatrix,totalDist);
        var c2 = new RMF(u2,m2,learningRate,regularization,userService,distMatrix,totalDist);
        System.out.println(fitness() +"|"+p2.fitness()+"|"+c1.fitness()+"|"+ c2.fitness());
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

        euclideanGradient(old_users,old_movies,gradientWeight);
        regularizationGradient(old_users,old_movies,gradientWeight);


        double weight = learningRate*gradientWeight;
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
    }

    @Override
    public double getLoss() {
        return fitness()+regularizationLoss();
    }
}
