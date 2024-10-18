package system.recommendation;

import system.recommendation.collaborativefiltering.CollaborativeFiltering;
import system.recommendation.collaborativefiltering.ItemBased;
import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.io.FileNotFoundException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small");
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        Map<Integer, User> users = datasetLoader.getUsers();
        MatrixFactorization mf = new MatrixFactorization(datasetLoader,0.001,0.0001, 100);

        mf.sgd();
        int uid = 1;
        int mid = 3;
        Movie movie = movies.get(mid);
        User user = users.get(uid);
        System.out.println(MatrixFactorization.vectorMultiplication(movie.getLatentFeatures(), user.getLatentFeatures()));
        System.out.println(user.getRating(mid));
    }
}