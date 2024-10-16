package system.recommendation.collaborative_filtering;

public interface Similarity<T> {
    double pearsonCorrelation(T a, T b);
    double cosineSimilarity(T a, T b);
}
