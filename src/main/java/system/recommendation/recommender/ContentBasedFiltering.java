package system.recommendation.recommender;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.TagService;
import system.recommendation.similarity.Cosine;
import system.recommendation.strategy.KNN;
import system.recommendation.strategy.Strategy;


public class ContentBasedFiltering extends Recommender<Movie,User> {

    public ContentBasedFiltering(DatasetLoader datasetLoader, RatingService<Movie,User> rs, Strategy<Movie> strategy){
        super(rs, strategy);
        TagService tagService = new TagService(datasetLoader.getTags(), datasetLoader.getMovies());
        System.out.println("calculating tfidf");
        tagService.calcTFIDF();
        System.out.println("calculating done");
    }

}
