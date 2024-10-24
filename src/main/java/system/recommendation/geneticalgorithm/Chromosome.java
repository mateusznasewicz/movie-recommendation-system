package system.recommendation.geneticalgorithm;

import java.util.List;

public abstract class Chromosome{
    private static int idCtr = 0;
    protected int id;
    protected double fitValue;

    public Chromosome(){
        this.id = idCtr++;
    }
    public double getFitness(){
        return this.fitValue;
    }
    public int getId(){
        return this.id;
    }
    public double fitness(){
        this.fitValue = _fitness();
        return this.fitValue;
    }

    protected abstract double _fitness();
    public abstract Chromosome mutate();
    public abstract List<Chromosome> crossover(Chromosome p);
}
