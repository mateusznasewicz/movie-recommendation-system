package system.recommendation.recommender;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.service.TagService;
import system.recommendation.similarity.Cosine;
import system.recommendation.strategy.KNN;


public class ContentBasedFiltering extends Recommender<Movie,User> {

    public ContentBasedFiltering(DatasetLoader datasetLoader, RatingService<Movie,User> rs, int k){
        super(rs,new KNN<>(datasetLoader.getMovies(),k,new Cosine()));
        TagService tagService = new TagService(datasetLoader.getTags(), datasetLoader.getMovies());
        System.out.println("calculating tfidf");
        tagService.calcTFIDF();
        System.out.println("calculating done");
    }

}
