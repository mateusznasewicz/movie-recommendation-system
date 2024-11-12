package system.recommendation.geneticalgorithm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.*;

public class KnnChromosome<T extends Entity, G extends Entity> implements Chromosome{
    private final int[] neighbors;
    private final double[] weights;

    private final T item;
    private final RatingService<T,G> ratingService;
    private final SplittableRandom random = new SplittableRandom();

    public KnnChromosome(T item, RatingService<T,G> ratingService, List<Integer> n, double[][] simMatrix) {
        this.item = item;
        this.ratingService = ratingService;
        neighbors = new int[n.size()];
        weights = new double[n.size()];

        for(int i = 0; i < n.size(); i++) {
            neighbors[i] = n.get(i);
            weights[i] = simMatrix[item.getId()-1][n.get(i)-1] + random.nextDouble(-0.1, 0.1);
        }
    }

    public KnnChromosome(T item, RatingService<T,G> ratingService, int[] neighbors, double[] weights) {
        this.item = item;
        this.neighbors = neighbors;
        this.weights = weights;
        this.ratingService = ratingService;
    }

    public List<Integer> getNeighbors(){
        return Arrays.stream(neighbors).boxed().toList();
    }

    @Override
    public void mutate(double chance){

    }

    @Override
    public void memetic(double chance) {
        for(int i = 0; i < weights.length; i++) {
            if(random.nextDouble() < chance) {
                weights[i] = random.nextDouble();
            }
        }
    }

    @Override
    public double fitness(){
        double error = 0;
        int n = 0;

        Map<Integer,Double> ratings = item.getRatings();

        for(Map.Entry<Integer,Double> entry : ratings.entrySet()) {
            int iID = entry.getKey();
            double rating = entry.getValue();
            double predicted = predict(iID);

            if(predicted == -1) continue;
            error += Math.abs(predicted - rating);
            n++;
        }

        return error/n;
    }

    @Override
    public Chromosome copy(){
        return new KnnChromosome<>(item,ratingService,neighbors.clone(),weights.clone());
    }

    private double predict(int iID){
        double numerator = 0;
        double denominator = 0;

        int i = 0;
        for(int nID: neighbors){
            double sim = weights[i];
            i++;
            if(!ratingService.isRatedById(nID, iID))continue;
            numerator += sim * ratingService.getRating(nID,iID);
            denominator += Math.abs(sim);
        }

        if(numerator == 0) return -1;
        return numerator / denominator;
    }

    @Override
    public List<Chromosome> crossover(Chromosome p, double weight) {
        double[] w1 = new double[weights.length];
        double[] w2 = new double[weights.length];
        double[] p1 = this.weights;
        double[] p2 = ((KnnChromosome<T,G>) p).weights;

        for(int i = 0; i < weights.length; i++) {
            w1[i] = p1[i]*weight + (1-weight)*p2[i];
            w2[i] = p2[i]*weight + (1-weight)*p1[i];
        }

        var c1 = new KnnChromosome<>(item, ratingService, neighbors, w1);
        var c2 = new KnnChromosome<>(item, ratingService, neighbors, w2);
        return List.of(c1,c2);
    }

    @Override
    public double[][] getChromosome() {
        return new double[0][];
    }
}
