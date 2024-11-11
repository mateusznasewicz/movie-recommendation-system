package system.recommendation.particleswarm;

import system.recommendation.models.Entity;
import system.recommendation.service.RatingService;
import system.recommendation.strategy.KMeans;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KmeansPSO<T extends Entity, G extends Entity> {
    private RatingService<T, G> ratingService;
    private int swarmSize;
    private int k;

    List<KMeans<T,G>> swarm = new ArrayList<>();
    List<KMeans<T,G>> localBest = new ArrayList<>();

    double[] localLoss;
    double[][][] v;

    public KmeansPSO(int swarmSize, int k, RatingService<T,G> rs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.swarmSize = swarmSize;
        this.k = k;
        this.ratingService = rs;
        for(int i = 0; i < swarmSize; i++){
            swarm.add(new KMeans<>(k,rs));
        }

        localLoss = new double[swarmSize];
        Arrays.fill(localLoss, Double.MAX_VALUE);

        v = new double[swarmSize][k][ratingService.getItemMap().size()];

        for(int i = 0; i < swarmSize; i++){
            localBest.add(copyParticle(swarm.get(i)));
        }
    }

    private KMeans<T,G> copyParticle(KMeans<T,G> x) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return new KMeans<>(x.getMembership(),x.getCentroids(),k,ratingService);
    }

    public KMeans<T, G> run(int epochs) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        KMeans<T,G> globalBest = null;
        double globalLoss = Double.MAX_VALUE;

        for(int t = 0; t < epochs; t++)
        {
            int bestID = -1;

            for(int i = 0; i < swarmSize; i++){
                KMeans<T,G> km = swarm.get(i);
                double loss = km.calcLoss();

                if(loss < globalLoss){
                    bestID = i;
                    globalLoss = loss;
                }

                if(loss < localLoss[i]){
                    localLoss[i] = loss;
                    localBest.set(i,copyParticle(swarm.get(i)));
                }
            }

            if(bestID != -1){
                globalBest = copyParticle(swarm.get(bestID));
            }

            for(int i = 0; i < swarmSize; i++){
                KMeans<T,G> km = swarm.get(i);
                km.updateVelocity(v[i],localBest.get(i),globalBest);
                km.updateParticle(v[i]);
                km.assignMembership();
            }

            System.out.println("Epoch " + t + "||"+globalLoss);
        }
        return globalBest;
    }
}
