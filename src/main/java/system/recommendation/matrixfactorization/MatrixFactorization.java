package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MatrixFactorization extends Chromosome<MatrixFactorization> {
    private final double[][] users;
    private final double[][] movies;
    private final RatingService<User> userService;
    private final int k;

    public MatrixFactorization(RatingService<User> userService, int k) {
        int u = userService.getUsers().size();
        int m = userService.getMovies().size();
        this.users = new double[u][k];
        this.movies = new double[m][k];
        this.userService = userService;
        this.k = k;

        for (int i = 0; i < u; i++) {
            users[i] = initLatentFeatures(k);
        }

        for (int i = 0; i < m; i++) {
            movies[i] = initLatentFeatures(k);
        }

        this.value = fitness();
    }

    public MatrixFactorization(double[][] users, double[][] movies){
        this.users = users;
        this.movies = movies;
        this.k = 0;
        this.userService = null;
    }

    public double[][] getMovies(){
        return movies;
    }

    public double[][] getUsers(){
        return users;
    }

    public double[][] getPredictedRating(){
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                predicted[i][j] = vectorMultiplication(users[i], movies[j]);
            }
        }
        return predicted;
    }

    public void sgd(double learningRate, double regularization, int epochs){

        for(int i = 0; i < epochs; i++)
        {
            System.out.println("EPOCH " + i);
            for(int u = 0; u < users.length; u++){
                User user = userService.getEntity(u+1);
                Map<Integer, Double> ratings = user.getRatings();
                for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                    int mid = entry.getKey();
                    double rating = entry.getValue();
                    double[] uf = this.users[u];
                    double[] mf = this.movies[mid-1];
                    double e = rating - vectorMultiplication(uf, mf);
                    updateLatentFeatures(2*e, uf, mf, regularization, learningRate);
                    updateLatentFeatures(2*e, mf, uf, regularization, learningRate);
                }
            }
        }
    }

    private void updateLatentFeatures(double error, double[] f1, double[] f2, double regularization, double learningRate){
        double[] c1 = vectorMultiplication(f1, regularization);
        double[] c2 = vectorMultiplication(f2, error);

        for(int i = 0; i < c2.length; i++){
            c2[i] = c2[i] - c1[i];
        }

        c2 = vectorMultiplication(c2, learningRate);

        for(int i = 0; i < f1.length; i++){
            f1[i] = f1[i] + c2[i];
        }
    }

    public static double vectorMultiplication(double[] f1, double[] f2) {
        if(f1.length != f2.length) return Double.MIN_VALUE;
        double sum = 0;

        for(int i = 0; i < f1.length; i++){
            sum += f1[i] * f2[i];
        }

        return sum;
    }

    private double[] vectorMultiplication(double[] f1, double a) {
        double[] c = f1.clone();
        for(int i = 0; i < f1.length; i++){
            c[i] = c[i] * a;
        }
        return c;
    }

    private double[] initLatentFeatures(int k) {
        double[] latentFeatures = new double[k];
        for(int i = 0; i < k; i++){
            Random random = new Random();
            double mean = 0.0;
            double stdDev = 0.01;
            latentFeatures[i] = mean + stdDev * random.nextGaussian();
        }
        return latentFeatures;
    }

    @Override
    public double fitness() {
        double sum = 0;

        for(int u = 0; u < users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey();
                double rating = entry.getValue();
                double[] uf = this.users[u];
                double[] mf = this.movies[mid-1];
                sum +=  Math.pow(rating - vectorMultiplication(uf, mf),2);
            }
        }

        return sum;
    }

    @Override
    public void mutate() {

    }

    @Override
    public List<MatrixFactorization> crossover(MatrixFactorization p){
        Random random = new Random();
        int ux = random.nextInt(this.k);
        int mx = random.nextInt(this.k);
        int uy = random.nextInt(this.users.length);
        int my = random.nextInt(this.movies.length);
        System.out.println(uy);

        int miny = Math.min(uy,my);
        int maxy = Math.max(uy,my);

        double[][] u1 = this.users.clone();
        double[][] m1 = this.movies.clone();
        double[][] u2 = p.getUsers().clone();
        double[][] m2 = p.getMovies().clone();

        for(int y = miny ; y <= maxy; y++){
            for(int x = 0; x < k; x++)
            {
                if(y != miny && y != maxy){
                    u1[y][x] = u2[y][x];
                    m1[y][x] = m2[y][x];
                    u2[y][x] = this.users[y][x];
                    m2[y][x] = this.movies[y][x];
                }
                else {

                    if((uy < my && y == miny) || (uy < my && x < mx) || (uy > my && y == miny && x > mx) || (uy == my && x < mx)){
                        m1[y][x] = m2[y][x];
                        m2[y][x] = this.movies[y][x];
                    }

                    if((uy < my && y == maxy) || (uy < my && x > ux) || (uy > my && y == maxy && x < ux) || (uy == my && x > ux)){
                        u1[y][x] = u2[y][x];
                        u2[y][x] = this.users[y][x];
                    }
                }
            }
        }

        return List.of(new MatrixFactorization(u1,m1),new MatrixFactorization(u2,m2));
    }
}
