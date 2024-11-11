import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.*;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.*;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;

import java.util.Map;

@SuppressWarnings("unchecked")
public class MatrixFactorizationTest {
    private final static double learningRate = 0.0002;
    private final static double regularization = 0.002;
    private final static int k = 10;
    private final static int populationSize = 100;
    private final static int epochs = 100;
    private final static double gradientWeight = 0.7;

    public static void run(DatasetLoader datasetLoader){
        Map<Integer, User> users = datasetLoader.getUsers();
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        RatingService<User,Movie> userService = new UserService(users,movies);
        ParticleProvider<MMMF> mmmFprovider = new MMMFprovider(userService,k,learningRate,regularization);
        ParticleProvider<NMF> nmFprovider = new NMFprovider(userService,k,learningRate);
        ParticleProvider<RMF> rmFprovider = new RMFprovider(userService,k,learningRate,regularization);

//        double mae = swarmTest(userService,rmFprovider)[0];
//        System.out.println(mae);

//        NMFtest(userService);
//        RMFtest(userService);
//        MMMFtest(userService);
        double mae = RMFtest(userService)[0];
        System.out.println(mae);
//        RMFtest(userService);
//        NMFGAtest(userService);
    }

    private static void NMFtest(RatingService<User,Movie> userService){
        MatrixFactorization mf = new NMF(userService,k,learningRate,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static double[] RMFtest(RatingService<User,Movie> userService){
        RMF mf = new RMF(userService,k,learningRate,regularization,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();

        return new double[]{QualityMeasure.MAE(ratings,userService),QualityMeasure.RMSE(ratings,userService)};
    }

    private static double[] MMMFtest(RatingService<User,Movie> userService){
        MatrixFactorization mf = new MMMF(userService,k,learningRate,regularization,0.01);
        mf.gd(epochs);

        double[][] ratings = mf.getPredictedRatings();
        return new double[]{QualityMeasure.MAE(ratings,userService),QualityMeasure.RMSE(ratings,userService)};
    }

    private static double[] RMFGAtest(RatingService<User,Movie> userService){
        RMFGA mf = new RMFGA(userService,k,learningRate,regularization);
        RMF best = mf.run(populationSize,epochs);

        double[][] ratings = best.getPredictedRatings();

        return new double[]{QualityMeasure.MAE(ratings,userService),QualityMeasure.RMSE(ratings,userService)};
    }

    private static void NMFGAtest(RatingService<User,Movie> userService){
        NMFGA mf = new NMFGA(userService,k,learningRate,regularization);
        NMF best = mf.run(populationSize,epochs);

        double[][] ratings = best.getPredictedRatings();
        System.out.println(QualityMeasure.MAE(ratings,userService));
        System.out.println(QualityMeasure.RMSE(ratings,userService));
    }

    private static <T extends MatrixFactorization> double[] swarmTest(RatingService<User,Movie> ratingService, ParticleProvider<T> pp){
        ParticleSwarm ps = new ParticleSwarm(pp,populationSize,gradientWeight);
        T best = (T) ps.run(epochs);
        double[][] predicted = best.getPredictedRatings();

        return new double[]{QualityMeasure.MAE(predicted,ratingService),QualityMeasure.RMSE(predicted,ratingService)};
    }
}
