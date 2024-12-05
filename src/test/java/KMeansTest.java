import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.models.Entity;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.KMEANSprovider;
import system.recommendation.particleswarm.ParticleProvider;
import system.recommendation.particleswarm.ParticleSwarm;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.MovieService;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.EuclideanDistance;
import system.recommendation.similarity.Similarity;
import system.recommendation.strategy.FuzzyCMeans;
import system.recommendation.strategy.KMeans;
import system.recommendation.strategy.Strategy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class KMeansTest {
    private final static int epochs = 10;
    private final static int k = 20;
    private final static int swarmSize = 50;
    private static final double fuzzines = 1.5;

    public static void run(DatasetLoader datasetLoader) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        RatingService<Movie,User> rs = new MovieService(datasetLoader.getUsers(), datasetLoader.getMovies());
        Similarity<Movie> sim = new EuclideanDistance<>(rs);
//        Strategy<Movie> s = KMeansPSOTest(rs,sim);
        Strategy<Movie> s = KMeansTest(rs,sim);

        Recommender<Movie,User> r = new CollaborativeFiltering<>(rs,s);
        double[][] pred = r.getPredictedRating();
        System.out.println(QualityMeasure.MAE(pred,rs,false));
        System.out.println(QualityMeasure.RMSE(pred,rs));
    }


    public static Strategy<Movie> FuzzyCMeansTest(RatingService<Movie, User> rs,Similarity<Movie> sim, int k) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FuzzyCMeans<Movie,User> x = new FuzzyCMeans<>(rs, sim,k,fuzzines);
        x.calcCentroids(epochs);
        return x;
    }

    public static Strategy<Movie> KMeansTest(RatingService<Movie, User> rs, Similarity<Movie> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        KMeans<Movie,User> x = new KMeans<>(k,rs,sim);
        x.calcCentroids(epochs);
        return x;
    }

    public static Strategy<Movie> KMeansPSOTest(RatingService<Movie, User> rs, Similarity<Movie> sim) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ParticleProvider pp = new KMEANSprovider<>(k,rs);
        ParticleSwarm ps = new ParticleSwarm(pp,swarmSize,0);
        KMeans<Movie,User> p = (KMeans<Movie, User>) ps.run(epochs);

        KMeans<Movie,User> x = new KMeans<>(p.getRatingService(),sim,k,rs);
        x.calcCentroids(epochs,p.getCentroids());
        return x;
    }
}
