package system.recommendation.service;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public class MovieService extends RatingService<Movie,User> {
    public MovieService(Map<Integer,User> users, Map<Integer, Movie> movies) {
        super(users,movies);
    }

    @Override
    public double getRating(int eID, int iID) {return movies.get(eID).getRating(iID);}

    @Override
    public double getAvg(int eID) {return movies.get(eID).getAvgRating();}

    @Override
    public boolean isRatedById(int eID, int iID) {
        return movies.get(eID).hasRating(iID);
    }

    @Override
    public Movie getEntity(int eID) {
        return this.movies.get(eID);
    }

    @Override
    public Set<Integer> getEntitiesID() {return movies.keySet();}

    @Override
    public Map<Integer, Movie> getEntityMap() {return movies;}

    @Override
    public Map<Integer, User> getItemMap() {return users;}

}
