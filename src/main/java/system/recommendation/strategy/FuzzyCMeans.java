package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.List;
import java.util.Map;

public class FuzzyCMeans<T extends Entity, G extends Entity> extends Clustering {

    double[][] fuzzyMembership;

    public FuzzyCMeans(RatingService ratingService, Similarity simFunction, int k) {
        super(ratingService, simFunction, k);
        Map<Integer, T> entityMap = ratingService.getEntityMap();
        this.fuzzyMembership = new double[entityMap.size()][k];
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
