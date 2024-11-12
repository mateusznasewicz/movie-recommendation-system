package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("unchecked")
public abstract class Clustering<T extends Entity, G extends Entity> extends Strategy<T>{
    protected RatingService<T ,G> ratingService;
    protected Similarity<T> distFunction;
    protected List<T> centroids = new ArrayList<>();
    protected final SplittableRandom rand = new SplittableRandom();
    private final Class<T> clazz;

    public Clustering(RatingService<T, G> ratingService, Similarity<T> simFunction, int k) {
        super(new HashMap<>(ratingService.getEntityMap()), k, simFunction);
        this.clazz = (Class<T>) ratingService.getEntity(1).getClass();
        newService(ratingService);
    }

    public Clustering(int k, RatingService<T, G> ratingService) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(new HashMap<>(ratingService.getEntityMap()),k);
        this.clazz = (Class<T>) ratingService.getEntity(1).getClass();
        newService(ratingService);
    }

    public Clustering(RatingService<T, G> best, RatingService<T, G> org, Similarity<T> simFunction, int k) {
        super(org.getEntityMap(), k, simFunction);
        this.clazz = (Class<T>) org.getEntity(1).getClass();
        this.ratingService = best;
        this.distFunction = new EuclideanDistance<>(best);
    }

    public void newService(RatingService<T, G> ratingService) {
        if(clazz == User.class){
            Map<Integer,User> users = (Map<Integer, User>) new HashMap<>(ratingService.getEntityMap());
            Map<Integer,Movie> movies = (Map<Integer, Movie>) new HashMap<>(ratingService.getItemMap());
            this.ratingService = (RatingService<T, G>) new UserService(users,movies);
        }else{
            Map<Integer,Movie> movies = (Map<Integer, Movie>) new HashMap<>(ratingService.getEntityMap());
            Map<Integer,User> users = (Map<Integer, User>) new HashMap<>(ratingService.getItemMap());
            this.ratingService = (RatingService<T, G>) new MovieService(users,movies);
        }
        this.distFunction = new EuclideanDistance<>(this.ratingService);
    }

    protected abstract void step();
    protected abstract double calcLoss();

    public List<T> getCentroids() {
        return centroids;
    }

    public RatingService<T, G> getRatingService() {
        return ratingService;
    }

    public Similarity<T> getDistFunction() {
        return distFunction;
    }

    public T randomCentroid(int id, RatingService<T, G> ratingService, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T centroid = clazz.getDeclaredConstructor(int.class).newInstance(id);

        double[] ratings = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        int s = ratings.length;
        int items = ratingService.getItemMap().size();

        for(int i = 1; i < items + 1; i++){
            centroid.addRating(i,ratings[rand.nextInt(s)]);
        }

        return centroid;
    }

    void initCentroids() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(int i = 0; i < k; i++){
            T centroid = randomCentroid(-i-1,this.ratingService,clazz);
            centroids.add(centroid);
            this.ratingService.addEntity(centroid);
        }
    }

    public void calcCentroids(int epochs) {
        for(int i = 0; i < epochs; i++){
            System.out.println(i + "||" + calcLoss());
            step();
        }
    }

    public void calcCentroids(int epochs, List<T> centroids){
        this.centroids = centroids;
        calcCentroids(epochs);
    }
}
