package system.recommendation;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;


import java.util.*;

public class KNN<T extends Entity, G extends  Entity>{
    private final Map<Integer, T> hashmap;
    private final int k;
    private final double[][] simMatrix;
    private final RatingService<T> ratingService;

    public KNN(Map<Integer, T> hashmap, int k, Similarity<T> simFunction, RatingService<T> ratingService) {
        this.hashmap = hashmap;
        this.k = k;
        this.ratingService = ratingService;
        this.simMatrix = computeNeighbors(simFunction);
    }

    public double[][] getSimMatrix() {
        return simMatrix;
    }

    public List<T> getNeighbors(T obj, G item){
        Queue<Integer> queue = findNeighbors(this.k,obj,item);
        return translateIDtoEntity(queue);
    }

    private Queue<Integer> findNeighbors(int k, T entity, G item){
        int eID = entity.getId();
        int iID = item.getId();

        Queue<Integer> queue = new PriorityQueue<>(k,(b, a) -> Double.compare(simMatrix[eID-1][b-1], simMatrix[eID-1][a-1]));
        Set<Integer> potentialNeighbors = ratingService.getEntities(iID);
        potentialNeighbors.forEach(nID ->{
            if(nID != eID && simMatrix[eID-1][nID-1] > 0){
                queue.add(nID);
                if (queue.size() > k) {
                    queue.poll();
                }
            }
        });

        return queue;
    }

    private List<T> translateIDtoEntity(Queue<Integer> neighborsID){
        List<T> neighbors = new ArrayList<>();
        while(!neighborsID.isEmpty()){
            int id = neighborsID.poll();
            neighbors.add(this.hashmap.get(id));
        }
        return neighbors;
    }

    private double[][] computeNeighbors(Similarity<T> simFunction) {
        System.out.println("Generating similarity matrix");
        int size = this.hashmap.size();
        double[][] matrix = new double[size][size];

        for(int i = 0; i < size; i++){
            for(int j = i; j < size; j++){
                if(i == j) continue;
                double sim = simFunction.calculate(this.hashmap.get(i+1), this.hashmap.get(j+1));
                matrix[i][j] = sim;
                matrix[j][i] = sim;
            }
        }

        System.out.println("Generating done");
        return matrix;
    }
}
