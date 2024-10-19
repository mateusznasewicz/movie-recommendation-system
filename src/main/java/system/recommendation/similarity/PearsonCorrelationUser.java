package system.recommendation.similarity;

import system.recommendation.models.User;

import java.util.Set;

public class PearsonCorrelationUser implements Similarity<User>{
    @Override
    public double calculate(User a, User b) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<Integer> commonMovies = findCommonMovies(a, b);
        if(commonMovies.isEmpty()) return 0;

        for(Integer id: commonMovies){
            numerator += (a.getRating(id) - a.getAvgRating())*(b.getRating(id) - b.getAvgRating());
            s1 += Math.pow((a.getRating(id) - a.getAvgRating()),2);
            s2 += Math.pow((b.getRating(id) - b.getAvgRating()),2);
        }
        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
    }
}
