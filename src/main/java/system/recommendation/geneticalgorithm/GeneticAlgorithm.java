package system.recommendation.geneticalgorithm;

import java.util.*;

public class GeneticAlgorithm {
    private static final SplittableRandom random = new SplittableRandom();

    public static Chromosome run(List<Chromosome> population, int epochs, double mutationRate){
        Chromosome best = null;
        double bestFit = Double.MAX_VALUE;

        for(int e = 0; e < epochs; e++)
        {
            System.out.println("===");
            double[] fitness = new double[population.size()];
            double totalFitness = 0;

            int bestID = -1;
            for(int i = 0; i < fitness.length; i++){
                fitness[i] = population.get(i).fitness();
//                System.out.println(fitness[i]);
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
            Set<ParentPair> crossovers = new HashSet<>();
            for(int i = 0; i < population.size()/2; i++){
                ChromosomePairID pp1 = rouletteWheel(population,fitness,totalFitness);
                ChromosomePairID pp2;
                ParentPair pairID;
                do{
                     pp2 = rouletteWheel(population,fitness,totalFitness);
                     pairID = new ParentPair(pp1.getId(),pp2.getId());
                }while(crossovers.contains(pairID) || pp1.getId() == pp2.getId());
                crossovers.add(pairID);

                Chromosome p1 = pp1.getChromosome();
                Chromosome p2 = pp2.getChromosome();

                List<Chromosome> children = p1.crossover(p2,0.4);
                newPopulation.addAll(children);
            }

            for(Chromosome c: newPopulation){
                c.mutate(0.05);
                c.memetic(0.3);
            }

            population = newPopulation;
            System.out.println("EPOCH " + e + ": " + bestFit);
            System.out.println("===");
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
