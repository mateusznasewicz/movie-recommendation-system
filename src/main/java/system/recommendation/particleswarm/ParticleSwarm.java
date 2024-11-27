package system.recommendation.particleswarm;

import system.recommendation.QualityMeasure;
import system.recommendation.matrixfactorization.RMF;
import system.recommendation.models.Movie;
import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Particle run(int epochs, double[][] mae, double[][] rmse, int i, RatingService<User, Movie> userService){
        Particle globalBest = null;
        double globalLoss = Double.MAX_VALUE;

        for(int t = 0; t < epochs; t++)
        {
            int bestID = findBest(globalLoss);

            if(bestID != -1){
                globalBest = swarm.get(bestID).copyParticle();
                globalLoss = globalBest.getLoss();
            }

            RMF b = (RMF) globalBest;
            double[][] ratings = b.getPredictedRatings();
            double[] result = new double[]{QualityMeasure.MAE(ratings,userService,false),QualityMeasure.RMSE(ratings,userService)};
            mae[i][t] = result[0];
            rmse[i][t] = result[1];

            for(Particle p : swarm){
                p.updateParticle(globalBest,gradientWeight);
            }

//            System.out.println("Epoch " + t + "||"+globalLoss);
        }
        int id = findBest(globalLoss);
        return swarm.get(id);
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
            System.out.println("EPOCH:"+t);
        }
        int id = findBest(globalLoss);
        return swarm.get(id);
    }
}
