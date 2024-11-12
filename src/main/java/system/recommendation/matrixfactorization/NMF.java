package system.recommendation.matrixfactorization;

import system.recommendation.Utils;
import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class NMF extends MatrixFactorization implements Chromosome, Particle {
    private final SplittableRandom rand = new SplittableRandom();

    public NMF(RatingService<User, Movie> userService, int features, double learningRate, double stdDev) {
        super(userService, features, learningRate, stdDev);
    }

    public NMF(double[][] users, double[][] movies, double learningRate, RatingService<User, Movie> userService) {
        super(users,movies,learningRate,userService);
    }

    @Override
    public double[][] getPredictedRatings() {
        return multiplyFactorizedMatrices();
    }

    @Override
    protected void gd_step() {
        double[][] old_users = Utils.deepCopy(users);
        double[][] old_movies = Utils.deepCopy(movies);
        divergenceAdditive(old_users,old_movies,1);
    }

    private void euclideanAdditive(double[][] old_users, double[][] old_movies,double gradient){
        for(int u = 0; u < users.length; u++){
            User entity = userService.getEntity(u+1);
            Map<Integer, Double> ratings = entity.getRatings();
            for(Map.Entry<Integer, Double> rating : ratings.entrySet()){
                double r = rating.getValue();
                int m = rating.getKey()-1;
                double predicted = vectorMultiplication(users[u],movies[m]);
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += learningRate*old_movies[m][f]*r - learningRate*old_movies[m][f]*predicted;
                    movies[m][f] += learningRate*old_users[u][f]*r - learningRate*old_users[u][f]*predicted;
                }
            }
        }
    }



    private void divergenceAdditive(double[][] old_users, double[][] old_movies, double gradientWeight) {
        double weight = gradientWeight*learningRate;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                double rating = entry.getValue();
                double predicted = vectorMultiplication(old_users[u], old_movies[mid]);
                double ratingToPredicted = rating/predicted;
                for(int f = 0; f < users[0].length; f++){
                    users[u][f] += weight*old_movies[mid][f]*ratingToPredicted - weight*old_movies[mid][f];
                    movies[mid][f] += weight*old_users[u][f]*ratingToPredicted - weight*old_users[u][f];
                }
            }
        }
    }

    @Override
    public void mutate(double chance) {
        if(rand.nextDouble() >= chance)return;
        gd_step();
    }

    @Override
    public void memetic(double chance) {

    }

    @Override
    public double fitness() {
        double fit = 0;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                double rating = entry.getValue();
                int mid = entry.getKey() - 1;
                double predicted = vectorMultiplication(users[u], movies[mid]);
                fit += rating*Math.log(rating/predicted) - rating+predicted;
            }
        }

        return fit;
    }

    @Override
    public Chromosome copy() {
        return new NMF(Utils.deepCopy(users), Utils.deepCopy(movies), learningRate,userService);
    }

    @Override
    public Particle copyParticle(){
        return new NMF(Utils.deepCopy(users), Utils.deepCopy(movies), learningRate,userService);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        double[][] pusers = ((NMF) p2).getUsers();
        double[][] pmovies = ((NMF) p2).getMovies();

        int u = rand.nextInt(users.length);
        int m = rand.nextInt(movies.length);

        double[][] u1 = Utils.deepCopy(users);
        double[][] m1 = Utils.deepCopy(movies);
        double[][] u2 = Utils.deepCopy(pusers);
        double[][] m2 = Utils.deepCopy(pmovies);

        u1[u] = pusers[u].clone();
        m1[m] = pmovies[m].clone();
        u2[u] = users[u].clone();
        m2[m] = movies[m].clone();

        return List.of(new NMF(u1,m1,learningRate,userService),new NMF(u2,m2,learningRate,userService));
    }

    @Override
    public double[][] getChromosome() {
        return new double[0][];
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {
        double[][] old_users = Utils.deepCopy(users);
        double[][] old_movies = Utils.deepCopy(movies);
        NMF best = (NMF) bestParticle;
        divergenceAdditive(old_users,old_movies,gradientWeight);


        double weight = learningRate*(1-gradientWeight);
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
    }

    @Override
    public double getLoss() {
        return fitness();
    }
}
