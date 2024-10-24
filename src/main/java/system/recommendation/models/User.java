package system.recommendation.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class User extends Entity{
    private final int id;
    private final HashMap<Integer, Double> ratings = new HashMap<>();

    public User(int id) {
        this.id = id;
    }

    public void addRating(int movieId, double rating) {
        int ratingsNumber = this.ratings.size();
        this.avgRating = (this.avgRating*ratingsNumber+rating)/(ratingsNumber+1);
        ratings.put(movieId, rating);
    }

    public int getId() {
        return id;
    }

    @Override
    public Set<Integer> getCommon(Entity entity) {
        Set<Integer> commonMovies = new HashSet<>();

        ((User)entity).getRatings().forEach((movieID,_) -> {
            if(this.ratings.containsKey(movieID)){
                commonMovies.add(movieID);
            }
        });

        return commonMovies;
    }

    public HashMap<Integer, Double> getRatings() {
        return this.ratings;
    }

    public double getRating(int movieId) {
        return this.ratings.get(movieId);
    }

    public boolean hasRating(int movieId) {
        return this.ratings.containsKey(movieId);
    }
}
