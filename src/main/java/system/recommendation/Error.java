package system.recommendation;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class Error {
    public static <T> double MAE(double[][] predicted, RatingService<T> ratingService){
        double error = 0;
        int n = 0;

        for(int i = 0; i < predicted.length; i++){
            for(int j = 0; j < predicted[i].length; j++){
                double pr = predicted[i][j];
                if(pr == -1 || !ratingService.isRatedById(i+1,j+1)) continue;
                error += Math.abs(pr - ratingService.getRating(i+1,j+1));
                n++;
            }
        }

        return error/n;
    }

    public static <T> double RMSE(double[][] predicted, RatingService<T> ratingService){
        double error = 0;
        int n = 0;

        for(int i = 0; i < predicted.length; i++){
            for(int j = 0; j < predicted[i].length; j++){
                double pr = predicted[i][j];
                if(pr == -1 || !ratingService.isRatedById(i+1,j+1)) continue;
                error += Math.pow(pr - ratingService.getRating(i+1,j+1),2);
                n++;
            }
        }

        return Math.sqrt(error/n);
    }
}
