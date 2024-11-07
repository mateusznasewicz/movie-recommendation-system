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

    public KMeans(int k, int epochs,RatingService<T, G> ratingService, Similarity<T> simFunction) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService.getEntityMap(), k, simFunction);
        this.ratingService = ratingService;
        this.simFunction = simFunction;

        Class<T> clazz = (Class<T>) ratingService.getEntity(1).getClass();
        for(int i = 0; i < k; i++){
            T centroid = randomCentroid(ratingService,clazz);
            centroids.add(centroid);
            ratingService.addEntity(centroid);
        }

        for(int i = 0; i < epochs; i++){
            step();
            for(int c = 0; c < centroids.size(); c++){
                System.out.print(c + ":" + membership.get(c).size() + "||");
            }
            System.out.println();
        }
    }

    private void calculateCenter(int c){
        Set<Integer> members = membership.get(c);
        T centroid = centroids.get(c);
        centroid.getRatings().clear();
        centroid.setAvgRating(0.0);

        int s = ratingService.getItemMap().size();
        for(int itemID = 1; itemID < s; itemID++){
            int n = 0;
            double rating = 0;
            for(int memberID: members){
                if(ratingService.isRatedById(memberID, itemID)){
                    rating += ratingService.getRating(memberID, itemID);
                    n++;
                }
            }
            if(n != 0){
                centroid.addRating(itemID, rating/n);
            }
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

        for(int i : ratingService.getEntitiesID()){
            if(i < 0)continue;

            double bestSim = -1;
            int closestID = 0;
            T entity = ratingService.getEntity(i);

            for (int c = 0; c < centroids.size() ; c++) {
                double sim = simFunction.calculate(entity, centroids.get(c));
                if (sim > bestSim) {
                    closestID = c;
                    bestSim = sim;
                }
            }
            membership.get(closestID).add(i);
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
                return cluster.stream().toList();
            }
        }
        return null;
    }
}
