package system.recommendation.particleswarm;

import java.util.ArrayList;
import java.util.List;

public class ParticleSwarm{
    private ParticleProvider particleProvider;
    private List<Particle> swarm = new ArrayList<>();
    private double gradientWeight;

    public ParticleSwarm(ParticleProvider pp, int swarmSize, double gradientWeight){
        this.particleProvider = pp;
        this.gradientWeight = gradientWeight;
        for(int i = 0; i < swarmSize; i++){
            swarm.add(particleProvider.initParticle());
        }
    }

    public Particle run(){
        Particle best = null;
        for(int t = 0; t < 50; t++){
            double bestLoss = Double.MAX_VALUE;

            for(int i = 0; i < swarm.size(); i++){
                Particle p = swarm.get(i);
                double loss = p.getLoss();
                if(loss < bestLoss){
                    best = p;
                    bestLoss = loss;
                }
            }

            for(Particle p : swarm){
                p.updateParticle(best,gradientWeight);
            }

            System.out.println(t);
        }
        return best;
    }
}
