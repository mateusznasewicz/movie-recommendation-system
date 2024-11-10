import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.MMMF;
import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.matrixfactorization.NMF;
import system.recommendation.matrixfactorization.RMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;

import java.util.Map;

public class MatrixFactorizationTest {
    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User,Movie> userService = new UserService(users,movies);
//        NMFtest(userService,1000);
//        RMFtest(userService,50);
        MMMFtest(userService,50);
    }

    private static void NMFtest(RatingService<User,Movie> userService, int epochs){
        MatrixFactorization mf = new NMF(userService,10,0.0002);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static void RMFtest(RatingService<User,Movie> userService, int epochs){
        MatrixFactorization mf = new RMF(userService,10,0.01,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static void MMMFtest(RatingService<User,Movie> userService, int epochs){
        MatrixFactorization mf = new MMMF(userService,10,0.01,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }
}
