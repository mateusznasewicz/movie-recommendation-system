package system.recommendation.strategy;


import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("unchecked")
public class KMeans<T extends Entity, G extends Entity> extends Clustering<T,G>{
    private List<Set<Integer>> membership;

    //do przewidywania
    public KMeans(int k,RatingService<T, G> ratingService, Similarity<T> simFunction) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService, simFunction, k);
        initCentroids();
        assignMembership();
    }

    //nowe w pso
    public KMeans(int k,RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);
        initCentroids();
        assignMembership();
    }

    //kopiowanie w pso
    public KMeans(List<Set<Integer>> membership, List<T> centroids, int k, RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);

        this.membership = new ArrayList<>();
        for(Set<Integer> members: membership){
            this.membership.add(new HashSet<>(members));
        }
        Class<T> clazz = (Class<T>) centroids.getFirst().getClass();
        for(T centroid: centroids){
            T c = clazz.getConstructor(int.class,double.class,HashMap.class).newInstance(centroid.getId(),centroid.getAvgRating(),centroid.getRatings());
            this.centroids.add(c);
        }
    }

    public List<Set<Integer>> getMembership() {
        return membership;
    }

    public double getRatingCentroid(int c, int i){
        return centroids.get(c).getRating(i);
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

    public double psoLoss(){
        double loss = 0;
        for(int c = 0; c < centroids.size(); c++){
            T centroid = centroids.get(c);
            double sum = 0;
            for(Integer u: membership.get(c)){
                T member = ratingService.getEntity(u);
                sum += distFunction.calculate(member,centroid);
            }
            loss += sum / membership.get(c).size();
        }
        return loss / centroids.size();
    }

    @Override
    protected void step(){
        assignMembership();

        for(int c = 0; c < centroids.size(); c++){
            calculateCenter(c);
        }
    }

    public void assignMembership(){
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

    public void updateParticle(double[][] v){
        for(int c = 0; c < v.length; c++){
            for(int i = 0; i < v[c].length; i++){
                double r = getRatingCentroid(c,i+1);
                centroids.get(c).setRating(i+1,v[c][i]+r);
            }
        }
    }

    public void updateVelocity(double[][] v, KMeans<T,G> local, KMeans<T,G> global){
        double r1 = 0.127;
        double r2 = 0.0975;
        double c1 = 1.42;
        double c2 = 1.42;
        double w = 0.72;

        for(int c = 0; c < v.length; c++){
            for(int i = 0; i < v[c].length; i++){
                double s1 = local.getRatingCentroid(c,i+1) - getRatingCentroid(c,i+1);
                double s2 = global.getRatingCentroid(c,i+1) - getRatingCentroid(c,i+1);
                v[c][i] = v[c][i]*w + c1*r1*s1 + c2*r2*s2;
            }
        }
    }
}
