package system.recommendation;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNN;

import java.util.List;
import java.util.Map;

public abstract class KnnRecommender<T extends Entity,G extends Entity> {
    private final RatingService<T> ratingService;
    private final KNN<T,G> knn;
    private final Map<Integer, T> baseHashmap;
    private final Map<Integer, G> itemHashmap;
    private final boolean RATE_ALL;
    private final double[][] predictedRating;

    public KnnRecommender(RatingService<T> ratingService,
                          int k,
                          Map<Integer, T> baseHashmap,
                          Map<Integer, G> itemHashmap,
                          Similarity<T> sim,
                          boolean rateAll
    ) {
        this.ratingService = ratingService;
        this.baseHashmap = baseHashmap;
        this.itemHashmap = itemHashmap;
        this.RATE_ALL = rateAll;
        this.knn = new KNN<>(baseHashmap,k,sim,ratingService);
        this.predictedRating = new double[baseHashmap.size()][itemHashmap.size()];
    }

    public double[][] getPredictedRating(){
        return predictedRating;
    }

    public void fillRatings(){
        for(int eID = 1; eID < baseHashmap.size()+1; eID++){
            if(eID % 100 == 0) System.out.println(eID+"/"+baseHashmap.size());
            for(int iID = 1; iID < itemHashmap.size()+1; iID++){
                if(this.RATE_ALL || !this.ratingService.isRatedById(eID, iID)){
                    T entity = baseHashmap.get(eID);
                    G item = itemHashmap.get(iID);
                    List<T> neighbors = knn.getNeighbors(entity,item);
                    double rating = predict(entity,item,neighbors);
                    predictedRating[eID-1][iID-1] = rating;
                }
            }
        }
    }

    private double predict(T user, G item, List<T> neighbors){
        if(neighbors.isEmpty()) return -1;
        double numerator = 0;
        double denominator = 0;
        double[][] simMatrix = knn.getSimMatrix();
        int uID = user.getId();
        int iID = item.getId();
        for(T neighbor: neighbors){
            int nID = neighbor.getId();
            double sim = simMatrix[uID-1][nID-1];
            numerator += sim * ratingService.getRating(nID,iID);
            denominator += Math.abs(sim);
        }

        return numerator / denominator;
    }
}
