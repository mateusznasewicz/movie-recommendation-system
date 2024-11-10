package system.recommendation.geneticalgorithm;


import java.util.List;

public interface Chromosome{
    void mutate(double chance);
    double fitness();
    Chromosome copy();
    List<Chromosome> crossover(Chromosome p2, double weight);
}
