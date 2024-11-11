import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.MovieService;
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

public class KMeansTest<T extends Entity, G extends Entity> {
    private final static int epochs = 10;
    private final static int k = 10;

    public static void run(DatasetLoader datasetLoader) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        RatingService<Movie, User> rs = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<Movie> sim = new PearsonCorrelation<>(rs);

//        Strategy<Movie> strategy = KMeansTest(rs,sim);
        Strategy<Movie> strategy = FuzzyCMeansTest(rs,sim);

        Recommender<Movie, User> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();
        System.out.println(QualityMeasure.MAE(predicted,rs));
        System.out.println(QualityMeasure.RMSE(predicted,rs));
    }

    public static Strategy<Movie> FuzzyCMeansTest(RatingService<Movie, User> rs, Similarity<Movie> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FuzzyCMeans<Movie,User> x = new FuzzyCMeans<>(rs,sim,k,1.5);
        x.calcCentroids(epochs);
        return x;
    }

    public static Strategy<Movie> KMeansTest(RatingService<Movie, User> rs, Similarity<Movie> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        KMeans<Movie,User> x = new KMeans<>(k,rs,sim);
        x.calcCentroids(epochs);
        return x;
    }
}
