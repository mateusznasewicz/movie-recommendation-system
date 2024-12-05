package system.recommendation.geneticalgorithm;

/*
Para : chromosom, id
 */
public class ChromosomePairID {
    private int id;
    private Chromosome chromosome;

    public ChromosomePairID(int id, Chromosome chromosome) {
        this.id = id;
        this.chromosome = chromosome;
    }

    public Chromosome getChromosome() {
        return chromosome;
    }

    public int getId() {
        return id;
    }
}
