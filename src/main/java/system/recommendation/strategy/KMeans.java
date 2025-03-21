package system.recommendation.strategy;


import system.recommendation.models.Entity;
import system.recommendation.particleswarm.Particle;
import system.recommendation.service.RatingService;
import system.recommendation.similarity.Similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@SuppressWarnings("unchecked")
public class KMeans<T extends Entity, G extends Entity> extends Clustering<T,G> implements Particle {
    private List<Set<Integer>> membership;
    private double[][] v;
    private KMeans<T,G> local;
    double loss = Double.MAX_VALUE;

    //do przewidywania
    public KMeans(int k,RatingService<T, G> ratingService, Similarity<T> simFunction) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(ratingService, simFunction, k);
        initCentroids();
        assignMembership();
    }

    //po PSO, dodatkowym argumentem serwis z najlepszej cząsteczki
    public KMeans(RatingService<T,G> bestService, Similarity<T> simFunction, int k, RatingService<T,G> orgService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(bestService,orgService, simFunction, k);
        System.out.println(centroids.size());
    }

    //nowe w pso
    public KMeans(int k,RatingService<T, G> ratingService) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);
        initCentroids();
        assignMembership();

        this.v = new double[k][ratingService.getItemMap().size()];
        this.local = (KMeans<T, G>) copyParticle();
        this.loss = calcLoss();
    }

    //kopiowanie w pso
    public KMeans(List<Set<Integer>> membership, List<T> centroids, int k, RatingService<T, G> ratingService, double loss) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super(k,ratingService);
        this.loss = loss;
        this.membership = new ArrayList<>();
        for(Set<Integer> members: membership){
            this.membership.add(new HashSet<>(members));
        }
        Class<T> clazz = (Class<T>) centroids.getFirst().getClass();
        for(T centroid: centroids){
            T c = clazz.getConstructor(int.class,double.class,HashMap.class).newInstance(centroid.getId(),centroid.getAvgRating(),centroid.getRatings());
            this.centroids.add(c);
        }
    }

    public double getRatingCentroid(int c, int i){
        return centroids.get(c).getRating(i);
    }

    private void calculateCenter(int c){
        Set<Integer> members = membership.get(c);
        T centroid = centroids.get(c);
        centroid.clear();

        int s = ratingService.getItemMap().size();
        for(int itemID = 1; itemID < s+1; itemID++){
            int n = 0;
            double rating = 0;
            for(int memberID: members){
                if(ratingService.isRatedById(memberID, itemID)){
                    rating += ratingService.getRating(memberID, itemID);
                    n++;
                }
            }
            if(n != 0){
                centroid.addRating(itemID, rating/n);
            }
        }
    }

    private void initMembership(){
        membership = new ArrayList<>();
        for(int c = 0; c < centroids.size(); c++){
            membership.add(new HashSet<>());
        }
    }

    @Override
    public double calcLoss(){
        double loss = 0;
        for(int c = 0; c < centroids.size(); c++){
            T centroid = centroids.get(c);
            for(Integer u: membership.get(c)){
                T entity = ratingService.getEntity(u);
                loss += distFunction.calculate(entity,centroid);
            }
        }
        return loss;
    }

    @Override
    protected void step(){
        assignMembership();
        System.out.println(calcLoss());
        for(int c = 0; c < centroids.size(); c++){
            calculateCenter(c);
        }
    }

    public void assignMembership(){
        initMembership();

        for(int i : ratingService.getEntitiesID()){
            if(i < 0)continue;

            double bestDist = Double.MAX_VALUE;
            int closestID = 0;
            T entity = ratingService.getEntity(i);

            for (int c = 0; c < centroids.size() ; c++) {
                double dist = distFunction.calculate(entity, centroids.get(c));
                if (dist < bestDist) {
                    closestID = c;
                    bestDist = dist;
                }
            }
            membership.get(closestID).add(i);
        }
    }

    @Override
    public List<Integer> getNeighbors(T item) {
        for(Set<Integer> cluster: membership){
            if(cluster.contains(item.getId())){
                return cluster.stream().toList();
            }
        }
        return null;
    }

    public void updateVelocity(Particle g){
        double r1 = rand.nextDouble();
        double r2 = rand.nextDouble();
        double c1 = 1.42;
        double c2 = 1.42;
        double w = 0.72;

        KMeans<T,G> global = (KMeans <T,G>) g;

        for(int c = 0; c < v.length; c++){
            for(int i = 0; i < v[c].length; i++){
                double s1 = local.getRatingCentroid(c,i+1) - getRatingCentroid(c,i+1);
                double s2 = global.getRatingCentroid(c,i+1) - getRatingCentroid(c,i+1);
                v[c][i] = v[c][i]*w + c1*r1*s1 + c2*r2*s2;
            }
        }
    }

    @Override
    public Particle copyParticle(){
        try {
            return new KMeans<>(membership, centroids, k, ratingService, loss);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateParticle(Particle bestParticle, double gradientWeight) {
        assignMembership();
        updateVelocity(bestParticle);

        for(int c = 0; c < v.length; c++){
            for(int i = 0; i < v[c].length; i++){
                double r = getRatingCentroid(c,i+1);
                centroids.get(c).setRating(i+1,v[c][i]+r);
            }
        }

        double old = this.loss;
        this.loss = calcLoss();
        if(old > this.loss){
            this.local = (KMeans<T, G>) copyParticle();
        }
    }

    @Override
    public double getLoss() {
        return loss;
    }
}
