package system.recommendation.collaborativefiltering;

public interface Similarity<T> {
    double pearsonCorrelation(T a, T b);
    double cosineSimilarity(T a, T b);
}
