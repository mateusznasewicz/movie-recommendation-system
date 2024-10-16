package system.recommendation.collaborative_filtering;

import system.recommendation.DatasetLoader;
import system.recommendation.filtering.KNN;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

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
    public UserBased(DatasetLoader datasetLoader, int k, boolean RATE_ALL) {
        this.datasetLoader = datasetLoader;
        this.hashmap = datasetLoader.getUsers();
        this.knn = new KNN<>(this.hashmap, k, this);
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
    //problem taki ze sa filmy dla ktorych z sasiedztwa nikt nie ocenil
    double predictRating(User user, Movie movie, List<User> neighbors) {
        double numerator = 0;
        double denominator = 0;
        int movieId = movie.getId();

        for(User neighbor: neighbors){
            if(!movie.getRatedByUsers().contains(neighbor)) continue;
            numerator += pearsonCorrelation(user,neighbor) * (neighbor.getRating(movieId) - neighbor.getAvgRating());
            denominator += Math.abs(pearsonCorrelation(user,neighbor));
        }

        return user.getAvgRating() + numerator / denominator;
    }

    private Set<Integer> findCommonMovies(User i, User j){
        HashMap<Integer,Double> jRatings = j.getRatings();
        Set<Integer> commonMovies = new HashSet<>();

        i.getRatings().forEach((key, _) ->{
            if(jRatings.containsKey(key)){
                commonMovies.add(key);
            }
        });

        return commonMovies;
    }

    @Override
    public double pearsonCorrelation(User i, User j) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<Integer> commonMovies = findCommonMovies(i, j);
        if(commonMovies.isEmpty()) return 0;

        for(Integer id: commonMovies){
            numerator += (i.getRating(id) - i.getAvgRating())*(j.getRating(id) - j.getAvgRating());
            s1 += Math.pow((i.getRating(id) - i.getAvgRating()),2);
            s2 += Math.pow((j.getRating(id) - j.getAvgRating()),2);
        }
        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
    }

    @Override
    public double cosineSimilarity(User a, User b) {
        return 0;
    }
}
