package system.recommendation.collaborativefiltering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;


public class UserBased extends CollaborativeFiltering<User,Movie> {

    public UserBased(DatasetLoader datasetLoader, int k, Similarity<User> similarity, RatingService<User> rs, boolean RATE_ALL) {
        super(
                datasetLoader.getUsers(),
                datasetLoader.getMovies(),
                k,
                similarity,
                rs,
                RATE_ALL
        );
    }
}
