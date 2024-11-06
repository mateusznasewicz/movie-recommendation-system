package system.recommendation.strategy;

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
    private final RatingService<T> ratingService;
    private final Recommender<T, G> recommender;

    public KNNGA(Map<Integer, T> hashmap, RatingService<T> ratingService, Similarity<T> simFunction, int populationSize, int k) {
        super(hashmap, k, simFunction);
        this.populationSize = populationSize;
        this.ratingService = ratingService;
        this.knn = new KNN<>(hashmap,k,simFunction);
        this.recommender = new CollaborativeFiltering<>(ratingService,strategy);
    }

    List<KnnChromosome<T>> initPopulation(T item){
        List<KnnChromosome<T>> population = new ArrayList<>();
        List<Integer> neighbors = knn.getNeighbors(item);
        double[][] simMatrix = knn.getSimMatrix();
        for(int i = 0; i < populationSize; i++){
            population.add(new KnnChromosome<>(simMatrix,item,neighbors,ratingService));
        }
        return population;
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        List<KnnChromosome<T>> population = initPopulation(item);
        GeneticAlgorithm.run(population);
    }
}
