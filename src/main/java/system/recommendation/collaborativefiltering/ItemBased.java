package system.recommendation.collaborativefiltering;


import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.Map;

public class ItemBased extends CollaborativeFiltering<Movie,User>{

    public ItemBased(Map<Integer, Movie> baseHashmap, Map<Integer,User> itemHashmap, int k, Similarity<Movie> similarity, RatingService<Movie> ratingService, boolean RATE_ALL) {
        super(baseHashmap,itemHashmap,k,similarity,ratingService,RATE_ALL);
    }
}
