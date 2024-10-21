package system.recommendation.similarity;

import system.recommendation.models.Movie;

import java.util.Map;
import java.util.Set;

public class Cosine implements Similarity<Movie>{
    @Override
    public double calculate(Movie a, Movie b) {
        double numerator = 0;
        double s1 = 0;
        double s2 = 0;
        Map<String, Double> aTFIDF = a.getTFIDF();
        Map<String, Double> bTFIDF = b.getTFIDF();

        for (Map.Entry<String, Double> entry : aTFIDF.entrySet()) {
            String word = entry.getKey();
            Double tfidf = aTFIDF.get(word);
            s1 += Math.pow(tfidf, 2);
            if(bTFIDF.containsKey(word)) {
                numerator += tfidf * bTFIDF.get(word);
            }
        }

        for(Double tfidf: bTFIDF.values()) {
            s2 += Math.pow(tfidf, 2);
        }

        Set<String> bGenres = b.getGenres();
        for(String tag: a.getGenres()){
            if(bGenres.contains(tag)){
                numerator += 1;
            }
        }

        s1 += a.getGenres().size();
        s2 += b.getGenres().size();

        if(numerator == 0) return 0;
        return numerator/Math.sqrt(s1*s2);
    }
}
