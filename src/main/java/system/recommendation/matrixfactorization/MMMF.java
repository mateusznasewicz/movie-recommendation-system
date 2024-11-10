package system.recommendation.matrixfactorization;

import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;

import java.util.Map;
import java.util.Random;

public class MMMF extends MatrixFactorization implements Particle{
    private final double[][] margin;
    private final double[] discrete_ratings = {0.5,1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};

    public MMMF(RatingService<User, Movie> userService, int features, double learningRate, double regularization, double stdDev){
        super(userService, features, learningRate, regularization, false, stdDev);
        this.margin = new double[users.length][discrete_ratings.length];
        Random random = new Random();
        for(int i = 0; i< users.length; i++){
            for(int j = 0; j < discrete_ratings.length; j++){
                margin[i][j] = random.nextGaussian() + 0.01;
            }
        }
    }

    public MMMF(double[][] users, double[][] movies, double[][] margin, double learningRate, double regularization ,RatingService<User,Movie> ratingService){
        super(users,movies,learningRate,regularization,ratingService);
        this.margin = margin;
    }

    @Override
    public double[][] getPredictedRatings() {
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                double p = vectorMultiplication(users[i], movies[j]);

                boolean changed = false;
                for(int k = 0; k < discrete_ratings.length; k++){
                    if(p < margin[i][k]){
                        p = discrete_ratings[k];
                        changed = true;
                        break;
                    }
                }

                if(!changed){
                    p = discrete_ratings[discrete_ratings.length-1]+0.5;
                }

                predicted[i][j] = p;
            }
        }
        return predicted;
    }

    @Override
    protected void gd_step() {
        double[][] old_margin = margin.clone();
        double[][] old_users = users.clone();
        double[][] old_movies = movies.clone();

        hingeLossGradient(old_users,old_movies,old_margin,1);
        regularizationGradient(old_users,old_movies,1);
    }

    private void hingeLossGradient(double[][] old_users, double[][] old_movies, double[][] old_margin, double gradientWeight){
        double weight = gradientWeight*learningRate;
        for(int i = 0; i< users.length; i++){
            for(int a = 0; a < discrete_ratings.length; a++){
                User user = userService.getEntity(i+1);
                Map<Integer, Double> ratings = user.getRatings();
                for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                    int mid = entry.getKey() - 1;
                    double rating = entry.getValue();
                    double predictedRating = vectorMultiplication(old_users[i],old_movies[mid]);
                    int t = (discrete_ratings[a] >= rating) ? 1 : -1;
                    double z = t*(old_margin[i][a] - predictedRating);
                    double h = smoothedHingeLossDerivative(z);

                    margin[i][a] -= weight*t*h;
                    for(int f = 0; f < users[0].length; f++){
                        users[i][f] += weight*t*h*old_movies[mid][f];
                        movies[mid][f] += weight*t*h*old_users[i][f];
                    }
                }
            }
        }
    }

    private double smoothedHingeLossDerivative(double z){
        if(z < 0){
            return -1;
        }

        if(z > 1){
            return 0;
        }

        return z - 1;
    }

    private double smoothedHingeLoss(double z){
        if(z < 0){
            return 0.5 - z;
        }

        if(z > 1){
            return 0;
        }

        return Math.pow(1-z,2)/2;
    }

    @Override
    public Particle copyParticle() {
        return new MMMF(users.clone(),movies.clone(),margin.clone(),learningRate,regularization,userService);
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {
        double[][] old_margin = margin.clone();
        double[][] old_users = users.clone();
        double[][] old_movies = movies.clone();
        MMMF best = (MMMF) bestParticle;

        //GD part
        hingeLossGradient(old_users, old_movies, old_margin,gradientWeight);
        regularizationGradient(old_users,old_movies,gradientWeight);

        //Swarm moves towards best solution
        double weight = learningRate*(1-gradientWeight);
        moveParticleTowardsSwarm(best.users,old_users,users,weight);
        moveParticleTowardsSwarm(best.movies,old_movies,movies,weight);
        moveParticleTowardsSwarm(best.margin,old_margin,margin,weight);
    }

    @Override
    public double getLoss() {
        double l = 0;
        for (int i = 0; i < users.length; i++) {
            for (int a = 0; a < discrete_ratings.length; a++) {
                User user = userService.getEntity(i + 1);
                Map<Integer, Double> ratings = user.getRatings();
                for (Map.Entry<Integer, Double> entry : ratings.entrySet()) {
                    int mid = entry.getKey() - 1;
                    double rating = entry.getValue();
                    double predictedRating = vectorMultiplication(users[i], movies[mid]);
                    int t = (discrete_ratings[a] >= rating) ? 1 : -1;
                    double z = t * (margin[i][a] - predictedRating);
                    l += smoothedHingeLoss(z);
                }
            }
        }
        return l + regularizationLoss();
    }
}
