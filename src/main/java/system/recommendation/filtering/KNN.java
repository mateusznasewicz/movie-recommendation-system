package system.recommendation.filtering;

import system.recommendation.collaborative_filtering.Similarity;
import system.recommendation.models.Entity;


import java.util.*;

public class KNN<T extends Entity>{
    private final Map<Integer, T> hashmap;
    private final int k;
    private final double[][] neighborMatrix;

    public KNN(Map<Integer, T> hashmap, int k, Similarity<T> simFunction) {
        this.hashmap = hashmap;
        this.k = k;
        this.neighborMatrix = computeNeighbors(simFunction);
    }

    public List<T> getNeighbors(T obj){
        Queue<Integer> queue = findNeighbors(this.k,obj);
        return translateIDtoEntity(queue);
    }

    private Queue<Integer> findNeighbors(int k, T obj){
        int id = obj.getId() - 1;
        int size = hashmap.size();

        Queue<Integer> queue = new PriorityQueue<>(k,(a, b) -> Double.compare(neighborMatrix[id][b], neighborMatrix[id][a]));
        for(int i = 0; i < size; i++){
            if(i == id) continue;
            queue.add(i);
            if (queue.size() > k) {
                queue.poll();
            }
        }

        return queue;
    }

    private List<T> translateIDtoEntity(Queue<Integer> neighborsID){
        List<T> neighbors = new ArrayList<>();
        while(!neighborsID.isEmpty()){
            int id = neighborsID.poll() + 1;
            neighbors.add(this.hashmap.get(id));
        }
        return neighbors;
    }

    private double[][] computeNeighbors(Similarity<T> simFunction) {
        System.out.println("Generating KNN");
        int size = this.hashmap.size();
        double[][] matrix = new double[size][size];

        for(int i = 0; i < size; i++){
            for(int j = i; j < size; j++){
                if(i == j) continue;
                double sim = simFunction.pearsonCorrelation(this.hashmap.get(i+1), this.hashmap.get(j+1));
                matrix[i][j] = sim;
                matrix[j][i] = sim;
            }
        }

        System.out.println("Generating done");
        return matrix;
    }
}
