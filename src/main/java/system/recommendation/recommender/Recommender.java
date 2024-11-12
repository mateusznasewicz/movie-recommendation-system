package system.recommendation.recommender;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.Strategy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Recommender<T extends Entity,G extends Entity> {
    private final RatingService<T,G> ratingService;
    private final Strategy<T> strategy;
    private final Map<Integer, T> baseHashmap;
    private final double[][] predictedRating;

    public Recommender(RatingService<T,G> ratingService, Strategy<T> strategy) {
        this.ratingService = ratingService;
        this.baseHashmap = ratingService.getEntityMap();
        this.strategy = strategy;
        this.predictedRating = new double[baseHashmap.size()][ratingService.getItemMap().size()];
    }



    public double[][] getPredictedRating(){
        fillRatings();
        return predictedRating;
    }

    private void fillRatings(){
        for(int eID : baseHashmap.keySet()){
            T entity = baseHashmap.get(eID);
            List<Integer> neighbors = strategy.getNeighbors(entity);
            for(Integer iID :entity.getTestRatings().keySet()){
                double rating = predict(eID,iID,neighbors);
                predictedRating[eID-1][iID-1] = rating;
            }
        }
    }

    public double predict(int eID, int iID, List<Integer> neighbors){
        double numerator = 0;
        double denominator = 0;
        double[][] simMatrix = strategy.getSimMatrix();

        for(Integer nID: neighbors){
            double sim = simMatrix[eID-1][nID-1];
            if(!ratingService.isRatedById(nID, iID) || sim < 0) continue;
            numerator += sim * ratingService.getRating(nID,iID);
            denominator += sim;
        }

        if(numerator == 0) return -1;
        return numerator / denominator;
    }
}
