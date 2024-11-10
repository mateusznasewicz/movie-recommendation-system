import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.*;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;

import java.util.Map;

public class MatrixFactorizationTest {
    private final static double learningRate = 0.0002;
    private final static double regularization = 0.02;
    private final static int k = 10;
    private final static int populationSize = 50;
    private final static int epochs = 10000;

    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User,Movie> userService = new UserService(users,movies);
//        NMFtest(userService);
//        RMFtest(userService);
//        MMMFtest(userService);
        RMFGAtest(userService);
    }

    private static void NMFtest(RatingService<User,Movie> userService){
        MatrixFactorization mf = new NMF(userService,k,learningRate,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static void RMFtest(RatingService<User,Movie> userService){
        MatrixFactorization mf = new RMF(userService,k,learningRate,regularization,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static void MMMFtest(RatingService<User,Movie> userService){
        MatrixFactorization mf = new MMMF(userService,k,learningRate,regularization,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static void RMFGAtest(RatingService<User,Movie> userService){
        RMFGA mf = new RMFGA(userService,k,learningRate,regularization);
        RMF best = mf.run(populationSize,epochs);

        double[][] ratings = best.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
        System.out.println(best.fitness());
    }
}
