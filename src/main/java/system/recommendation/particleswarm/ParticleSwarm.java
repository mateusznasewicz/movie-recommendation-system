package system.recommendation.particleswarm;

import java.util.ArrayList;
import java.util.List;

public class ParticleSwarm{
    private final List<Particle> swarm = new ArrayList<>();
    private final double gradientWeight;

    public ParticleSwarm(ParticleProvider pp, int swarmSize, double gradientWeight){
        this.gradientWeight = gradientWeight;
        for(int i = 0; i < swarmSize; i++){
            swarm.add(pp.initParticle());
        }
    }

    public Particle run(int epochs){
        Particle globalBest = null;
        double globalBestLoss = Double.MAX_VALUE;

        for(int t = 0; t < epochs; t++){
            double bestLoss = Double.MAX_VALUE;
            Particle best = null;

            for(int i = 0; i < swarm.size(); i++){
                Particle p = swarm.get(i);
                double loss = p.getLoss();

                if(loss < bestLoss){
                    best = p;
                    bestLoss = loss;
                }

                if(loss < globalBestLoss){
                    globalBest = p;
                    globalBestLoss = loss;
                }
            }

            for(Particle p : swarm){
                p.updateParticle(best,gradientWeight);
            }

            System.out.println(t);
        }
        return globalBest;
    }
}
