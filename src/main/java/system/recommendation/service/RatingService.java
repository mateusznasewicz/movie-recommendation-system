package system.recommendation.service;


import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Map;
import java.util.Set;

public abstract class RatingService<T extends  Entity,G extends  Entity> {
    protected Map<Integer, T> entityMap;
    protected Map<Integer, G> itemMap;

    public RatingService(Map<Integer, T> entityMap, Map<Integer, G> itemMap){
        this.entityMap = entityMap;
        this.itemMap = itemMap;
    }

    public double getRating(int id1, int id2){
        return entityMap.get(id1).getRating(id2);
    }
    public double getAvg(int id){
        return itemMap.get(id).getAvgRating();
    }
    public boolean isRatedById(int id1, int id2){
        return entityMap.get(id1).hasRating(id2);
    }
    public T getEntity(int id){
        return entityMap.get(id);
    }
    public Set<Integer> getEntitiesID(){
        return entityMap.keySet();
    }
    public Map<Integer,T> getEntityMap(){
        return this.entityMap;
    }
    public Map<Integer,G> getItemMap(){
        return this.itemMap;
    }
    public void addEntity(T entity){
        this.entityMap.put(entity.getId(), entity);
    }
}
