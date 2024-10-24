import system.recommendation.DatasetLoader;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small");
        GeneticAlgorithmTest.run(datasetLoader);
        //ContentBasedFilteringTest.run(datasetLoader);
        //MatrixFactorizationTest.run(datasetLoader);
        //KnnTest.run(datasetLoader);
        //CollaborativeFilteringTest.run(datasetLoader);
    }
}
