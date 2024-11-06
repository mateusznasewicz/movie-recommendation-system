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
import system.recommendation.strategy.KNNGA;
import system.recommendation.strategy.Strategy;

public class KNNGATEST {
    public static void run(DatasetLoader datasetLoader){
        RatingService<User,Movie> rs = new UserService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<User> sim = new PearsonCorrelation<>(rs);
        Strategy<User> strategy = new KNNGA<>(rs,sim,50,10,50);
        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();

        System.out.println(QualityMeasure.MAE(predicted,rs));
        System.out.println(QualityMeasure.RMSE(predicted,rs));
    }
}
