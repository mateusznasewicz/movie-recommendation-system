package system.recommendation.strategy;


import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KMeans<T extends Entity, G extends Entity> extends Clustering<T,G>{
    private List<Set<Integer>> membership;

    public KMeans(int k,RatingService<T, G> ratingService, Similarity<T> simFunction){
        super(ratingService, simFunction, k);
    }

    public KMeans(int k,RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);
    }

    public KMeans(List<Set<Integer>> membership, List<T> centroids, int k, RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);

        this.membership = new ArrayList<>();
        for(Set<Integer> members: membership){
            membership.add(new HashSet<>(members));
        }

        for(T centroid: centroids){
//            this.centroids.add();
        }
    }

    public List<Set<Integer>> getMembership() {
        return membership;
    }

    private void calculateCenter(int c){
        Set<Integer> members = membership.get(c);
        T centroid = centroids.get(c);
        centroid.clear();

        int s = ratingService.getItemMap().size();
        for(int itemID = 1; itemID < s+1; itemID++){
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

    @Override
    public double calcLoss(){
        double loss = 0;
        for(int c = 0; c < centroids.size(); c++){
            T centroid = centroids.get(c);
            for(Integer u: membership.get(c)){
                T entity = ratingService.getEntity(u);
                loss += distFunction.calculate(entity,centroid);
            }
        }
        return loss;
    }

    @Override
    protected void step(){
        initMembership();

        for(int i : ratingService.getEntitiesID()){
            if(i < 0)continue;

            double bestDist = Double.MAX_VALUE;
            int closestID = 0;
            T entity = ratingService.getEntity(i);

            for (int c = 0; c < centroids.size() ; c++) {
                double dist = distFunction.calculate(entity, centroids.get(c));
                if (dist < bestDist) {
                    closestID = c;
                    bestDist = dist;
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

    public void updateParticle(){

    }

    public void updateVelocity(KMeans<T,G> v){
        double r1 = 0.127;
        double r2 = 0.0975;
        double c1 = 1.42;
        double c2 = 1.42;
        double w = 0.72;
    }
}
