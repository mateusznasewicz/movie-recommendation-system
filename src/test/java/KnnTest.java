import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.AdjustedCosine;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.PearsonCorrelation;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KnnTest {
    public static void run(DatasetLoader datasetLoader) throws IOException {
        saveToFile(datasetLoader,"knn_item_pearson");
    }

    private static void saveToFile(DatasetLoader datasetLoader, String filename) throws IOException {
        RatingService<Movie, User> rs = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<Movie> sim = new PearsonCorrelation<>(rs);
        BufferedWriter maeFile = new BufferedWriter(new FileWriter("data/"+filename+"_MAE"));
        BufferedWriter rmseFile = new BufferedWriter(new FileWriter("data/"+filename+"_RMSE"));

        for(int k = 1; k < 100; k++){
            Strategy<Movie> strategy = new KNN<>(datasetLoader.getMovies(), k, sim);
            Recommender<Movie, User> recommender = new CollaborativeFiltering<>(rs,strategy);
            double[][] predicted = recommender.getPredictedRating();
            double[] e = new double[]{QualityMeasure.MAE(predicted,rs,false),QualityMeasure.RMSE(predicted,rs)};
            maeFile.write(k + " " + e[0]);
            rmseFile.write(k + " " + e[1]);
            maeFile.newLine();
            rmseFile.newLine();
            System.out.println("REP:"+k);
        }

        maeFile.close();
        rmseFile.close();
    }
}
