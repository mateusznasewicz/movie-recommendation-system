import system.recommendation.DatasetLoader;
import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.MMMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.particleswarm.MMMFprovider;
import system.recommendation.particleswarm.ParticleProvider;
import system.recommendation.particleswarm.ParticleSwarm;
import system.recommendation.service.RatingService;
import system.recommendation.service.UserService;

import java.util.Map;

public class ParticleSwarmTest {
    public static void run(DatasetLoader datasetLoader){
        Map<Integer, Movie> movies = datasetLoader.getMovies();
        Map<Integer,User> users = datasetLoader.getUsers();
        RatingService<User,Movie> ratingService = new UserService(users,movies);
        ParticleProvider provider = new MMMFprovider(ratingService,10,0.002,0);
        ParticleSwarm ps = new ParticleSwarm(provider,100,0);
        MMMF best = (MMMF) ps.run();
        double[][] predicted = best.getPredictedRatings();

        System.out.println(QualityMeasure.MAE(predicted,ratingService));
        System.out.println(QualityMeasure.RMSE(predicted,ratingService));
    }
}
