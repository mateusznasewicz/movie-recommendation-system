package system.recommendation.geneticalgorithm;


import java.util.List;

public interface Chromosome{
    void mutate();
    double fitness();
    List<Chromosome> crossover(Chromosome p2);
}
