package system.recommendation.geneticalgorithm;

import java.util.*;

public class GeneticAlgorithm<T extends Chromosome> {
    private final int populationSize;
    private final int iterations;
    private final double mutationRate;
    private final double crossoverRate;
    private final ChromosomeProvider<T> chromosomeProvider;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate,
                            int iterations, ChromosomeProvider<T> chromosomeProvider) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.iterations = iterations;
        this.chromosomeProvider = chromosomeProvider;
    }

    public T run(){
        Random random = new Random();
        Queue<T> population = initPopulation();

        for(int i = 0; i < this.iterations; i++)
        {
            //crossover
            for(int j = 0; j < this.populationSize; j++){
                if(random.nextDouble() >= this.crossoverRate) continue;
                T p1 = tournamentSelection(this.populationSize/100);
                T p2 = tournamentSelection(this.populationSize/100);
                List<T> children = p1.crossover(p2);
                T c1 = children.getFirst();
                T c2 = children.getLast();
                population.add(c1);
                population.add(c2);
            }

            //mutation
            population.forEach(chromosome -> {
                if(random.nextDouble() < this.mutationRate){
                    population.remove(chromosome);
                    chromosome.mutate();
                    population.add(chromosome);
                }
            });

            //selecting new population
            while(population.size() > populationSize){
                T toRemove = population.poll();
            }
        }

        T best = population.peek();
        while(!population.isEmpty()){
            best = population.poll();
        }
        return best;
    }

    private Queue<T> initPopulation(){
        Queue<T> population = new PriorityQueue<>(Comparator.comparingDouble(Chromosome::getFitness));
        for(int i = 0; i < this.populationSize; i++){
            T chromosome = chromosomeProvider.getChromosome();
            population.add(chromosome);
        }
        return population;
    }

    private T tournamentSelection(int k){
        return null;
    }
}
