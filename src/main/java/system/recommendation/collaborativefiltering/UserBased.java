package system.recommendation.collaborativefiltering;

import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.Map;

public class UserBased extends CollaborativeFiltering<User,Movie> {

    public UserBased(Map<Integer, User> baseHashmap, Map<Integer,Movie> itemHashmap, int k, Similarity<User> similarity, RatingService<User> ratingService, boolean RATE_ALL) {
        super(baseHashmap,itemHashmap,k,similarity,ratingService,RATE_ALL);
    }
}
