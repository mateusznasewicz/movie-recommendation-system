package system.recommendation.service;


import java.util.Set;

public interface RatingService<T> {
    double getRating(int id1, int id2);
    double getAvg(int id);
    boolean isRatedById(int id1, int id2);
    Set<Integer> getEntities(int itemID);
}
