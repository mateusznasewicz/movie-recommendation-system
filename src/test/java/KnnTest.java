import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;

public class KnnTest {
    public static void run(DatasetLoader datasetLoader){
        RatingService<User, Movie> rs = new UserService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<User> sim = new PearsonCorrelation<>(rs);
        Strategy<User> strategy = new KNN<>(datasetLoader.getUsers(), 50, sim);
        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();

        System.out.println(QualityMeasure.MAE(predicted,rs));
        System.out.println(QualityMeasure.RMSE(predicted,rs));
    }
}
