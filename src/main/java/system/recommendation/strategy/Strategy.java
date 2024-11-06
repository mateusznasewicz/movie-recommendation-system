package system.recommendation.strategy;

import system.recommendation.models.Entity;
import system.recommendation.similarity.Similarity;

import java.util.List;
import java.util.Map;

public abstract class Strategy<T extends Entity> {

    protected final double[][] simMatrix;
    protected final Map<Integer, T> hashmap;
    protected final int k;

    public Strategy(Map<Integer, T> hashmap, int k, Similarity<T> simFunction){
        this.simMatrix = computeNeighbors(simFunction);
        this.hashmap = hashmap;
        this.k = k;
    }


    abstract public List<T> getNeighbors(T item);

    public double[][] getSimMatrix() {
        return simMatrix;
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
