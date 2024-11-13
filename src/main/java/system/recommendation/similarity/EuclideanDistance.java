package system.recommendation.similarity;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.Set;

public class EuclideanDistance<T extends Entity, G extends Entity> implements Similarity<T>{
    private final RatingService<T,G> ratingService;

    public EuclideanDistance(RatingService<T,G> ratingService){
        this.ratingService = ratingService;
    }

    @Override
    public double calculate(T a, T b) {
        double result = 0;
        Set<Integer> common = a.getCommon(b);
        if(common.isEmpty()) {
            //System.out.println("KJHSDFJHKFSDKJHFSD");
            return Double.MAX_VALUE;
        }

        int aID = a.getId();
        int bID = b.getId();

        for(Integer id: common){
            double r1 = ratingService.getRating(aID,id);
            double r2 = ratingService.getRating(bID,id);
            result += Math.pow(r1-r2,2);
        }

        return Math.sqrt(result);
    }
}
