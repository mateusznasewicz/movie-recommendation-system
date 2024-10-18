package system.recommendation.models;

import java.util.Random;

public abstract class Entity{
    public static int K = 10;
    protected int id;
    protected double[] latentFeatures;

    public int getId(){
        return id;
    }
    public double[] getLatentFeatures() { return latentFeatures; }
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
