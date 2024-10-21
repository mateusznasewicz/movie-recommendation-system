package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;

public class UserService implements RatingService<User> {
    private final Map<Integer, User> users;
    private final Map<Integer, Movie> movies;
    public UserService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        this.movies = movies;
        this.users = users;
    }

    @Override
    public double getRating(int uID, int mID) {
        return users.get(uID).getRating(mID);
    }

    @Override
    public double getAvg(int id) {
        return movies.get(id).getAvgRating();
    }

    @Override
    public boolean isRatedById(int uID, int mID) {
        return users.get(uID).hasRating(mID);
    }
}
