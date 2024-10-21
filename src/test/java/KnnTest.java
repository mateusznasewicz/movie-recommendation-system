import system.recommendation.DatasetLoader;
import system.recommendation.KNN;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class KnnTest {

    public static void run(DatasetLoader datasetLoader) {
        testQueue(datasetLoader);
    }

    public static void testQueue(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User> rs = new UserService(users,movies);
        Similarity<User> sim = new PearsonCorrelation<>(rs);
        KNN<User,Movie> knn = new KNN<>(users,10,sim,rs);
        User user = users.get(1);
        Movie movie = movies.get(1);
        List<User> neighbors = knn.getNeighbors(user,movie);
        System.out.println(neighbors.size());
        neighbors.forEach(n -> {
            double s = sim.calculate(n,user);
            System.out.println(s);
        });
    }
}
