package system.recommendation.geneticalgorithm;

import java.util.*;

public class GeneticAlgorithm {
    private static final SplittableRandom random = new SplittableRandom();

    public static Chromosome run(List<Chromosome> population, int epochs, double mutationRate){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;
        int elitismSize = (population.size()/10)/2;

        for(int e = 0; e < epochs; e++)
        {
            double[] fitness = new double[population.size()];
            double totalFitness = 0;

            Queue<Integer> elitism = new PriorityQueue<>(elitismSize, (a,b)->Double.compare(fitness[b], fitness[a]));

            int bestID = -1;
            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
                totalFitness += 1.0 / fitness[i];

                if(fitness[i] < bestFit){
                    bestFit = fitness[i];
                    bestID = i;
                }

                elitism.add(i);
                if(elitism.size() > elitismSize){
                    elitism.poll();
                }
            }

            if(bestID != -1){
                best = population.get(bestID).copy();
            }

            List<Chromosome> newPopulation = new ArrayList<>();
            for(int i = 0; i < (population.size()-elitismSize)/2; i++){
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

            for(Chromosome c: newPopulation){
                c.memetic(mutationRate);
            }

            while(!elitism.isEmpty()){
                newPopulation.add(population.get(elitism.poll()));
            }

            population = newPopulation;
            System.out.println("EPOCH " + e + "|" + bestFit);
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
