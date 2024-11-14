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

    private int findBest(double globalLoss){
        int bestID = -1;
        for(int i = 0; i < swarm.size(); i++){
            Particle p = swarm.get(i);
            double loss = p.getLoss();

            if(loss < globalLoss){
                bestID = i;
                globalLoss = loss;
            }
        }
        return bestID;
    }

    public Particle run(int epochs){
        Particle globalBest = null;
        double globalLoss = Double.MAX_VALUE;

        for(int t = 0; t < epochs; t++)
        {
            int bestID = findBest(globalLoss);

            if(bestID != -1){
                globalBest = swarm.get(bestID).copyParticle();
                globalLoss = globalBest.getLoss();
            }

            for(Particle p : swarm){
                p.updateParticle(globalBest,gradientWeight);
            }

            System.out.println("Epoch " + t + "||"+globalLoss);
        }
        int id = findBest(globalLoss);
        return swarm.get(id);
    }
}
