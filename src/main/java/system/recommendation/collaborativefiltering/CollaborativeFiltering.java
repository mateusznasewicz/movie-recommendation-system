package system.recommendation.collaborativefiltering;

import system.recommendation.KnnRecommender;
import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.util.*;

public abstract class CollaborativeFiltering<T extends Entity, G extends Entity> extends KnnRecommender<T,G> {

    public CollaborativeFiltering(Map<Integer, T> baseHashmap,
                                  Map<Integer,G> itemHashmap,
                                  int k,
                                  Similarity<T> similarity,
                                  RatingService<T> ratingService,
                                  boolean RATE_ALL
    ){
        super(ratingService,k,baseHashmap,itemHashmap,similarity,RATE_ALL);
    }
}
