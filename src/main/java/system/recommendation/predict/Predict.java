package system.recommendation.predict;

import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.similarity.Similarity;

import java.util.List;

public class Predict {
    public static double predictRatingKNN(User user, Movie movie, List<User> neighbors, Similarity<User> similarity) {
        double numerator = 0;
        double denominator = 0;
        int movieId = movie.getId();

        for(User neighbor: neighbors){
            if(!movie.getRatedByUsers().contains(neighbor)) continue;
            numerator += similarity.calculate(user,neighbor) * (neighbor.getRating(movieId) - neighbor.getAvgRating());
            denominator += Math.abs(similarity.calculate(user,neighbor));
        }

        return user.getAvgRating() + numerator / denominator;
    }

    public static double predictRatingKNN(Movie movie, User user, List<Movie> neighbors, Similarity<Movie> similarity) {
        double numerator = 0;
        double denominator = 0;
        for(Movie neighbor: neighbors){
            if(!neighbor.getRatedByUsers().contains(user)) continue;
            int movieId = neighbor.getId();
            numerator += similarity.calculate(movie, neighbor) * user.getRating(movieId);
            denominator += Math.abs(similarity.calculate(movie,neighbor));
        }

        return numerator / denominator;
    }
}
