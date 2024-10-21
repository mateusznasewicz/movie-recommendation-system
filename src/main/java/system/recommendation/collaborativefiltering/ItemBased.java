package system.recommendation.collaborativefiltering;


import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;


public class ItemBased extends CollaborativeFiltering<Movie,User>{

    public ItemBased(DatasetLoader datasetLoader, int k, Similarity<Movie> similarity, RatingService<Movie> rs, boolean RATE_ALL) {
        super(
                datasetLoader.getMovies(),
                datasetLoader.getUsers(),
                k,
                similarity,
                rs,
                RATE_ALL
        );
    }
}
