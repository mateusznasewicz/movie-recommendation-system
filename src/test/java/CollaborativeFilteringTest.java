import system.recommendation.DatasetLoader;
import system.recommendation.collaborativefiltering.CollaborativeFiltering;
import system.recommendation.collaborativefiltering.ItemBased;
import system.recommendation.collaborativefiltering.UserBased;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.Error;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.AdjustedCosine;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;

import java.util.Map;

public class CollaborativeFilteringTest {
    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User> rsu = new UserService(users,movies);
        RatingService<Movie> rsm = new MovieService(users);
        userBased(datasetLoader,new AdjustedCosine<>(rsu),rsu);
        itemBased(datasetLoader,new AdjustedCosine<>(rsm),rsm);
    }

    public static void userBased(DatasetLoader datasetLoader,Similarity<User> sim, RatingService<User> rs){
        CollaborativeFiltering<User,Movie> cf = new UserBased(datasetLoader, 10, sim,rs,true);
        cf.fillRatings();

        double[][] predictedRatings = cf.getPredictedRating();
        System.out.println("MAE: " + Error.MAE(predictedRatings,rs));
        System.out.println("RMSE: " + Error.RMSE(predictedRatings,rs));
    }

    public static void itemBased(DatasetLoader datasetLoader,Similarity<Movie> sim, RatingService<Movie> rs){

        CollaborativeFiltering<Movie,User> cf = new ItemBased(datasetLoader, 10, sim,rs,true);
        cf.fillRatings();

        double[][] predictedRatings = cf.getPredictedRating();
        System.out.println("MAE: " + Error.MAE(predictedRatings,rs));
        System.out.println("RMSE: " + Error.RMSE(predictedRatings,rs));
    }
}
