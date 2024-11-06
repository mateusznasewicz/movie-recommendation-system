package system.recommendation.similarity;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.Set;

public class AdjustedCosine<T extends  Entity, G extends Entity> implements Similarity<T> {
    private final RatingService<T,G> ratingService;

    public AdjustedCosine(RatingService<T,G> ratingService){
        this.ratingService = ratingService;
    }
    @Override
    public double calculate(T a, T b) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<Integer> common = a.getCommon(b);
        if(common.isEmpty()) return 0;

        for(Integer id: common){
            int aID = a.getId();
            int bID = b.getId();
            double avg = ratingService.getAvg(id);
            double r1 = ratingService.getRating(aID,id);
            double r2 = ratingService.getRating(bID,id);
            numerator += (r1-avg)*(r2 - avg);
            s1 += Math.pow((r1 - avg),2);
            s2 += Math.pow((r2 - avg),2);
        }
        double denominator = Math.sqrt(s1 * s2);

        if(denominator == 0 || numerator == 0) return -1;
        return numerator/denominator;
    }
}
