package system.recommendation.geneticalgorithm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;

import java.util.*;

public class KnnChromosome<T extends Entity, G extends Entity> implements Chromosome{
    private final double[] weights;
    private int k;

    private final T item;
    private final RatingService<T,G> ratingService;
    private final SplittableRandom random = new SplittableRandom();

    public KnnChromosome(T item, RatingService<T,G> ratingService, int k) {
        this.k = k;
        this.item = item;
        this.ratingService = ratingService;
        weights = new double[ratingService.getEntityMap().size()];

        for(int i = 0; i < weights.length; i++) {
            weights[i] = random.nextDouble();
        }
    }

    public KnnChromosome(T item, RatingService<T,G> ratingService, double[] weights, int k) {
        this.item = item;
        this.weights = weights;
        this.ratingService = ratingService;
        this.k = k;
    }

    public int getID(){
        return this.item.getId();
    }

    public double[] getWeights(){
        return this.weights;
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
        List<Integer> nei = neighborsLocal();

        for(Map.Entry<Integer,Double> entry : ratings.entrySet()) {
            int iID = entry.getKey();
            double rating = entry.getValue();

            double predicted = predict(iID,nei);

            if(predicted == -1) continue;
            error += Math.abs(predicted - rating);
            n++;
        }
//        System.out.println(error/n + " " + n);
        if(n == 0) return  Double.MAX_VALUE;
        return error / n;
    }

    @Override
    public Chromosome copy(){
        return new KnnChromosome<>(item,ratingService,weights.clone(),k);
    }

    public List<Integer> neighborsLocal(){
        Queue<Integer> queue = new PriorityQueue<>(k,(a, b) -> Double.compare(weights[a-1], weights[b-1]));

        for(int nID = 1; nID < weights.length+1; nID++){
            if(nID != item.getId()){
                queue.add(nID);
                if (queue.size() > k) {
                    queue.poll();
                }
            }
        }

        return queue.stream().toList();
    }

    private double predict(int iID, List<Integer> n){
        double numerator = 0;
        double denominator = 0;

        for(int nID: n){
            double sim = weights[nID-1];
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

//        for(int i = 0; i < weights.length; i++) {
//            if(random.nextBoolean()){
//                w1[i] = p1[i];
//                w2[i] = p2[i];
//            }else  {
//                w1[i] = p2[i];
//                w2[i] = p1[i];
//            }
//        }

        var c1 = new KnnChromosome<>(item, ratingService, w1, k);
        var c2 = new KnnChromosome<>(item, ratingService, w2, k);
        return List.of(c1,c2);
    }

    @Override
    public double[][] getChromosome() {
        return new double[0][];
    }
}
