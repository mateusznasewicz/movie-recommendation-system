package system.recommendation.matrixfactorization;

import system.recommendation.Utils;
import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.*;

public class RMF extends MatrixFactorization implements Chromosome, Particle {

    private final SplittableRandom rand = new SplittableRandom();
    private double[][] distMatrix = null;
    private double[] totalDist = null;
    private double fitness = 0;

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
    protected void step() {
        double[][] old_users = Utils.deepCopy(users);
        double[][] old_movies = Utils.deepCopy(movies);

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

        int u = rand.nextInt(users.length);
        User entity = userService.getEntity(u+1);
        List<Integer> mIDs = entity.getRatings().keySet().stream().toList();
        int mID =  mIDs.get(rand.nextInt(mIDs.size()));

        double[] old_users = users[u].clone();
        double[] old_movies = movies[mID-1].clone();
        double e = entity.getRating(mID) - vectorMultiplication(old_users, old_movies);
        double weight = learningRate*e;
        for(int f = 0; f < users[0].length; f++){
            users[u][f] += weight*old_movies[f];
            movies[mID-1][f] += weight*old_users[f];
        }

        User user = userService.getEntity(u+1);
        Movie movie = userService.getItem(mID);
        double old_fit = calcFitness(old_users,old_movies,user,movie);
        double new_fit = calcFitness(users[u],movies[mID-1],user,movie);
        fitness += new_fit - old_fit;
    }

    @Override
    public double fitness() {
        if(fitness != 0) return fitness;
        double e = 0;
        System.out.println("LICZY");
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

        this.fitness = e;
        return e;
    }

    private double calcFitness(double[] u, double[] m, User user, Movie movie){
        double e = 0;
        int movieID = movie.getId();

        Map<Integer, Double> ratings = user.getRatings();
        for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
            double rating = entry.getValue();
            int mid = entry.getKey() - 1;
            double predicted = vectorMultiplication(u, movies[mid]);
            e += Math.pow(rating - predicted,2);
        }

        for(Integer uid: movie.getRated()){
            double rating = userService.getRating(uid,movieID);
            double predicted = vectorMultiplication(m, users[uid-1]);
            e += Math.pow(rating - predicted,2);
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
        double[][] pusers = ((RMF) p2).getUsers();
        double[][] pmovies = ((RMF) p2).getMovies();

        int u = rand.nextInt(users.length);
        int m = rand.nextInt(movies.length);
        User user = userService.getEntity(u+1);
        Movie movie = userService.getItem(m + 1);

        double[][] u1 = Utils.deepCopy(users);
        double[][] m1 = Utils.deepCopy(movies);
        double[][] u2 = Utils.deepCopy(pusers);
        double[][] m2 = Utils.deepCopy(pmovies);

        double old1 = calcFitness(u1[u],m1[m],user,movie);
        double old2 = calcFitness(u2[u],m2[m],user,movie);

        u1[u] = pusers[u].clone();
        m1[m] = pmovies[m].clone();
        u2[u] = users[u].clone();
        m2[m] = movies[m].clone();

        double new1 = calcFitness(u1[u],m1[m],user,movie);
        double new2 = calcFitness(u2[u],m2[m],user,movie);

        RMF c1 = new RMF(u1,m1,learningRate,regularization,userService,distMatrix,totalDist);
        RMF c2 = new RMF(u2,m2,learningRate,regularization,userService,distMatrix,totalDist);

        c1.fitness +=  new1 - old1;
        c2.fitness +=  new2 - old2;
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

        lossGradient(old_users,old_movies,gradientWeight);
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
