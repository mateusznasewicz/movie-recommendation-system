package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class FuzzyCMeans<T extends Entity, G extends Entity> extends Clustering<T,G> {

    double[][] fuzzyMembership;
    private double fuzzines = 2;

    public FuzzyCMeans(RatingService<T,G> ratingService, Similarity<T> simFunction, int k) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService, simFunction, k);
        Map<Integer, T> entityMap = ratingService.getEntityMap();
        this.fuzzyMembership = new double[entityMap.size()][k];


        calcCentroids(10);
    }

    @Override
    protected void step() {
        calcFuzzyMembership();
        for(int c = 0; c < centroids.size(); c++){
            calculateCenter(c);
        }
    }

    private void calculateCenter(int c){
        T centroid = centroids.get(c);
        double denominator = 0;

        for(int u = 0; u < fuzzyMembership.length; u++){
            denominator += Math.pow(fuzzyMembership[u][c],fuzzines);
        }

        int s = ratingService.getItemMap().size();
        for(int itemID = 0 ; itemID < s; itemID++){
            double rating = -1;
            for(int u = 0; u < fuzzyMembership.length; u++){
                if(ratingService.isRatedById(u+1, itemID+1)){
                    rating += ratingService.getRating(u+1, itemID+1) * Math.pow(fuzzyMembership[u][c],fuzzines);
                }
            }
            if(rating != -1){
                centroid.setRating(itemID, rating/denominator);
            }
        }
    }

    private void calcFuzzyMembership() {
        for(int i = 0; i < fuzzyMembership.length; i++) {
            T entity = ratingService.getEntity(i+1);
            for(int j = 0; j < fuzzyMembership[i].length; j++) {
                T c1  = centroids.get(j);
                double sum = 0;
                double d1 = distFunction.calculate(entity, c1);
                for(int k = 0; k < fuzzyMembership[i].length; k++) {
                    T c2 = centroids.get(k);
                    double d2 = distFunction.calculate(entity, c2);
                    double pow = 2/(fuzzines-1);
                    sum += Math.pow((d1/d2),pow);
                }
                fuzzyMembership[i][j] = 1 / sum;
            }
        }
    }

    public void bestCluster(int n){

    }

    public void fewClusters(int c, int n){

    }

    @Override
    public List<Integer> getNeighbors(Entity item) {
        return List.of();
    }
}
