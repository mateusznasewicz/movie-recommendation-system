package system.recommendation.geneticalgorithm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnnChromosome<T extends Entity> implements Chromosome {
    Map<Integer, Double> neighborsWeightMap = new HashMap<>();
    private T item;

    public KnnChromosome(double[][] simMatrix, T item, List<Integer> neighbors, RatingService<T> ratingService) {
        this.item = item;
        for(Integer nID : neighbors) {
            neighborsWeightMap.put(nID, simMatrix[item.getId()-1][nID-1]);
        }
    }

    @Override
    public void mutate() {

    }

    @Override
    public double fitness() {

    }

    @Override
    public List<Chromosome> crossover(Chromosome p2) {
        return List.of();
    }
}
