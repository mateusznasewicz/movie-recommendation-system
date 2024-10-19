package system.recommendation.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Movie extends Entity{
    private final Set<User> ratedByUsers = new HashSet<>();
    private final Map<String,Integer> tags = new HashMap<>();
    private final Set<String> genres = new HashSet<>();

    public Movie(int id){
        this.id = id;
    }

    public void addUser(User user) {
        ratedByUsers.add(user);
    }
    public void addTag(String tag) {
        if(!tags.containsKey(tag)) {
            tags.put(tag,1);
        }else{
            tags.put(tag,tags.get(tag)+1);
        }

    }
    public void addGenre(String genre) { genres.add(genre); }

    public Set<User> getRatedByUsers() {
        return ratedByUsers;
    }
    public Map<String,Integer> getTags() { return tags; }
    public Set<String> getGenres() { return genres; }
}
