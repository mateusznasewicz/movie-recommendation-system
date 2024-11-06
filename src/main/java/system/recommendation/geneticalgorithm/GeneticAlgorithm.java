package system.recommendation.geneticalgorithm;

import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    public static Chromosome run(List<Chromosome> population, int epochs){
        Chromosome best = null;

        for(int e = 0; e < epochs; e++){
            double[] fitness = new double[population.size()];
            double totalFitness = 0;
            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += fitness[i];
            }
        }

        return best;
    }

    private Chromosome rouletteWheel(List<Chromosome> population, double[] fitness, double totalFitness){
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
