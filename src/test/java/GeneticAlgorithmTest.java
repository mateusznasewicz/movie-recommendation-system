import system.recommendation.DatasetLoader;
import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;

import java.util.List;
import java.util.Map;

public class GeneticAlgorithmTest {
    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User> userService = new UserService(users,movies);

        crossoverTest(userService);
    }

    private static void crossoverTest(RatingService<User> userService){
        MatrixFactorization c1 = new MatrixFactorization(userService,10);
        MatrixFactorization c2 = new MatrixFactorization(userService,10);
        List<MatrixFactorization> children = c1.crossover(c2);
    }
}
