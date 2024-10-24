package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public class MovieService extends RatingService<Movie> {
    public MovieService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        super(users,movies);
    }

    @Override
    public double getRating(int mID, int uID) {
        return users.get(uID).getRating(mID);
    }

    @Override
    public double getAvg(int id) {
        return users.get(id).getAvgRating();
    }

    @Override
    public boolean isRatedById(int mID, int uID) {
        return users.get(uID).hasRating(mID);
    }

    @Override
    public Set<Integer> getEntities(int itemID) {
        return users.get(itemID).getRatings().keySet();
    }

    @Override
    public Movie getEntity(int id) {
        return this.movies.get(id);
    }
}
