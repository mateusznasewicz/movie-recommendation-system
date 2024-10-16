package system.recommendation.collaborative_filtering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.*;

public abstract class CollaborativeFiltering<T extends Entity> implements Similarity<T> {
    protected DatasetLoader datasetLoader;
    protected Map<Integer, T> hashmap;
    protected boolean RATE_ALL;

    abstract void fillNeighbor(T entity);
    abstract double predictRating(User user, Movie movie, List<T> neighbors);

    public void fillRatings(){
        this.hashmap.forEach(((_,entity)->fillNeighbor(entity)));
    }

}
