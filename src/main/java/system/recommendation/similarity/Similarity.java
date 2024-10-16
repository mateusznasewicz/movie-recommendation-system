package system.recommendation.similarity;


public interface Similarity<T> {
    double calculateSimilarity(T i, T j);
}
