package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.similarity.Similarity;


import java.util.*;

public class KNN<T extends Entity> extends Strategy<T>{


    public KNN(Map<Integer, T> hashmap, int k, Similarity<T> simFunction) {
        super(hashmap, k, simFunction);
    }

    @Override
    public List<Integer> getNeighbors(T obj){
        Queue<Integer> queue = findNeighbors(this.k,obj);
        return queue.stream().toList();
    }

    private Queue<Integer> findNeighbors(int k, T entity){
        int eID = entity.getId();

        Queue<Integer> queue = new PriorityQueue<>(k,(b, a) -> Double.compare(simMatrix[eID-1][b-1], simMatrix[eID-1][a-1]));
        for(int nID = 1; nID < hashmap.size()+1; nID++){
            if(nID != eID && simMatrix[eID-1][nID-1] > 0){
                queue.add(nID);
                if (queue.size() > k) {
                    queue.poll();
                }
            }
        }

        return queue;
    }
}
