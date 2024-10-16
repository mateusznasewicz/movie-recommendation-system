package system.recommendation.models;

import java.util.HashSet;
import java.util.Set;

public class Movie {
    private final int id;
    private final Set<User> ratedByUsers = new HashSet<>();

    public Movie(int id){
        this.id = id;
    }

    public void addUser(User user) {
        ratedByUsers.add(user);
    }

    public Set<User> getRatedByUsers() {
        return ratedByUsers;
    }

    public int getId() {
        return id;
    }
}
