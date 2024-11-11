package system.recommendation.matrixfactorization;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RMFGA{

    private final RatingService<User, Movie> userService;
    private final int k;
    private final double learningRate;
    private final double regularization;
    private final double[][] distMatrix;

    public RMFGA(RatingService<User, Movie> userService, int k, double learningRate, double regularization) {
        this.userService = userService;
        this.k = k;
        this.learningRate = learningRate;
        this.regularization = regularization;

        Similarity<User> sim = new EuclideanDistance<>(userService);
        Strategy<User> s = new KNN<>(userService.getEntityMap(),sim);
        distMatrix = s.getSimMatrix();
    }

    private List<Chromosome> initPopulation(int populationSize){
        List<Chromosome> population = new ArrayList<>();
        double[] total = new double[userService.getEntityMap().size()];
        for(int i = 0; i < total.length; i++){
            for(int j = 0; j < total.length; j++){
                if(distMatrix[i][j] == 0) continue;
                total[i] += 1.0 / distMatrix[i][j];
            }
        }

        for(int i = 0; i < populationSize; i++){
            RMF individual = new RMF(userService, k, learningRate, regularization,0.01, distMatrix, total);
            individual.gd_step();
            population.add(individual);
        }
        return population;
    }

    public RMF run(int populationSize, int epochs){
        List<Chromosome> population = initPopulation(populationSize);
        Chromosome best = GeneticAlgorithm.run(population,epochs,0.3);
        return (RMF) best;
    }
}
