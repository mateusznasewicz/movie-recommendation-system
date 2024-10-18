package system.recommendation.collaborativefiltering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemBased extends CollaborativeFiltering<Movie>{
    private KNN<Movie> knn;

    public ItemBased(DatasetLoader datasetLoader, int k, boolean RATE_ALL) {
        this.datasetLoader = datasetLoader;
        this.hashmap = datasetLoader.getMovies();
        this.knn = new KNN<>(this.hashmap, k, this);
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
        //System.out.println("Oceniono film " + movie.getId() + " dla wszystkich uzytkownikow");
    }

    @Override
    double predictRating(User user, Movie movie, List<Movie> neighbors) {
        double numerator = 0;
        double denominator = 0;
        for(Movie neighbor: neighbors){
            if(!neighbor.getRatedByUsers().contains(user)) continue;
            int movieId = neighbor.getId();
            numerator += pearsonCorrelation(movie,neighbor) * user.getRating(movieId);
            denominator += Math.abs(pearsonCorrelation(movie,neighbor));
        }

        return numerator / denominator;
    }

    private Set<User> findCommonUsers(Movie i, Movie j){
        Set<User> jUsers = j.getRatedByUsers();
        Set<User> commonUsers = new HashSet<>();

        for(User iUser: i.getRatedByUsers()){
            if(jUsers.contains(iUser)){
                commonUsers.add(iUser);
            }
        }

        return commonUsers;
    }

    @Override
    public double pearsonCorrelation(Movie i, Movie j) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<User> commonUsers = findCommonUsers(i,j);
        if(commonUsers.isEmpty()) return 0;

        for(User user: commonUsers){
            numerator += (user.getRating(i.getId()) - user.getAvgRating())*(user.getRating(j.getId()) - user.getAvgRating());
            s1 += Math.pow((user.getRating(i.getId()) - user.getAvgRating()),2);
            s2 += Math.pow((user.getRating(j.getId()) - user.getAvgRating()),2);
        }

        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
    }

    @Override
    public double cosineSimilarity(Movie a, Movie b) {
        return 0;
    }
}
