package system.recommendation.geneticalgorithm;

import java.util.*;

public class GeneticAlgorithm {
    private final int populationSize;
    private final int iterations;
    private final double mutationRate;
    private final double crossoverRate;
    private final ChromosomeProvider chromosomeProvider;

    private Chromosome bestChromosome = null;
    private double bestFit = Double.MAX_VALUE;

    public GeneticAlgorithm(int populationSize, double mutationRate, double crossoverRate,
                            int iterations, ChromosomeProvider chromosomeProvider) {
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.iterations = iterations;
        this.chromosomeProvider = chromosomeProvider;
    }

    public Chromosome run(){
        Random random = new Random();
        System.out.println("population init in process");
        Map<Integer,Chromosome> population = initPopulation();
        System.out.println("population init done");

        for(int i = 0; i < this.iterations; i++)
        {
            Map<Integer,Chromosome> newGeneration = new HashMap<>();
            while(newGeneration.size() < this.populationSize){
                //selection
                List<Chromosome> parents = tournamentSelection(this.populationSize/10, population);
                Chromosome p1 = parents.getFirst();
                Chromosome p2 = parents.getLast();

                //crossover
                if(random.nextDouble() < this.crossoverRate){
                    List<Chromosome> children = p1.crossover(p2);
                    p1 = children.getFirst();
                    p2 = children.getLast();
                    updateBestChromosomeParents(p1,p2,p1.fitness(),p2.fitness());
                }

                //mutation
                if(random.nextDouble() < this.mutationRate){
                    p1 = p1.mutate();
                    p2 = p2.mutate();
                    updateBestChromosomeParents(p1,p2,p1.fitness(),p2.fitness());
                }

                //new generation
                newGeneration.put(p1.getId(),p1);
                newGeneration.put(p2.getId(),p2);
            }
            population = newGeneration;
        }
        return bestChromosome;
    }

    private void updateBestChromosomeParents(Chromosome p1, Chromosome p2, double f1, double f2){
        if(f1 < f2){
            updateBestChromosome(p1,f1);
        }else{
            updateBestChromosome(p2,f2);
        }
    }

    private void updateBestChromosome(Chromosome potential, double f){
        if(f < bestFit){
            bestChromosome = potential;
            bestFit = f;
        }
    }

    private Map<Integer,Chromosome> initPopulation(){
        Map<Integer,Chromosome> population = new HashMap<>();
        for(int i = 0; i < this.populationSize; i++){
            Chromosome chromosome = chromosomeProvider.getChromosome();
            double f = chromosome.fitness();
            population.put(chromosome.getId(),chromosome);
            updateBestChromosome(chromosome,f);
        }
        return population;
    }

    private List<Chromosome> tournamentSelection(int k,Map<Integer,Chromosome> population){
        Random random = new Random();
        List<Chromosome> popList = new ArrayList<>(population.values());
        Queue<Chromosome> parents = new PriorityQueue<>((a,b) -> Double.compare(b.getFitness(),a.getFitness()));

        for(int i = 0; i < k; i ++){
            int id = random.nextInt(popList.size());
            Chromosome potential = popList.get(id);
            parents.add(potential);
            if(parents.size() >  2){
                parents.poll();
            }
        }

        return new ArrayList<>(parents);
    }
}
