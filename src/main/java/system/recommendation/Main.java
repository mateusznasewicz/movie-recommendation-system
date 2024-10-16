package system.recommendation;

import system.recommendation.collaborative_filtering.CollaborativeFiltering;
import system.recommendation.collaborative_filtering.ItemBased;
import system.recommendation.collaborative_filtering.UserBased;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.io.FileNotFoundException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small");
//        CollaborativeFiltering<User> cf = new UserBased(datasetLoader,20,true);

        CollaborativeFiltering<Movie> cf = new ItemBased(datasetLoader,20,true);
        cf.fillRatings();
        User user = datasetLoader.getUsers().get(1);
        System.out.println(user.getPredictedRatings());
        user.getRatings().forEach((id,rating)->{
            Map<Integer, Double> predictions = user.getPredictedRatings();
            if(predictions.containsKey(id)){
                System.out.println(rating + " , " + predictions.get(id));
            }
        });
    }
}