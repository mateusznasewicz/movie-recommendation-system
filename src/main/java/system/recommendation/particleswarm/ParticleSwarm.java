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
        int t = 0;
        int prevBest = -1;
        int withoutChange = 0;
        Particle best = null;
        while(t < 50){
            double bestLoss = Double.MAX_VALUE;
            int bestID = -1;

            for(int i = 0; i < swarm.size(); i++){
                Particle p = swarm.get(i);
                double loss = p.getLoss();
                if(loss < bestLoss){
                    best = p;
                    bestLoss = loss;
                    bestID = i;
                }
                i++;
            }

//            if(withoutChange > 10){
//                return best;
//            }

            if(prevBest != -1 && prevBest == bestID){
                withoutChange++;
            }else{
                withoutChange = 0;
            }

            for(Particle p : swarm){
                p.updateParticle(best,gradientWeight);
            }

            prevBest = bestID;
            t++;
            System.out.println(t);
        }
        return best;
    }
}
