package system.recommendation.strategy;

import system.recommendation.geneticalgorithm.Chromosome;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.geneticalgorithm.SimChromosome;
import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class SimGa <T extends Entity, G extends Entity>{
    private final int epochs;
    private final int populationSize;
    private final RatingService<T,G> ratingService;
    private final SplittableRandom rand = new SplittableRandom();
    private final int k;

    public SimGa(RatingService<T,G> ratingService, int populationSize, int k, int epochs) {
        this.populationSize = populationSize;
        this.epochs = epochs;
        this.ratingService = ratingService;
        this.k = k;
    }

    private double[][] randomSimMatrix(){
        double[][] simMatrix = new double[ratingService.getEntityMap().size()][ratingService.getEntityMap().size()];
        for(int i = 0; i < simMatrix.length; i++){
            for(int j = i; j < simMatrix[i].length; j++){
                if(i == j){
                    simMatrix[i][j] = 0;
                    continue;
                }
                double sim = rand.nextDouble();
                simMatrix[i][j] = sim;
                simMatrix[j][i] = sim;
            }
        }
        return simMatrix;
    }

    List<Chromosome> initPopulation(){
        List<Chromosome> population = new ArrayList<>();
        for(int i = 0; i < populationSize; i++){
            population.add(new SimChromosome<>(ratingService,randomSimMatrix(),k));
        }
        return population;
    }

    public SimChromosome<T,G> run() {
        List<Chromosome> population = initPopulation();
        Chromosome best = GeneticAlgorithm.run(population,epochs,0.2);
        return (SimChromosome<T, G>) best;
    }
}
