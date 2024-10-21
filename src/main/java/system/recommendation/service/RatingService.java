package system.recommendation.service;


public interface RatingService<T> {
    double getRating(int id1, int id2);
    double getAvg(int id);
    boolean isRatedById(int id1, int id2);
}
