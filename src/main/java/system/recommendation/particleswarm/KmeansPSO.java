package system.recommendation.particleswarm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.KMeans;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class KmeansPSO<T extends Entity, G extends Entity> {
    private int swarmSize;
    private int k;
    List<KMeans<T,G>> kmeans = new ArrayList<>();
    List<KMeans<T,G>> localBest = new ArrayList<>();
    List<KMeans<T,G>> v = new ArrayList<>();

    double[] localLoss = new double[swarmSize];

    public KmeansPSO(int swarmSize, int k, RatingService<T,G> rs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.swarmSize = swarmSize;
        this.k = k;
        for(int i = 0; i < swarmSize; i++){
            kmeans.add(new KMeans<>(k,rs));
        }
    }

    public KMeans<T, G> run(int epochs){
        KMeans<T,G> globalBest = null;
        double globalLoss = Double.MAX_VALUE;

        for(int t = 0; t < epochs; t++)
        {
            int bestID = -1;

            for(int i = 0; i < swarmSize; i++){
                KMeans<T,G> km = kmeans.get(i);
                double loss = km.calcLoss();

                if(loss < globalLoss){
                    bestID = i;
                    globalLoss = loss;
                }

                if(loss < localLoss[i]){
                    localLoss[i] = loss;
                    localBest.set(i,kmeans.get(i));
                }
            }

            if(bestID != -1){
                globalBest = kmeans.get(bestID);
            }

            for(int i = 0; i < swarmSize; i++){
                KMeans<T,G> km = kmeans.get(i);
                km.updateVelocity(v.get(i));
                km.updateParticle();
            }

            System.out.println("Epoch " + t + "||"+globalLoss);
        }
        return globalBest;
    }
}
