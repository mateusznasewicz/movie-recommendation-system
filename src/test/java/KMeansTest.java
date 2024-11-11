import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.Clustering;
import system.recommendation.strategy.FuzzyCMeans;
import system.recommendation.strategy.KMeans;
import system.recommendation.strategy.Strategy;

import java.lang.reflect.InvocationTargetException;

public class KMeansTest {
    private final static int epochs = 10;
    private final static int k = 10;

    public static void run(DatasetLoader datasetLoader) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RatingService<User, Movie> rs = new UserService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<User> sim = new PearsonCorrelation<>(rs);

//        Strategy<User> strategy = KMeansTest(rs,sim);
        Strategy<User> strategy = FuzzyCMeansTest(rs,sim);

        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();
        System.out.println(QualityMeasure.MAE(predicted,rs));
        System.out.println(QualityMeasure.RMSE(predicted,rs));
    }

    public static Strategy<User> FuzzyCMeansTest(RatingService<User, Movie> rs, Similarity<User> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FuzzyCMeans<User,Movie> x = new FuzzyCMeans<>(rs,sim,k);
        x.calcCentroids(epochs);
        return x;
    }

    public static Strategy<User> KMeansTest(RatingService<User, Movie> rs, Similarity<User> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        KMeans<User,Movie> x = new KMeans<>(k,rs,sim);
        x.calcCentroids(epochs);
        return x;
    }
}
