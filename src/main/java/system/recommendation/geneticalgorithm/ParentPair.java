package system.recommendation.geneticalgorithm;

public class ParentPair {
    int parent1;
    int parent2;

    public ParentPair(int p1, int p2) {
        if (p1 < p2) {
            this.parent1 = p1;
            this.parent2 = p2;
        } else {
            this.parent1 = p2;
            this.parent2 = p1;
        }
    }

    public String toString(){
        return parent1 + " " + parent2;
    }
}
