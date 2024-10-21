package system.recommendation.service;

import system.recommendation.models.User;

import java.util.Map;

public class UserService implements RatingService<User> {
    private final Map<Integer, User> users;
    public UserService(Map<Integer,User> users) {
        this.users = users;
    }

    @Override
    public double getRating(int uID, int mID) {
        return users.get(uID).getRating(mID);
    }

    @Override
    public boolean isRatedById(int uID, int mID) {
        return users.get(uID).hasRating(mID);
    }
}
