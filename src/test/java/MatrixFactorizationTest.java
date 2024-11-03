import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.MMMF;
import system.recommendation.matrixfactorization.MatrixFactorization;
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
        RatingService<User> userService = new UserService(users,movies);

        MatrixFactorization mf = new MMMF(userService,10,0.001,0.0001);
        mf.sgd(100);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));

    }
}
