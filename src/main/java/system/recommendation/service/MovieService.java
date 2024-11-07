package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public class MovieService extends RatingService<Movie,User> {
    public MovieService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        super(movies,users);
    }
}
