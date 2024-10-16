package system.recommendation.similarity;

import system.recommendation.models.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PearsonCorrelationUserBased implements Similarity<User> {

    @Override
    public double calculateSimilarity(User i, User j) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Set<Integer> commonMovies = findCommonMovies(i, j);

        for(Integer id: commonMovies){
            numerator += (i.getRating(id) - i.getAvgRating())*(j.getRating(id) - j.getAvgRating());
            s1 += Math.pow((i.getRating(id) - i.getAvgRating()),2);
            s2 += Math.pow((j.getRating(id) - j.getAvgRating()),2);
        }

        double denominator = Math.sqrt(s1 * s2);

        return numerator/denominator;
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
}
