package system.recommendation.particleswarm;

import system.recommendation.matrixfactorization.NMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

public class NMFprovider implements ParticleProvider {

    private final RatingService<User, Movie> userService;
    private final int features;
    private final double learningRate;

    public NMFprovider(RatingService<User, Movie> userService, int features, double learningRate){
        this.userService = userService;
        this.features = features;
        this.learningRate = learningRate;
    }

    @Override
    public Particle initParticle() {
        return new NMF(userService, features, learningRate, 0.01);
    }
}
