package system.recommendation.strategy;


import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("unchecked")
public class KMeans<T extends Entity, G extends Entity> extends Strategy<T> {

    private final List<T> centroids = new ArrayList<>();
    private List<Set<Integer>> membership;
    private final SplittableRandom rand = new SplittableRandom();
    private final RatingService<T ,G> ratingService;
    private final Similarity<T> simFunction;

    public KMeans(int k, RatingService<T, G> ratingService, Similarity<T> simFunction) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService.getEntityMap(), k, simFunction);
        this.ratingService = ratingService;
        this.simFunction = simFunction;

        Class<T> clazz = (Class<T>) ratingService.getEntity(1).getClass();
        for(int i = 0; i < k; i++){
            T centroid = randomCentroid(ratingService,clazz);
            centroids.add(centroid);
        }

        for(int i = 0; i < 100; i++){
            step();
        }
    }

    private void calculateCenter(int c){
        Set<Integer> members = membership.get(c);
        T centroid = centroids.get(c);

        for(int itemID: centroid.getRatings().keySet()){
            int n = 0;
            double rating = 0;
            for(int memberID: members){
                if(ratingService.isRatedById(memberID, itemID)){
                    rating += ratingService.getRating(memberID, itemID);
                    n++;
                }
            }
            centroid.setRating(itemID, rating/n);
        }
    }

    private void initMembership(){
        membership = new ArrayList<>();
        for(int c = 0; c < centroids.size(); c++){
            membership.add(new HashSet<>());
        }
    }

    private void step(){
        initMembership();

        int entitiesSize = ratingService.getEntityMap().size();
        for(int i = 0; i < entitiesSize; i++){
            double closestDist = Double.MAX_VALUE;
            int closestID = -1;
            T entity = ratingService.getEntity(i+1);

            for (int c = 0; c < centroids.size() ; c++) {
                double dist = simFunction.calculate(entity, centroids.get(c));
                if (dist < closestDist) {
                    closestID = c;
                    closestDist = dist;
                }
            }

            membership.get(closestID).add(i+1);
        }

        for(int c = 0; c < centroids.size(); c++){
            calculateCenter(c);
        }
    }

    private T randomCentroid(RatingService<T, G> ratingService, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T centroid = clazz.getDeclaredConstructor().newInstance();

        double[] ratings = {0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0};
        int s = ratings.length;
        int items = ratingService.getItemMap().size();

        for(int i = 1; i < items + 1; i++){
            centroid.addRating(i,ratings[rand.nextInt(s)]);
        }

        return centroid;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        for(Set<Integer> cluster: membership){
            if(cluster.contains(item.getId())){
                return (List<Integer>) cluster;
            }
        }
        return null;
    }
}
