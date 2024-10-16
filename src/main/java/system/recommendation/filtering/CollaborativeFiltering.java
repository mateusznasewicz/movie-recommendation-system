package system.recommendation.filtering;

import system.recommendation.DatasetLoader;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.similarity.Similarity;

import java.util.Set;

/*
Find the rating R that a user U would give to an item I
*/
public class CollaborativeFiltering {
    private DatasetLoader datasetLoader;

    public CollaborativeFiltering(DatasetLoader datasetLoader){
        this.datasetLoader = datasetLoader;
    }
    /*
    1.Find users similar to U who have rated the item I
    2.Calculate the rating R based the ratings of users found in (1)

    (2) - avg
    (2) -  (sum of ratings times similarity to original user)/sum of similarities to original
    */
    public void UserBased(){

    }

    private double predictRating(User user, Movie movie, Set<User> neighbors, Similarity<?> simFunction){
        double nominator = 0;
        double denominator = 0;

        for(User neighbor : neighbors){

        }

        return user.getAvgRating() + nominator / denominator;
    }
}
