package system.recommendation.similarity;

import system.recommendation.models.Entity;

public interface Similarity<T extends Entity> {
    double calculate(T a, T b);
}
