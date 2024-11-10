package system.recommendation.particleswarm;

public interface Particle {
    Particle copyParticle();
    void updateParticle(Particle bestParticle, double gradientWeight);
    double getLoss();

    default void moveParticleTowardsSwarm(double[][] best, double[][] particle, double[][] particleFuture, double weight){
        for(int i = 0; i<particle.length; i++){
            for(int f = 0; f < particle[0].length; f++){
//                System.out.println(weight*(best[i][f] - particle[i][f]));
                particleFuture[i][f] += weight*(best[i][f] - particle[i][f]);
            }
        }
    }
}
