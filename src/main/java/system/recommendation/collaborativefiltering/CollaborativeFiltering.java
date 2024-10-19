package system.recommendation.collaborativefiltering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.similarity.Similarity;

import java.util.*;

public abstract class CollaborativeFiltering<T extends Entity>{
    protected DatasetLoader datasetLoader;
    protected Map<Integer, T> hashmap;
    protected Similarity<T> similarity;
    protected boolean RATE_ALL;

    abstract void fillNeighbor(T entity);
    abstract double predictRating(User user, Movie movie, List<T> neighbors);

    public void fillRatings(){
        this.hashmap.forEach(((_,entity)->fillNeighbor(entity)));
    }

}
