import system.recommendation.DatasetLoader;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small");
        //ContentBasedFilteringTest.run(datasetLoader);
        //MatrixFactorizationTest.run(datasetLoader);
        //KnnTest.run(datasetLoader);
        CollaborativeFilteringTest.run(datasetLoader);
    }
}
