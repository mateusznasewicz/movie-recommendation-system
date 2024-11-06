package system.recommendation.recommender;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.Strategy;

import java.util.*;

public class CollaborativeFiltering<T extends Entity, G extends Entity> extends Recommender<T,G> {

    public CollaborativeFiltering(RatingService<T,G> ratingService, Strategy<T> strategy){
        super(ratingService,strategy);
    }
}
