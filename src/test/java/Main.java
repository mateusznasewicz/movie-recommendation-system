import system.recommendation.DatasetLoader;
import system.recommendation.geneticalgorithm.GeneticAlgorithm;
import system.recommendation.strategy.KMeans;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        DatasetLoader datasetLoader = new DatasetLoader("ml-latest-small",true,false);
//        KnnTest.run(datasetLoader);
        KNNGATEST.run(datasetLoader);
//        ParticleSwarmTest.run(datasetLoader);
//        MatrixFactorizationTest.run(datasetLoader);
//        KMeansTest.run(datasetLoader);
    }
}
