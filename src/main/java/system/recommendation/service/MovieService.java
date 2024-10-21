package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;

public class MovieService implements RatingService<Movie> {
    private final Map<Integer, User> users;
    public MovieService(Map<Integer,User> users) {
        this.users = users;
    }

    @Override
    public double getRating(int mID, int uID) {
        return users.get(uID).getRating(mID);
    }

    @Override
    public boolean isRatedById(int mID, int uID) {
        return users.get(uID).hasRating(mID);
    }
}
