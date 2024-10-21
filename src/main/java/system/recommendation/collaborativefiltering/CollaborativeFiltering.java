package system.recommendation.collaborativefiltering;

import system.recommendation.KNN;
import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.*;

public abstract class CollaborativeFiltering<T extends Entity, G extends Entity>{
    private final Map<Integer, T> baseHashmap;
    private final Map<Integer, G> itemHashmap;
    private final RatingService<T> ratingService;
    private final KNN<T,G> knn;
    private final double[][] predictedRating;
    private final boolean RATE_ALL;

    public CollaborativeFiltering(Map<Integer, T> baseHashmap, Map<Integer,G> itemHashmap, int k, Similarity<T> similarity, RatingService<T> ratingService, boolean RATE_ALL){
        this.baseHashmap = baseHashmap;
        this.itemHashmap = itemHashmap;
        this.knn = new KNN<>(this.baseHashmap, k, similarity,ratingService);
        this.RATE_ALL = RATE_ALL;
        this.ratingService = ratingService;
        this.predictedRating = new double[baseHashmap.size()][itemHashmap.size()];
    }

    public double predictRating(T user, G item, List<T> neighbors) {
        double numerator = 0;
        double denominator = 0;
        double[][] simMatrix = knn.getSimMatrix();
        for(T neighbor: neighbors){
            int nID = neighbor.getId();
            int uID = user.getId();
            int iID = item.getId();
            double sim = simMatrix[uID-1][nID-1];

            numerator += sim * ratingService.getRating(nID,iID);
            denominator += Math.abs(sim);
        }

        if(numerator == 0 || denominator == 0) return -1;
        return numerator / denominator;
    }

    public void fillRatings(){
        for(int eID = 1; eID < baseHashmap.size()+1; eID++){
            for(int iID = 1; iID < itemHashmap.size()+1; iID++){
                if(this.RATE_ALL || !this.ratingService.isRatedById(eID, iID)){
                    T entity = baseHashmap.get(eID);
                    G item = itemHashmap.get(iID);
                    List<T> neighbors = knn.getNeighbors(entity,item);
                    double rating = predictRating(entity,item,neighbors);
                    predictedRating[eID-1][iID-1] = rating;
                }
            }
        }
    }

    public double[][] getPredictedRating(){
        return predictedRating;
    }
}
