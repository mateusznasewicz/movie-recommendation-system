package system.recommendation.geneticalgorithm;

import system.recommendation.models.Entity;
import system.recommendation.recommender.Recommender;

import java.util.*;

public class KnnChromosome<T extends Entity, G extends Entity> implements Chromosome{
    private final int[] neighbors;
    private final double[] weights;

    private final T item;
    private final Recommender<T,G> recommender;

    public KnnChromosome(T item, List<Integer> n, Recommender<T,G> recommender) {
        this.item = item;
        this.recommender = recommender;
        double[][] simMatrix = recommender.getSimMatrix();
        neighbors = new int[n.size()];
        weights = new double[n.size()];

        for(int i = 0; i < n.size(); i++) {
            neighbors[i] = n.get(i);
            weights[i] = simMatrix[item.getId()-1][n.get(i)-1];
        }
    }

    public KnnChromosome(T item, Recommender<T,G> recommender, int[] neighbors, double[] weights) {
        this.item = item;
        this.neighbors = neighbors;
        this.weights = weights;
        this.recommender = recommender;
    }

    public List<Integer> getNeighbors(){
        return Arrays.stream(neighbors).boxed().toList();
    }

    @Override
    public void mutate(double chance){
        Random random = new Random();
        for(int i = 0; i < weights.length; i++) {
            if(random.nextDouble() < chance) {
                weights[i] = random.nextDouble();
            }
        }
    }

    @Override
    public double fitness(){
        double error = 0;
        int n = 0;

        int eID = this.item.getId();
        Map<Integer,Double> ratings = item.getRatings();
        List<Integer> neighbors = getNeighbors();

        for(Map.Entry<Integer,Double> entry : ratings.entrySet()) {
            int iID = entry.getKey();
            double rating = entry.getValue();
            double predicted = recommender.predict(eID,iID,neighbors);

            if(predicted == -1) continue;

            error += Math.abs(predicted - rating);
            n++;
        }

        return error/n;
    }

    @Override
    public List<Chromosome> crossover(Chromosome p, double weight) {
        double[] w1 = new double[weights.length];
        double[] w2 = new double[weights.length];
        double[] p1 = this.weights;
        double[] p2 = ((KnnChromosome<T,G>) p).weights;

        for(int i = 0; i < weights.length; i++) {
            w1[i] = p1[i] * weight + (1 - weight)*p2[i];
            w2[i] = p2[i] * weight + (1 - weight)*p1[i];
        }

        var c1 = new KnnChromosome<>(item, recommender, neighbors, w1);
        var c2 = new KnnChromosome<>(item, recommender, neighbors, w2);
        return List.of(c1,c2);
    }
}
