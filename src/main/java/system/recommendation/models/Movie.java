package system.recommendation.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Movie extends Entity{
    private final Map<String,Integer> tags = new HashMap<>();
    private final Map<String, Double> TFIDF = new HashMap<>();
    private final Set<String> genres = new HashSet<>();

    public Movie(int id){
        this.id = id;
    }

    public void putTFIDF(String word, double tfidf){
        this.TFIDF.put(word, tfidf);
    }

    public double calcTF(String word){
        return (double) tags.get(word)/tags.size();
    }

    public Map<String, Double> getTFIDF(){
        return this.TFIDF;
    }


    public void addTag(String tag) {
        if(!tags.containsKey(tag)) {
            tags.put(tag,1);
        }else{
            tags.put(tag,tags.get(tag)+1);
        }

    }
    public void addGenre(String genre) { genres.add(genre); }

    public Map<String,Integer> getTags() { return tags; }
    public Set<String> getGenres() { return genres; }
}
