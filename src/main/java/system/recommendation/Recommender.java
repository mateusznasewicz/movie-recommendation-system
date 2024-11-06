package system.recommendation;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;

import java.util.List;
import java.util.Map;

public abstract class Recommender<T extends Entity,G extends Entity> {
    private final RatingService<T> ratingService;
    private final Strategy<T> strategy;
    private final Map<Integer, T> baseHashmap;
    private final Map<Integer, G> itemHashmap;
    private final double[][] predictedRating;

    public Recommender(RatingService<T> ratingService,
                       Map<Integer, T> baseHashmap,
                       Map<Integer, G> itemHashmap,
                       Strategy<T> strategy
                       ) {
        this.ratingService = ratingService;
        this.baseHashmap = baseHashmap;
        this.itemHashmap = itemHashmap;
        this.strategy = strategy;
        this.predictedRating = new double[baseHashmap.size()][itemHashmap.size()];
    }

    public double[][] getPredictedRating(){
        return predictedRating;
    }

    public void fillRatings(){
        for(int eID = 1; eID < baseHashmap.size()+1; eID++){
            T entity = baseHashmap.get(eID);
            List<T> neighbors = strategy.getNeighbors(entity);
            for(int iID = 1; iID < itemHashmap.size()+1; iID++){
                if(this.ratingService.isRatedById(eID, iID)){
                    G item = itemHashmap.get(iID);
                    double rating = predict(entity,item,neighbors);
                    predictedRating[eID-1][iID-1] = rating;
                }
            }
        }
    }

    private double predict(T user, G item, List<T> neighbors){
        double numerator = 0;
        double denominator = 0;
        double[][] simMatrix = strategy.getSimMatrix();
        int uID = user.getId();
        int iID = item.getId();
        for(T neighbor: neighbors){
            int nID = neighbor.getId();
            if(!ratingService.isRatedById(uID, iID))continue;

            double sim = simMatrix[uID-1][nID-1];
            numerator += sim * ratingService.getRating(nID,iID);
            denominator += Math.abs(sim);
        }

        if(numerator == 0) return -1;
        return numerator / denominator;
    }
}
