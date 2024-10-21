import system.recommendation.DatasetLoader;
import system.recommendation.collaborativefiltering.CollaborativeFiltering;
import system.recommendation.collaborativefiltering.ItemBased;
import system.recommendation.collaborativefiltering.UserBased;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;

import java.io.FileNotFoundException;
import java.util.Map;

public class CollaborativeFilteringTest {
    public static void run(DatasetLoader datasetLoader) throws FileNotFoundException {
        //userBased(datasetLoader);
        itemBased(datasetLoader);
    }

    public static void userBased(DatasetLoader datasetLoader) throws FileNotFoundException {
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();

        RatingService<User> rs = new UserService(users);
        Similarity<User> sim = new PearsonCorrelation<>(rs);
        CollaborativeFiltering<User,Movie> cf = new UserBased(users,movies, 10, sim,rs,true);
        cf.fillRatings();

        double[][] predictedRatings = cf.getPredictedRating();
        users.forEach((uID,user)->{
            movies.forEach((mID,_) ->{
                if(user.hasRating(mID)){
                    System.out.println(predictedRatings[uID-1][mID-1]+","+user.getRating(mID));
                }
            });
        });
    }

    public static void itemBased(DatasetLoader datasetLoader) throws FileNotFoundException {
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();

        RatingService<Movie> rs = new MovieService(users);
        Similarity<Movie> sim = new PearsonCorrelation<>(rs);
        CollaborativeFiltering<Movie,User> cf = new ItemBased(movies,users, 10, sim,rs,true);
        cf.fillRatings();

        double[][] predictedRatings = cf.getPredictedRating();
        users.forEach((uID,user)->{
            movies.forEach((mID,_) ->{
                if(user.hasRating(mID)){
                    System.out.println(predictedRatings[mID-1][uID-1]+","+user.getRating(mID));
                }
            });
        });
    }
}
