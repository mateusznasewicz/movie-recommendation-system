package system.recommendation.strategy;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.geneticalgorithm.KnnChromosome;
import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KNNGA<T extends Entity, G extends Entity> extends Strategy<T> {

    private final int populationSize;
    private final KNN<T> knn;
    private final Recommender<T, G> recommender;

    public KNNGA(Map<Integer, T> hashmap, RatingService<T,G> ratingService, Similarity<T> simFunction, int populationSize, int k) {
        super(hashmap, k, simFunction);
        this.populationSize = populationSize;
        this.knn = new KNN<>(hashmap,k,simFunction);
        this.recommender = new CollaborativeFiltering<>(ratingService,knn);
    }

    List<Chromosome> initPopulation(T item){
        List<Chromosome> population = new ArrayList<>();
        List<Integer> neighbors = knn.getNeighbors(item);
        for(int i = 0; i < populationSize; i++){
            population.add(new KnnChromosome<>(item,neighbors,recommender));
        }
        return population;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        List<Chromosome> population = initPopulation(item);
        Chromosome best = GeneticAlgorithm.run(population);
        List<Integer> n = ((KnnChromosome<T,G>) best).getNeighbors();
        return n;
    }
}
