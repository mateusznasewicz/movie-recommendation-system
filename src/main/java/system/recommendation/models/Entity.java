package system.recommendation.models;

import java.util.Set;

public abstract class Entity<T>{
    protected int id;
    protected double avgRating = 0;

    public int getId(){
        return id;
    }
    public double getAvgRating() { return avgRating; }
    public abstract Set<Integer> getCommon(T entity);
}
