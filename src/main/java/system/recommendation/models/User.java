package system.recommendation.models;

import java.util.HashMap;

public class User {
    private double avgRating = 0;
    private final HashMap<Integer, Double> ratings = new HashMap<>();

    public void addRating(int movieId, double rating) {
        int ratingsNumber = this.ratings.size();
        this.avgRating = (this.avgRating*ratingsNumber+rating)/(ratingsNumber+1);
        ratings.put(movieId, rating);
    }

    public double getRating(int movieId) {
        return this.ratings.get(movieId);
    }

    public double getAvgRating() {
        return this.avgRating;
    }

    public HashMap<Integer, Double> getRatings() {
        return this.ratings;
    }
}
