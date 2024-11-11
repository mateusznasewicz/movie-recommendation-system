package system.recommendation.matrixfactorization;

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
//        double[][] old_users = users.clone();
//        double[][] old_movies = movies.clone();
//        List<Integer> perm = new ArrayList<>(users.length);
//        for(int i = 0; i < users.length; i++){
//            perm.add(i);
//        }
//        Collections.shuffle(perm);
//
//        for(int u = 0; u < users.length/4; u++){
//            int uid = perm.get(u);
//            User user = userService.getEntity(uid+1);
//            Map<Integer, Double> ratings = user.getRatings();
//            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
//                int mid = entry.getKey() - 1;
//                double rating = entry.getValue();
//                double e = rating - vectorMultiplication(old_users[uid], old_movies[mid]);
//                double w1 = learningRate*e*2;
//                double w2 = learningRate*regularization;
//                for(int f = 0; f < users[0].length; f++){
//                    users[uid][f] += w1*old_movies[mid][f];
//                    movies[mid][f] += w1*old_users[uid][f];
//
//                    users[uid][f] -= w2*old_users[uid][f];
//                    movies[mid][f] -= w2*old_movies[mid][f];
//                }
//            }
//        }
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
        return new RMF(users.clone(),movies.clone(),learningRate,regularization,userService,distMatrix,totalDist);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p2, double weight) {
        double[][] pusers = ((RMF) p2).getUsers();
        double[][] pmovies = ((RMF) p2).getMovies();

        double[][] u1 = users.clone();
        double[][] u2 = pusers.clone();
        double[][] m1 = pmovies.clone();
        double[][] m2 = movies.clone();

        List<Chromosome> l = List.of(new RMF(u1,m1,learningRate,regularization,userService,distMatrix,totalDist),new RMF(u2,m2,learningRate,regularization,userService,distMatrix,totalDist));
//        System.out.println(fitness() + "||" + p2.fitness() + "||" + l.get(0).fitness() + "||" + l.get(1).fitness());
        return l;
    }

    @Override
    public double[][] getChromosome() {
        return movies;
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

        lossGradient(old_users,old_movies,1);
        regularizationGradient(old_users,old_movies,1);


        double weight = learningRate;
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
    }

    @Override
    public double getLoss() {
        return fitness();
    }
}
