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
        System.out.println(fitness());
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
        //double fit = fitness();
        int u = rand.nextInt(users.length);
        User user = userService.getEntity(u+1);
        Map<Integer, Double> ratings = user.getRatings();

        List<Integer> mIDs = new ArrayList<>(ratings.keySet());
        int m = rand.nextInt(mIDs.size());
        int mID =  mIDs.get(m) - 1;
        double rating = ratings.get(mID+1);

        double[] old_users = users[u].clone();
        double[] old_movies = movies[mID].clone();
        double e = rating - vectorMultiplication(old_users, old_movies);

        for(int f = 0; f < users[0].length; f++){
            users[u][f] -= regularization*learningRate*old_users[f];
            movies[mID][f] -= regularization*learningRate*old_movies[f];

            users[u][f] += 2*e*learningRate*old_movies[f];
            movies[mID][f] += 2*e*learningRate*old_users[f];
        }

        //System.out.println(fit + "||" + fitness());
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
                //System.out.println(predicted + "||" + rating);
                e += Math.pow(rating - predicted,2);
            }
        }

        return e;
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        int usize = this.users.length;
        int msize = this.movies.length;
        int k = this.users[0].length;
        int u = rand.nextInt(this.users.length);
        int m = rand.nextInt(this.movies.length);
        double[][] pusers = ((RMF) p2).getUsers();
        double[][] pmovies = ((RMF) p2).getMovies();
        double[][] u1 = new double[usize][k];
        double[][] m1 = new double[msize][k];
        double[][] u2 = new double[usize][k];
        double[][] m2 = new double[msize][k];

        for(int i = 0; i < u + 1; i++){
            u1[i] = this.users[i].clone();
            u2[i] = pusers[i].clone();
        }

        for(int i = u + 1; i < this.users.length; i++){
            u1[i] = pusers[i].clone();
            u2[i] = this.users[i].clone();
        }

        for(int i = 0; i < m; i++){
            m1[i] = pmovies[i].clone();
            m2[i] = this.movies[i].clone();
        }

        for(int i = m; i < this.movies.length; i++){
            m1[i] = this.movies[i].clone();
            m2[i] = pmovies[i].clone();
        }
        List<Chromosome> l = List.of(new RMF(u1,m1,learningRate,regularization,userService),new RMF(u2,m2,learningRate,regularization,userService));
        //System.out.println(l.get(0).fitness() + "||" + l.get(1).fitness() + "||" + fitness() + "||" + p2.fitness());
        return l;
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {

    }

    @Override
    public double getLoss() {
        return 0;
    }
}
