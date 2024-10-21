package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
}
