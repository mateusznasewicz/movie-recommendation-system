import system.recommendation.DatasetLoader;
import system.recommendation.Error;
import system.recommendation.KnnRecommender;
import system.recommendation.contentbasedfiltering.ContentBasedFiltering;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;

public class ContentBasedFilteringTest {
    public static void run(DatasetLoader datasetLoader){
        RatingService<Movie> rs = new MovieService(datasetLoader.getUsers(),datasetLoader.getMovies());
        KnnRecommender<Movie, User> cbf = new ContentBasedFiltering(datasetLoader,rs,10,true);
        cbf.fillRatings();

        double[][] predictedRatings = cbf.getPredictedRating();
        System.out.println("MAE: " + Error.MAE(predictedRatings,rs));
        System.out.println("RMSE: " + Error.RMSE(predictedRatings,rs));
    }
}
