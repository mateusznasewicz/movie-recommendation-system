import system.recommendation.DatasetLoader;
import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.Error;
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

        sgdTest(userService,users,movies);
    }

    public static void sgdTest(RatingService<User> userService, Map<Integer, User> users, Map<Integer, Movie> movies){
        MatrixFactorization mf = new MatrixFactorization(userService, 10,0.001,0.0001);
        mf.sgd(1000);
        double[][] predicted = mf.getPredictedRating();
        System.out.println(Error.MAE(predicted,new UserService(users,movies)));
        System.out.println(Error.RMSE(predicted,new UserService(users,movies)));
    }
}
