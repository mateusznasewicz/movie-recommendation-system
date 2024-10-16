package system.recommendation.similarity;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.HashSet;
import java.util.Set;

public class PearsonCorrelationItemBased implements Similarity<Movie> {
    @Override
    public double calculateSimilarity(Movie i, Movie j) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<User> commonUsers = findCommonUsers(i,j);

        for(User user: commonUsers){
            numerator += (user.getRating(i.getId()) - user.getAvgRating())*(user.getRating(j.getId()) - user.getAvgRating());
            s1 += Math.pow((user.getRating(i.getId()) - user.getAvgRating()),2);
            s2 += Math.pow((user.getRating(j.getId()) - user.getAvgRating()),2);
        }

        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
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
}
