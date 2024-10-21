package system.recommendation.models;

import java.util.Random;
import java.util.Set;

public abstract class Entity<T>{
    public static int K = 10;
    protected int id;
    protected double[] latentFeatures;
    protected double avgRating = 0;

    public int getId(){
        return id;
    }
    public double[] getLatentFeatures() { return latentFeatures; }
    public double getAvgRating() { return avgRating; }
    abstract public Set<Integer> getCommon(T entity);
    public void initLatentFeatures() {
        this.latentFeatures = new double[K];
        for(int i = 0; i < K; i++){
            Random random = new Random();
            double mean = 0.0;
            double stdDev = 0.01;
            latentFeatures[i] = mean + stdDev * random.nextGaussian();
        }
    }

    public Entity(){
        initLatentFeatures();
    }
}
