package system.recommendation.strategy;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.geneticalgorithm.KnnChromosome;
import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.ArrayList;
import java.util.List;

public class KNNGA<T extends Entity, G extends Entity> extends KNN<T> {

    private final int epochs;
    private final int populationSize;
    private final RatingService<T,G> ratingService;

    public KNNGA(RatingService<T,G> ratingService, Similarity<T> simFunction, int populationSize, int k, int epochs) {
        super(ratingService.getEntityMap(), k, simFunction);
        this.populationSize = populationSize;
        this.epochs = epochs;
        this.ratingService = ratingService;
    }

    List<Chromosome> initPopulation(T item){
        List<Integer> neighbors = super.getNeighbors(item);
        if(neighbors.isEmpty()) return null;
        List<Chromosome> population = new ArrayList<>();

        for(int i = 0; i < populationSize; i++){
            population.add(new KnnChromosome<>(item,ratingService,neighbors,simMatrix));
        }
        return population;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        List<Chromosome> population = initPopulation(item);
        if(population == null) return List.of();

        Chromosome best = GeneticAlgorithm.run(population,epochs,0.02);
        return ((KnnChromosome<T,G>) best).getNeighbors();
    }
}
