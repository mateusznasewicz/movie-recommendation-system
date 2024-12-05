package system.recommendation.particleswarm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.KMeans;

import java.lang.reflect.InvocationTargetException;

public class KMEANSprovider<T extends Entity,G extends Entity> implements ParticleProvider {
    private int k;
    private RatingService<T,G> ratingService;

    public KMEANSprovider(int k, RatingService<T, G> ratingService){
        this.k = k;
        this.ratingService = ratingService;
    }
    @Override
    public Particle initParticle() {
        try{
            return new KMeans<>(k,ratingService);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
