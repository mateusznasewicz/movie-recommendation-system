package system.recommendation.particleswarm;

import system.recommendation.matrixfactorization.MMMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;


public class MMMFprovider implements ParticleProvider {

    private final RatingService<User,Movie> userService;
    private final int features;
    private final double learningRate;
    private final double regularization;

    public MMMFprovider(RatingService<User, Movie> userService, int features, double learningRate, double regularization){
        this.userService = userService;
        this.features = features;
        this.learningRate = learningRate;
        this.regularization = regularization;
    }

    @Override
    public Particle initParticle() {
        return new MMMF(userService, features, learningRate, regularization,0.01);
    }
}
