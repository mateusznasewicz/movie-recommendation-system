package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.ArrayList;
import java.util.List;

public class NMFGA {
    private final RatingService<User, Movie> userService;
    private final int k;
    private final double learningRate;

    public NMFGA(RatingService<User, Movie> userService, int k, double learningRate) {
        this.userService = userService;
        this.k = k;
        this.learningRate = learningRate;
    }

    private List<Chromosome> initPopulation(int populationSize){
        List<Chromosome> population = new ArrayList<>();

        for(int i = 0; i < populationSize; i++){
            NMF individual = new NMF(userService, k, learningRate,0.01);
            individual.gd_step();
            population.add(individual);
        }
        return population;
    }

//    public NMF run(int populationSize, int epochs, double mutationRate){
//        List<Chromosome> population = initPopulation(populationSize);
//        Chromosome best = GeneticAlgorithm.run(population,epochs,mutationRate);
//        return (NMF) best;
//    }
}
