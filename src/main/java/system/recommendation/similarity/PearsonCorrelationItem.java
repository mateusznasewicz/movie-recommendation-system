package system.recommendation.similarity;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.util.Set;

public class PearsonCorrelationItem implements Similarity<Movie>{
    @Override
    public double calculate(Movie a, Movie b) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<User> commonUsers = findCommonUsers(a,b);
        if(commonUsers.isEmpty()) return 0;

        for(User user: commonUsers){
            numerator += (user.getRating(a.getId()) - user.getAvgRating())*(user.getRating(b.getId()) - user.getAvgRating());
            s1 += Math.pow((user.getRating(a.getId()) - user.getAvgRating()),2);
            s2 += Math.pow((user.getRating(b.getId()) - user.getAvgRating()),2);
        }

        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
    }
}
