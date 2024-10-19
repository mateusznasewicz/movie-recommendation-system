package system.recommendation.similarity;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public interface Similarity<T> {
    double calculate(T a, T b);
    default Set<Integer> findCommonMovies(User a, User b) {
        HashMap<Integer,Double> jRatings = a.getRatings();
        Set<Integer> commonMovies = new HashSet<>();

        b.getRatings().forEach((key, _) ->{
            if(jRatings.containsKey(key)){
                commonMovies.add(key);
            }
        });

        return commonMovies;
    }
    default Set<User> findCommonUsers(Movie a, Movie b){
        Set<User> jUsers = a.getRatedByUsers();
        Set<User> commonUsers = new HashSet<>();

        for(User iUser: b.getRatedByUsers()){
            if(jUsers.contains(iUser)){
                commonUsers.add(iUser);
            }
        }

        return commonUsers;
    }
}
