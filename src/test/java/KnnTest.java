import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.ContentBasedFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.*;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;


import java.io.IOException;

public class KnnTest {
    private static int k = 10;

    public static void run(DatasetLoader datasetLoader) throws IOException {
        RatingService<User, Movie> rs_userbased = new UserService(datasetLoader.getUsers(), datasetLoader.getMovies());
        RatingService<Movie, User> rs_itembased = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());

        Similarity<User> ac = new AdjustedCosine<>(rs_userbased);
        Similarity<User> pc = new PearsonCorrelation<>(rs_userbased);
        Similarity<User> ed = new EuclideanDistance<>(rs_userbased);
        Strategy<User> strategy = new KNN<>(datasetLoader.getUsers(), k, ac);
        CollaborativeFilteringTest(rs_userbased,strategy);
    }

    private static void CollaborativeFilteringTest(RatingService<User, Movie> rs, Strategy<User> strategy){
        Recommender<User, Movie> recommender = new CollaborativeFiltering<>(rs,strategy);
        double[][] predicted = recommender.getPredictedRating();
        double[] e = new double[]{QualityMeasure.MAE(predicted,rs,false),QualityMeasure.RMSE(predicted,rs)};
        System.out.println(e[0]);
    }

    //zmienić datasetloader na itembased w tests/main
    private static void ContentBasedFilteringTest(DatasetLoader datasetLoader,RatingService<Movie, User> rs, Strategy<Movie> strategy){
        Recommender<Movie, User> recommender = new ContentBasedFiltering(datasetLoader,rs,strategy);
        double[][] predicted = recommender.getPredictedRating();
        double[] e = new double[]{QualityMeasure.MAE(predicted,rs,false),QualityMeasure.RMSE(predicted,rs)};
        System.out.println(e[0]);
    }
}
