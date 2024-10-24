import system.recommendation.DatasetLoader;
import system.recommendation.Error;
import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.ChromosomeProvider;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.geneticalgorithm.MatrixProvider;
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
        ChromosomeProvider matrixProvider = new MatrixProvider(userService,10,0.001,0.0001);
        GeneticAlgorithm ga = new GeneticAlgorithm(100,0.5,0.8,10,matrixProvider);
        Chromosome best = ga.run();

        double[][] predicted = ((MatrixFactorization) best).getPredictedRating();
        System.out.println(Error.MAE(predicted,userService));
        System.out.println(Error.RMSE(predicted,userService));
    }

    private static void crossoverTest(RatingService<User> userService){

    }
}
