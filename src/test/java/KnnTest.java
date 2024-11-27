import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.ContentBasedFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;
import system.recommendation.similarity.*;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KnnTest {
    public static void run(DatasetLoader datasetLoader) throws IOException {
        saveToFile(datasetLoader,"knn_content");
//        singleTest(datasetLoader);
    }

    private static void saveToFile(DatasetLoader datasetLoader, String filename) throws IOException {
        RatingService<Movie, User> rs = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());
        BufferedWriter maeFile = new BufferedWriter(new FileWriter("data/"+filename+"_MAE"));
        BufferedWriter rmseFile = new BufferedWriter(new FileWriter("data/"+filename+"_RMSE"));

        for(int k = 1; k < 100; k++){
            Strategy<Movie> strategy = new KNN<>(datasetLoader.getMovies(), k, new Cosine());
            Recommender<Movie, User> recommender = new ContentBasedFiltering(datasetLoader,rs,strategy);
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

    private static void singleTest(DatasetLoader datasetLoader){
        RatingService<Movie, User> rs = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Strategy<Movie> strategy = new KNN<>(datasetLoader.getMovies(), 10, new Cosine());
        Recommender<Movie, User> recommender = new ContentBasedFiltering(datasetLoader,rs,strategy);
        double[][] predicted = recommender.getPredictedRating();
        double[] e = new double[]{QualityMeasure.MAE(predicted,rs,false),QualityMeasure.RMSE(predicted,rs)};
        System.out.println(e[0]);
    }
}
