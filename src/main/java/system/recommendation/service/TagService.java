package system.recommendation.service;

import system.recommendation.models.Movie;

import java.util.Map;

public class TagService {
    private final Map<String, Integer> tags;
    private final Map<Integer, Movie> movies;

    public TagService(Map<String, Integer> tags, Map<Integer, Movie> movies) {
        this.tags = tags;
        this.movies = movies;
    }

    public void calcTFIDF(){
        int moviesAmount = movies.size();
        movies.forEach((_,movie)->{
            movie.getTags().forEach((word,_)->{
                double tf = movie.calcTF(word);
                double df = tags.get(word);
                double idf = Math.log10(moviesAmount/df);
                movie.putTFIDF(word, tf*idf);
            });
        });
    }
}
