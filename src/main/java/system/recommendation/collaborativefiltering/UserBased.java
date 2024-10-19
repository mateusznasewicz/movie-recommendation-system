package system.recommendation.collaborativefiltering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.predict.Predict;
import system.recommendation.similarity.Similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserBased extends CollaborativeFiltering<User> {

    private KNN<User> knn;

    /**
     * @param datasetLoader
     * @param k - parametr okreslajacy liczbe sasiadow dla knn
     * @param RATE_ALL - parametr uzywany do testow. Ocenia wszystko, nawet juz ocenione filmy
     */
    public UserBased(DatasetLoader datasetLoader, int k, Similarity<User> similarity, boolean RATE_ALL) {
        this.datasetLoader = datasetLoader;
        this.hashmap = datasetLoader.getUsers();
        this.knn = new KNN<>(this.hashmap, k, similarity);
        this.RATE_ALL = RATE_ALL;
    }

    public UserBased(){}

    @Override
    void fillNeighbor(User user) {
        List<User> neighbors = knn.getNeighbors(user);

        datasetLoader.getMovies().forEach((id,movie)->{
            if(this.RATE_ALL || !movie.getRatedByUsers().contains(user)){
                double rating = predictRating(user,movie,neighbors);
                user.addPredictedRating(id, rating);
            }
        });
        System.out.println("Oceniono wszystkie filmy dla uzytkownika " + user.getId());
    }

    @Override
    double predictRating(User user, Movie movie, List<User> neighbors) {
        return Predict.predictRatingKNN(user,movie,neighbors,this.similarity);
    }
}
