package system.recommendation.geneticalgorithm;

import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

public class MatrixProvider implements ChromosomeProvider<MatrixFactorization> {
    private final RatingService<User> userService;
    private final int k;

    public MatrixProvider(RatingService<User> userService, int k) {
        this.k = k;
        this.userService = userService;
    }
    @Override
    public MatrixFactorization getChromosome() {
        return new MatrixFactorization(userService,k);
    }
}
