package system.recommendation.geneticalgorithm;

import system.recommendation.matrixfactorization.MatrixFactorization;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

public class MatrixProvider implements ChromosomeProvider {
    private final RatingService<User> userService;
    private final int k;
    private final double learningRate;
    private final double regularization;

    public MatrixProvider(RatingService<User> userService, int k,double learningRate, double regularization) {
        this.k = k;
        this.userService = userService;
        this.learningRate = learningRate;
        this.regularization = regularization;
    }
    @Override
    public MatrixFactorization getChromosome() {
        return new MatrixFactorization(userService,k,learningRate,regularization);
    }
}
