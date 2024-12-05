package system.recommendation.geneticalgorithm;


import java.util.List;

/*
Chromosom używany w algorytmie genetycznym
 */
public interface Chromosome{
    void mutate(double chance);
    void memetic(double chance);
    double fitness();
    Chromosome copy();
    List<Chromosome> crossover(Chromosome p2, double weight);

    double[][] getChromosome();
}
