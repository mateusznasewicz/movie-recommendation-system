import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.AdjustedCosine;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNNGA;
import system.recommendation.strategy.SimGa;
import system.recommendation.strategy.Strategy;

public class KNNGATEST {
    private static int populationSize = 50;
    private static int epochs = 50;
    private static int k = 10;

    public static void run(DatasetLoader datasetLoader){
        RatingService<User,Movie> rs = new UserService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<User> sim = new AdjustedCosine<>(rs);
        ver1(rs,sim);
    }

    public static void ver1(RatingService<User,Movie> rs, Similarity<User> sim){
        Strategy<User> strategy = new KNNGA<>(rs,sim,50,50,50);
        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();

        System.out.println(QualityMeasure.MAE(predicted,rs,false));
        System.out.println(QualityMeasure.RMSE(predicted,rs));
    }
//
//    public static void ver2(RatingService<User,Movie> rs, Similarity<User> sim){
//        SimGa<User,Movie> simGa = new SimGa<>(rs,populationSize,k,epochs);
//        Strategy<User> strategy = simGa.run();
//
//        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
//        double[][] predicted = recommender.getPredictedRating();
//
//        System.out.println(QualityMeasure.MAE(predicted,rs,false));
//        System.out.println(QualityMeasure.RMSE(predicted,rs));
//    }
}
