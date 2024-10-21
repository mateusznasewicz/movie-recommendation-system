import system.recommendation.DatasetLoader;
import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.Error;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.UserService;


import java.util.Map;

public class MatrixFactorizationTest {
    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        MatrixFactorization mf = new MatrixFactorization(datasetLoader,0.001,0.0001,1000);
        mf.sgd();
        double[][] predicted = mf.getPredictedRating();
        System.out.println(Error.MAE(predicted,new UserService(users,movies)));
        System.out.println(Error.RMSE(predicted,new UserService(users,movies)));
    }
}
