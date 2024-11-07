package system.recommendation;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.HashMap;
import java.util.Map;


public class QualityMeasure {
    public static <T extends Entity, G extends Entity> double MAE(double[][] predicted, RatingService<T,G> ratingService){
        double error = 0;
        int n = 0;
        int total = 0;
        for(int u: ratingService.getEntitiesID()){
            HashMap<Integer,Double> ratings = ratingService.getEntity(u).getTestRatings();
            total += ratings.size();
            for(Map.Entry<Integer, Double> entry: ratings.entrySet()){
                int m = entry.getKey()-1;
                double rating = entry.getValue();
                double pr = predicted[u-1][m];
                if(pr == -1) continue;

                error += Math.abs(pr - rating);
                n++;
            }
        }
        System.out.println(n+"/"+total);
        return error/n;
    }

    public static <T extends Entity, G extends Entity> double RMSE(double[][] predicted, RatingService<T,G> ratingService){
        double error = 0;
        int n = 0;

        for(int u: ratingService.getEntitiesID()){
            HashMap<Integer,Double> ratings = ratingService.getEntity(u).getTestRatings();
            for(Map.Entry<Integer, Double> entry: ratings.entrySet()){
                int m = entry.getKey()-1;
                double rating = entry.getValue();
                double pr = predicted[u-1][m];
                if(pr == -1) continue;

                error += Math.pow(pr - rating,2);
                n++;
            }
        }

        return Math.sqrt(error/n);
    }
}
