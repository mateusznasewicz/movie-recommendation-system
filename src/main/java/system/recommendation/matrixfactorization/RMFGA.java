package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.geneticalgorithm.KnnChromosome;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.ArrayList;
import java.util.List;

public class RMFGA{


    private final RatingService<User, Movie> userService;
    private final int k;
    private final double learningRate;
    private final double regularization;

    public RMFGA(RatingService<User, Movie> userService, int k, double learningRate, double regularization) {
        this.userService = userService;
        this.k = k;
        this.learningRate = learningRate;
        this.regularization = regularization;
    }

    private List<Chromosome> initPopulation(int populationSize){
        List<Chromosome> population = new ArrayList<>();

        for(int i = 0; i < populationSize; i++){
            population.add(new RMF(userService, k, learningRate, regularization,0.1));
        }
        return population;
    }

    public RMF run(int populationSize, int epochs){
        List<Chromosome> population = initPopulation(populationSize);
        Chromosome best = GeneticAlgorithm.run(population,epochs,0.3);
        return (RMF) best;
    }
}
