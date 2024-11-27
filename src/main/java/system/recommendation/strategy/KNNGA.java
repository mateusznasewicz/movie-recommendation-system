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
    private final double mutationRate;
    private final RatingService<T,G> ratingService;

    public KNNGA(RatingService<T,G> ratingService, Similarity<T> simFunction, int populationSize, int k, int epochs, double mutationRate) {
        super(ratingService.getEntityMap(), k, simFunction);
        this.populationSize = populationSize;
        this.epochs = epochs;
        this.ratingService = ratingService;
        this.mutationRate = mutationRate;
    }

    List<Chromosome> initPopulation(T item){
        List<Chromosome> population = new ArrayList<>();

        for(int i = 0; i < populationSize; i++){
            population.add(new KnnChromosome<>(item,ratingService,k));
        }
        return population;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        List<Chromosome> population = initPopulation(item);
        Chromosome best = GeneticAlgorithm.run(population,epochs,mutationRate);

        KnnChromosome<T,G> b = (KnnChromosome<T,G>) best;
        int bestID = b.getID();
        double[] weights = b.getWeights();
        List<Integer> neighbors = b.neighborsLocal();

        for(int n: neighbors){
            simMatrix[bestID-1][n-1] = weights[n-1];
        }

        return neighbors;
    }
}
