package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

@SuppressWarnings("unchecked")
public abstract class Clustering<T extends Entity, G extends Entity> extends Strategy<T>{
    protected final RatingService<T ,G> ratingService;
    protected final Similarity<T> distFunction;
    protected final List<T> centroids = new ArrayList<>();
    protected final SplittableRandom rand = new SplittableRandom();
    private final Class<T> clazz;

    public Clustering(RatingService<T, G> ratingService, Similarity<T> simFunction, int k) {
        super(ratingService.getEntityMap(), k, simFunction);
        this.ratingService = ratingService;
        this.distFunction = new EuclideanDistance<>(ratingService);
        this.clazz = (Class<T>) ratingService.getEntity(1).getClass();
    }

    public Clustering(int k, RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService.getEntityMap(),k);
        initCentroids();
    }

    protected abstract void step();
    protected abstract double calcLoss();

    public T randomCentroid(RatingService<T, G> ratingService, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T centroid = clazz.getDeclaredConstructor().newInstance();

        double[] ratings = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        int s = ratings.length;
        int items = ratingService.getItemMap().size();

        for(int i = 1; i < items + 1; i++){
            centroid.addRating(i,ratings[rand.nextInt(s)]);
        }

        return centroid;
    }

    private void initCentroids() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(int i = 0; i < k; i++){
            T centroid = randomCentroid(ratingService,clazz);
            centroids.add(centroid);
            ratingService.addEntity(centroid);
        }
    }

    public void calcCentroids(int epochs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(int i = 0; i < k; i++){
            T centroid = randomCentroid(ratingService,clazz);
            centroids.add(centroid);
            ratingService.addEntity(centroid);
        }

        for(int i = 0; i < epochs; i++){
            step();
            System.out.println(i + "||" + calcLoss());
        }
    }
}
