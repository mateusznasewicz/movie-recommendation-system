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
        this.hashmap = hashmap;
        this.k = k;
        this.simMatrix = computeNeighbors(simFunction);
    }

    public Strategy(Map<Integer, T> hashmap, Similarity<T> simFunction){
        this.hashmap = hashmap;
        this.simMatrix = computeNeighbors(simFunction);
        this.k = 0;
    }


    abstract public List<Integer> getNeighbors(T item);

    public double[][] getSimMatrix() {
        return simMatrix;
    }

    private double[][] computeNeighbors(Similarity<T> simFunction) {
        System.out.println("Generating similarity matrix");
        int size = this.hashmap.size();
        double[][] matrix = new double[size][size];

        for(int i = 0; i < size; i++){
            for(int j = i; j < size; j++){
                T a = this.hashmap.get(i+1);
                T b = this.hashmap.get(j+1);
                double sim;
                if(i==j || a.getCommon(b).size() < 7){
                    sim = 0;
                }else{
                    sim = simFunction.calculate(a, b);
                }
                matrix[i][j] = sim;
                matrix[j][i] = sim;
            }
        }

        System.out.println("Generating done");
        return matrix;
    }
}
