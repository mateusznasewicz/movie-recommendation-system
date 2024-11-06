package system.recommendation.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    public static Chromosome run(List<Chromosome> population, int epochs){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;

        for(int e = 0; e < epochs; e++)
        {
            List<Chromosome> newPopulation = new ArrayList<>();

            double[] fitness = new double[population.size()];
            double totalFitness = 0;
            double bestLocal = Double.MAX_VALUE;

            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += fitness[i];

                if(fitness[i] < bestFit){
                    best = population.get(i);
                    bestFit = fitness[i];
                    System.out.println("best " + e);
                }

            }

//            for(double i: fitness){
//                System.out.println(i);
//            }


            for(int i = 0; i < population.size()/2; i++){
                Chromosome p1 = rouletteWheel(population,fitness,totalFitness);
                Chromosome p2 = rouletteWheel(population,fitness,totalFitness);
                List<Chromosome> children = p1.crossover(p2,0.4);
                newPopulation.addAll(children);
            }

            for(Chromosome c: newPopulation){
                c.mutate(0.015);
            }

            population = newPopulation;
        }

        return best;
    }

    private static Chromosome rouletteWheel(List<Chromosome> population, double[] fitness, double totalFitness){
        Random random = new Random();
        double r = random.nextDouble() * totalFitness;
        double cumulativeFitness = 0;

        for(int i = 0; i < fitness.length; i++){
            cumulativeFitness += fitness[i];
            if(cumulativeFitness > r){
                return population.get(i);
            }
        }

        return population.getLast();
    }
}
