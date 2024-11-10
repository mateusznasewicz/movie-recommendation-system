package system.recommendation.geneticalgorithm;

import java.util.*;

public class GeneticAlgorithm {
    private static final SplittableRandom random = new SplittableRandom();

    public static Chromosome run(List<Chromosome> population, int epochs, double mutationRate){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;

        for(int e = 0; e < epochs; e++)
        {
            System.out.println("Epoch " + e);
            double[] fitness = new double[population.size()];
            double totalFitness = 0;

            int elitismSize = (population.size()/5) / 2;
            Queue<Integer> elitism = new PriorityQueue<>(elitismSize, (a,b)->Double.compare(fitness[b], fitness[a]));

            List<Chromosome> newPopulation = new ArrayList<>();

            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += fitness[i];

                if(fitness[i] < bestFit){
                    best = population.get(i);
                    bestFit = fitness[i];
                }

                elitism.add(i);
                if(elitism.size() > elitismSize){
                    elitism.poll();
                }
            }

            System.out.println(bestFit);

            for(int i = 0; i < (population.size()-elitismSize)/2; i++){
                Chromosome p1 = rouletteWheel(population,fitness,totalFitness);
                Chromosome p2 = rouletteWheel(population,fitness,totalFitness);
                List<Chromosome> children = p1.crossover(p2,0.4);
                newPopulation.addAll(children);
            }

            for(Chromosome c: newPopulation){
                c.mutate(mutationRate);
            }

            while(!elitism.isEmpty()){
                newPopulation.add(population.get(elitism.poll()));
            }

            population = newPopulation;
        }

        return best;
    }

    private static Chromosome rouletteWheel(List<Chromosome> population, double[] fitness, double totalFitness){

        double r = random.nextDouble(totalFitness);
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
