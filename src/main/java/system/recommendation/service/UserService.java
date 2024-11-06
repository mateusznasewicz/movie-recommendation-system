package system.recommendation.service;

import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class UserService extends RatingService<User,Movie> {

    public UserService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        super(users,movies);
    }

    @Override
    public double getRating(int eID, int iID) { return users.get(eID).getRating(iID); }

    @Override
    public double getAvg(int eID) {
        return movies.get(eID).getAvgRating();
    }

    @Override
    public boolean isRatedById(int eID, int iID) {
        return users.get(eID).hasRating(iID);
    }

    @Override
    public User getEntity(int eID) {
        return users.get(eID);
    }

    @Override
    public Set<Integer> getEntitiesID() { return users.keySet(); }

    @Override
    public Map<Integer, User> getEntityMap() { return users; }

    @Override
    public Map<Integer, Movie> getItemMap() { return movies; }
}
