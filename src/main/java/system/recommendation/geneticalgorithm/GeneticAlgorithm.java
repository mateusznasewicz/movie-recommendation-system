package system.recommendation.geneticalgorithm;

import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.RMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.*;

public class GeneticAlgorithm {
    private static final SplittableRandom random = new SplittableRandom();

    public static Chromosome run(List<Chromosome> population, int epochs, double mutationRate, double[][] mae, double[][] rmse, RatingService<User, Movie> userService, int id){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;

        for(int e = 0; e < epochs; e++)
        {
            double[] fitness = new double[population.size()];
            double totalFitness = 0;

            int bestID = -1;
            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += 1.0 / fitness[i];

                if(fitness[i] < bestFit){
                    bestFit = fitness[i];
                    bestID = i;
                }
            }

            if(bestID != -1){
                best = population.get(bestID).copy();
            }

            RMF b = (RMF) best;
            double[][] ratings = b.getPredictedRatings();
            double[] result = new double[]{QualityMeasure.MAE(ratings,userService,false),QualityMeasure.RMSE(ratings,userService)};
            mae[id][e] = result[0];
            rmse[id][e] = result[1];

            List<Chromosome> newPopulation = new ArrayList<>();
            for(int i = 0; i < population.size()/2; i++){
                ChromosomePairID pp1 = rouletteWheel(population,fitness,totalFitness);
                ChromosomePairID pp2;
                do{
                     pp2 = rouletteWheel(population,fitness,totalFitness);
                }while(pp1.getId() == pp2.getId());

                Chromosome p1 = pp1.getChromosome();
                Chromosome p2 = pp2.getChromosome();
                List<Chromosome> children = p1.crossover(p2,0.4);
                newPopulation.addAll(children);
            }

            for(Chromosome c : newPopulation){
                c.memetic(mutationRate);
            }

            population = newPopulation;
        }

        for(int i = 0; i < population.size(); i++){
            double fit = population.get(i).fitness();

            if(fit < bestFit){
                bestFit = fit;
                best = population.get(i).copy();
            }
        }

        return best;
    }

    public static Chromosome run(List<Chromosome> population, int epochs, double mutationRate){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;

        for(int e = 0; e < epochs; e++)
        {
            double[] fitness = new double[population.size()];
            double totalFitness = 0;

            int bestID = -1;
            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += 1.0 / fitness[i];

                if(fitness[i] < bestFit){
                    bestFit = fitness[i];
                    bestID = i;
                }
            }

            if(bestID != -1){
                best = population.get(bestID).copy();
            }

            List<Chromosome> newPopulation = new ArrayList<>();
            for(int i = 0; i < population.size()/2; i++){
                ChromosomePairID pp1 = rouletteWheel(population,fitness,totalFitness);
                ChromosomePairID pp2;
                do{
                    pp2 = rouletteWheel(population,fitness,totalFitness);
                }while(pp1.getId() == pp2.getId());

                Chromosome p1 = pp1.getChromosome();
                Chromosome p2 = pp2.getChromosome();
                List<Chromosome> children = p1.crossover(p2,0.4);
                newPopulation.addAll(children);
            }

            for(Chromosome c : newPopulation){
                c.memetic(mutationRate);
            }

            population = newPopulation;
            System.out.println("EPOCH:"+e);
        }

        for(int i = 0; i < population.size(); i++){
            double fit = population.get(i).fitness();

            if(fit < bestFit){
                bestFit = fit;
                best = population.get(i).copy();
            }
        }

        return best;
    }

    private static ChromosomePairID rouletteWheel(List<Chromosome> population, double[] fitness, double totalFitness){

        double r = random.nextDouble(totalFitness);
        double cumulativeFitness = 0;

        for(int i = 0; i < fitness.length; i++){
            cumulativeFitness += 1.0 / fitness[i];
            if(cumulativeFitness > r){
                return new ChromosomePairID(i,population.get(i));
            }
        }

        return new ChromosomePairID(-1,population.get(-1));
    }
}
