package system.recommendation.geneticalgorithm;

import java.util.List;

public abstract class Chromosome<T>{
    protected double value;
    private static int idCounter = 0;
    protected int id;

    public double getFitness() {
        return this.value;
    }

    public int getId() {
        return this.id;
    }

    public Chromosome() {
        this.id = idCounter++;
    }

    public abstract double fitness();
    public abstract void mutate();
    public abstract List<T> crossover(T p);
}
