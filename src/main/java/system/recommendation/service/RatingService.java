package system.recommendation.service;


import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public abstract class RatingService<T,G> {
    protected Map<Integer, User> users;
    protected Map<Integer, Movie> movies;

    public RatingService(Map<Integer, User> users, Map<Integer, Movie> movies){
        this.users = users;
        this.movies = movies;
    }

    public abstract double getRating(int id1, int id2);
    public abstract double getAvg(int id);
    public abstract boolean isRatedById(int id1, int id2);
    public abstract T getEntity(int id);
    public abstract Set<Integer> getEntitiesID();

    public abstract Map<Integer,T> getEntityMap();
    public abstract Map<Integer,G> getItemMap();
}
