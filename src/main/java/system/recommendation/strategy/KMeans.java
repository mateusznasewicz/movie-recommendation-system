package system.recommendation.strategy;


import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KMeans<T extends Entity, G extends Entity> extends Clustering<T,G> {
    private List<Set<Integer>> membership;

    public KMeans(int k, int epochs,RatingService<T, G> ratingService, Similarity<T> simFunction) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService, simFunction, k);
    }

    private void calculateCenter(int c){
        Set<Integer> members = membership.get(c);
        T centroid = centroids.get(c);

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
                centroid.setRating(itemID, rating/n);
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
