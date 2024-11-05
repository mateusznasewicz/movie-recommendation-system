package system.recommendation.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Entity{
    protected int id;
    protected double avgRating = 0;
    protected final HashMap<Integer, Double> ratings = new HashMap<>();
    protected HashMap<Integer, Double> testRatings = new HashMap<>();

    public int getId(){
        return id;
    }
    public double getAvgRating() { return avgRating; }
    public HashMap<Integer, Double> getRatings() {
        return this.ratings;
    }
    public HashMap<Integer, Double> getTestRatings() { return this.testRatings; }
    public void setTestRatings(HashMap<Integer, Double> ratings) { this.testRatings = ratings; }

    public boolean hasRating(int movieId) {
        return this.ratings.containsKey(movieId);
    }

    public void addRating(int itemID, double rating) {
        int ratingsNumber = this.ratings.size();
        this.avgRating = (this.avgRating*ratingsNumber+rating)/(ratingsNumber+1);
        ratings.put(itemID, rating);
    }

    public void addTestRating(int itemID, double rating){
        testRatings.put(itemID, rating);
    }

    public double getRating(int itemID) {
        return this.ratings.get(itemID);
    }
    public double getTestRating(int itemID) { return this.testRatings.get(itemID); }

    public Set<Integer> getCommon(Entity entity){
        Set<Integer> commonItems = new HashSet<>();

        entity.getRatings().forEach((itemID,_) -> {
            if(this.ratings.containsKey(itemID)){
                commonItems.add(itemID);
            }
        });

        return commonItems;
    }
}
