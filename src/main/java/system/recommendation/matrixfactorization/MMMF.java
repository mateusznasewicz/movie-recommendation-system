package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class MMMF extends MatrixFactorization {
    private double[][] margin;
    private final double[] discrete_ratings = {1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};

    public MMMF(RatingService<User> userService, int features, double learningRate, double regularization){
        super(userService, features, learningRate, regularization);
        this.margin = new double[users.length][discrete_ratings.length];
        for(int i = 0; i< users.length; i++){
            for(int j = 0; j < discrete_ratings.length; j++){
                margin[i][j] = (j + 1) * 0.5;
            }
        }
    }

    @Override
    public double[][] getPredictedRatings() {
        double[][] predicted = new double[users.length][movies.length];
        for(int i = 0; i < users.length; i++){
            for(int j = 0; j < movies.length; j++){
                double p = vectorMultiplication(users[i], movies[j]);

                boolean changed = false;
                for(double rating: discrete_ratings){
                    if(p < rating){
                        p = rating;
                        changed = true;
                        break;
                    }
                }

                if(!changed){
                    p = discrete_ratings[discrete_ratings.length-1];
                }

                predicted[i][j] = p;
            }
        }
        return predicted;
    }

    @Override
    protected void sgd_step() {
        double[][] hk = new double[users.length][movies.length];
        double[][] hkT = new double[movies.length][users.length];
        double[][] hj = new double[users.length][discrete_ratings.length];
        for(int u = 0; u< users.length; u++) {
            User user = userService.getEntity(u + 1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                for(int k = 0; k < discrete_ratings.length; k++){
                    double rating = entry.getValue();
                    double predictedRating = vectorMultiplication(users[u],movies[mid]);
                    int h = HingeLoss(k,rating,predictedRating,u,margin);
                    hk[u][mid] += h;
                    hkT[mid][u] += h;
                    hj[u][k] += h;
                }
            }
        }

        double[][] uc = users.clone();
        double[][] mc = movies.clone();
        double[][] x1 = multiplyMatrices(hk,mc);
        double[][] x2 = multiplyMatrices(hkT,uc);
        uc = multiplyMatrix(uc,this.regularization);
        uc = subMatrices(uc,x1);
        uc = multiplyMatrix(uc,this.learningRate);
        mc = multiplyMatrix(mc,this.regularization);
        mc = subMatrices(mc,x2);
        mc = multiplyMatrix(mc,this.learningRate);
        double[][] oc = multiplyMatrix(hj,this.learningRate);
        users = subMatrices(users,uc);
        movies = subMatrices(movies,mc);
        margin = subMatrices(margin,oc);


        //normalizacja?
        double lu = 0;
        double lm = 0;
        for(double[] user: users) {
            for (double f : user) {
                lu += Math.pow(f, 2);
            }
        }

        for(double[] movie: movies){
            for(double f: movie){
                lm += Math.pow(f,2);
            }
        }
        double unorm = Math.sqrt(lm / lu);
        double mnorm = Math.sqrt(lu / lm);

        users = multiplyMatrix(users,unorm);
        movies = multiplyMatrix(movies,mnorm);
        calcLoss();
    }

    private int HingeLoss(int k, double rating, double predictedRating, int u, double[][] threshold){
        if(discrete_ratings[k] >= rating && threshold[u][k] <= predictedRating + 1) return -1;
        if(discrete_ratings[k] <= rating && threshold[u][k] >= predictedRating - 1) return 1;
        return 0;
    }

    private void calcLoss(){
        double loss = 0;
        for(int i=0; i<users.length; i++){
            User u = userService.getEntity(i+1);
            Map<Integer, Double> ratings = u.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                double rating = entry.getValue();
                int mid = entry.getKey() - 1;
                double predictedRating = vectorMultiplication(users[i],movies[mid]);
                for(int k = 0;  k < discrete_ratings.length; k++){
                    int t = (discrete_ratings[k] >= rating) ? 1 : -1;
                    loss += Math.max(0,1-(t*(margin[i][k]-predictedRating)));
                }
            }
        }
        System.out.println(loss);
    }
}
