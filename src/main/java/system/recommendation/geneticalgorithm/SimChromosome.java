package system.recommendation.geneticalgorithm;

import system.recommendation.QualityMeasure;
import system.recommendation.Utils;
import system.recommendation.models.Entity;
import system.recommendation.recommender.CollaborativeFiltering;
import system.recommendation.recommender.Recommender;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.KNN;

import java.util.List;
import java.util.SplittableRandom;

public class SimChromosome<T extends Entity, G extends Entity> extends KNN<T> implements Chromosome{
    private final RatingService<T,G> ratingService;
    private final SplittableRandom random = new SplittableRandom();

    public SimChromosome(RatingService<T,G> ratingService, double[][] simMatrix, int k) {
        super(k,simMatrix);
        this.ratingService = ratingService;
    }



    @Override
    public void mutate(double chance){

    }

    @Override
    public void memetic(double chance) {
        if(random.nextDouble() >= chance) return;
        int i = random.nextInt(this.simMatrix.length);
        int j;
        do{
            j = random.nextInt(this.simMatrix.length);
        }while(i == j);
        double r = random.nextDouble();
        simMatrix[i][j] = r;
        simMatrix[j][i] = r;
    }

    @Override
    public double fitness(){
        double[][] predictedRating = new double[simMatrix.length][ratingService.getItemMap().size()];

        for(int i = 0;  i < simMatrix.length;  i++){
            T entity = ratingService.getEntity(i+1);
            List<Integer> neighbors = super.getNeighbors(entity);
            for(Integer iID : entity.getRatings().keySet()){
                double rating = predict(i+1,iID,neighbors);
                predictedRating[i][iID-1] = rating;
            }
        }
        return QualityMeasure.MAE(predictedRating,ratingService,true);
    }

    public double predict(int eID, int iID, List<Integer> neighbors){
        double numerator = 0;
        double denominator = 0;

        for(Integer nID: neighbors){
            double sim = simMatrix[eID-1][nID-1];
            if(!ratingService.isRatedById(nID, iID) || sim < 0) continue;
            numerator += sim * ratingService.getRating(nID,iID);
            denominator += sim;
        }

        if(numerator == 0) return -1;
        return numerator / denominator;
    }

    @Override
    public Chromosome copy(){
        return new SimChromosome<>(ratingService, Utils.deepCopy(simMatrix),k);
    }

    @Override
    public List<Chromosome> crossover(Chromosome p, double weight) {
       double[][] c1 = new double[simMatrix.length][simMatrix.length];
       double[][] c2 = new double[simMatrix.length][simMatrix.length];


       SimChromosome p2 = (SimChromosome)p;

       for(int i = 0; i < simMatrix.length; i++){
           for(int j = 0; j < simMatrix.length; j++){
               if(random.nextBoolean()){
                   c1[i][j] = simMatrix[i][j];
                   c2[i][j] = p2.simMatrix[j][i];
               }else{
                   c2[i][j] = simMatrix[i][j];
                   c1[i][j] = p2.simMatrix[j][i];
               }
           }
       }

        return List.of(new SimChromosome<>(ratingService,c1,k),new SimChromosome<>(ratingService,c2,k));
    }

    @Override
    public double[][] getChromosome() {
        return new double[0][];
    }
}