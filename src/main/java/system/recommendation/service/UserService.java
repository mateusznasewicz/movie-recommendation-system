package system.recommendation.service;

import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UserService extends RatingService<User> {

    public UserService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        super(users,movies);
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

    @Override
    public Set<Integer> getEntities(int itemID) {
        return null;
    }

    @Override
    public User getEntity(int id) {
        return users.get(id);
    }

    @Override
    public double getTestRating(int id1, int id2) { return users.get(id1).getTestRating(id2); }

    @Override
    public Set<Integer> getEntities() { return users.keySet();}
}
