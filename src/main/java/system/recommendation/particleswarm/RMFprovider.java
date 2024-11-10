package system.recommendation.particleswarm;

import system.recommendation.matrixfactorization.RMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

public class RMFprovider implements ParticleProvider{
    private final RatingService<User, Movie> userService;
    private final int features;
    private final double learningRate;
    private final double regularization;
    private final double stdDev;

    public RMFprovider(RatingService<User,Movie> userService, int features, double learningRate, double regularization,double stdDev){
        this.userService = userService;
        this.features = features;
        this.learningRate = learningRate;
        this.regularization = regularization;
        this.stdDev = stdDev;
    }

    @Override
    public Particle initParticle() {
        return new RMF(userService, features, learningRate, regularization,stdDev);
    }
}
