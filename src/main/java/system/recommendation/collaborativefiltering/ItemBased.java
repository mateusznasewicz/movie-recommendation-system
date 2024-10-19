package system.recommendation.collaborativefiltering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.predict.Predict;
import system.recommendation.similarity.Similarity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemBased extends CollaborativeFiltering<Movie>{
    private KNN<Movie> knn;

    public ItemBased(DatasetLoader datasetLoader, int k, Similarity<Movie> similarity,boolean RATE_ALL) {
        this.datasetLoader = datasetLoader;
        this.hashmap = datasetLoader.getMovies();
        this.similarity = similarity;
        this.knn = new KNN<>(this.hashmap, k, similarity);
        this.RATE_ALL = RATE_ALL;
    }

    public ItemBased(){}

    @Override
    void fillNeighbor(Movie movie) {
        List<Movie> neighbors = knn.getNeighbors(movie);

        datasetLoader.getUsers().forEach((id,user)->{
            if(this.RATE_ALL || !movie.getRatedByUsers().contains(user)){
                double rating = predictRating(user,movie,neighbors);
                user.addPredictedRating(id, rating);
                if(id == 1){
                    System.out.println("user 1 film "+movie.getId()+" rating: "+rating);
                }
            }
        });
    }

    @Override
    double predictRating(User user, Movie movie, List<Movie> neighbors) {
        return Predict.predictRatingKNN(movie,user,neighbors,this.similarity);
    }

}
