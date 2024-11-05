import system.recommendation.DatasetLoader;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small",true);
//        long startTime = System.currentTimeMillis();
        ParticleSwarmTest.run(datasetLoader);
//        System.out.println(System.currentTimeMillis() - startTime);
//        ContentBasedFilteringTest.run(datasetLoader);
//        long startTime = System.currentTimeMillis();
//        MatrixFactorizationTest.run(datasetLoader);
//        System.out.println(System.currentTimeMillis() - startTime);
//        KnnTest.run(datasetLoader);
//        CollaborativeFilteringTest.run(datasetLoader);
    }
}
