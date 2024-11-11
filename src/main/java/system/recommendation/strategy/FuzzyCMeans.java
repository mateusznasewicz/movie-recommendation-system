package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FuzzyCMeans<T extends Entity, G extends Entity> extends Clustering<T,G>  {

    private double[][] fuzzyMembership;
    private double fuzzines = 1.7;

    public FuzzyCMeans(RatingService<T,G> ratingService, Similarity<T> simFunction, int k) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService, simFunction, k);
        Map<Integer, T> entityMap = ratingService.getEntityMap();
        this.fuzzyMembership = new double[entityMap.size()][k];
    }

    @Override
    protected void step() {
        calcFuzzyMembership();
        for(int c = 0; c < centroids.size(); c++){
            calculateCenter(c);
        }
    }

    @Override
    protected double calcLoss(){
        double loss = 0;
        for(int i = 0; i <  fuzzyMembership.length; i++){
            T entity = ratingService.getEntity(i+1);
            for(int j = 0; j < centroids.size(); j++){
                T centroid = centroids.get(j);
                loss += distFunction.calculate(entity,centroid) * fuzzyMembership[i][j];
            }
        }
        return loss;
    }

    private void calculateCenter(int c){
        T centroid = centroids.get(c);
        int s = ratingService.getItemMap().size();
        for(int itemID = 0 ; itemID < s; itemID++){
            double rating = -1;
            double denominator = 0;
            for(int u = 0; u < fuzzyMembership.length; u++){
                if(!ratingService.isRatedById(u+1, itemID+1))continue;
                double w = Math.pow(fuzzyMembership[u][c],fuzzines);
                rating += ratingService.getRating(u+1, itemID+1) * w;
                denominator += w;
            }
            if(rating != -1){
                centroid.setRating(itemID+1, rating/denominator);
            }
        }
    }

    private void calcFuzzyMembership() {
        double pow = 2/(fuzzines-1);
        for(int i = 0; i < fuzzyMembership.length; i++) {
            T entity = ratingService.getEntity(i+1);
            for(int j = 0; j < fuzzyMembership[i].length; j++) {
                T c1  = centroids.get(j);
                double sum = 0;
                double d1 = distFunction.calculate(entity, c1);
                for(int k = 0; k < fuzzyMembership[i].length; k++) {
                    T c2 = centroids.get(k);
                    double d2 = distFunction.calculate(entity, c2);
                    sum += Math.pow((d1/d2),pow);
                }
                fuzzyMembership[i][j] = 1 / sum;
            }
        }
        for(double[] membership : fuzzyMembership){
            System.out.println(Arrays.toString(membership));
        }

    }

    private List<Integer> getNeighborsFromCluster(int n, int c, int id){
        Queue<Integer> neighbors = new PriorityQueue<>(n, (a,b) -> Double.compare(fuzzyMembership[a-1][c],fuzzyMembership[b-1][c]));

        for(int i = 0; i < fuzzyMembership.length; i++){
            if(i == id - 1) continue;
            neighbors.add(i+1);
            if(neighbors.size() > n) neighbors.poll();
        }

        return neighbors.stream().toList();
    }

    private List<Integer> getClusters(int n, int id){
        Queue<Integer> clusters = new PriorityQueue<>(n, (a,b) -> Double.compare(fuzzyMembership[id-1][a],fuzzyMembership[id-1][b]));

        for(int i = 0; i < centroids.size(); i++){
            clusters.add(i);
            if(clusters.size() > n) clusters.poll();
        }

        return clusters.stream().toList();
    }

    public List<Integer> bestCluster(int n, T item){
        int id = item.getId();
        double[] membership = fuzzyMembership[id-1];

        int cluster = 0;
        double best = membership[0];
        for(int i = 0; i < membership.length; i++){
            if(membership[i] < best){
                cluster = i;
                best = membership[i];
            }
        }
        return getNeighborsFromCluster(n, cluster, id);
    }

    public List<Integer> fewClusters(int c, int n, T item){
        int id = item.getId();
        List<Integer> neighbors = new ArrayList<>();
        List<Integer> clusters = getClusters(c, id);

        for(int cluster: clusters){
            neighbors.addAll(getNeighborsFromCluster(n, cluster, id));
        }

        return neighbors;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        return bestCluster(50,item);
//        return bestCluster(50,item);
    }
}
