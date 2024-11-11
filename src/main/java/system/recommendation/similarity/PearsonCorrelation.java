package system.recommendation.similarity;

import system.recommendation.service.RatingService;
import system.recommendation.models.Entity;

import java.util.Set;

public class PearsonCorrelation<T extends Entity, G extends Entity> implements Similarity<T>{
    private final RatingService<T,G> ratingService;

    public PearsonCorrelation(RatingService<T,G> ratingService){
        this.ratingService = ratingService;
    }

    @Override
    public double calculate(T a, T b) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<Integer> common = a.getCommon(b);
        int aID = a.getId();
        int bID = b.getId();

        for(Integer id: common){
            numerator += (ratingService.getRating(aID,id) - a.getAvgRating())*(ratingService.getRating(bID,id) - b.getAvgRating());
            s1 += Math.pow((ratingService.getRating(aID,id) - a.getAvgRating()),2);
            s2 += Math.pow((ratingService.getRating(bID,id) - b.getAvgRating()),2);
        }
        double denominator = Math.sqrt(s1 * s2);

        if(denominator == 0 || numerator == 0) return -1;
        return numerator/denominator;
    }
}
