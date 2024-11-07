package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public class UserService extends RatingService<User,Movie> {
    public UserService(Map<Integer,User> users, Map<Integer, Movie> movies) {super(users,movies);}
}
