package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class MMMF extends MatrixFactorization {
    private double[][] margin;
    private final int R = 9;

    public MMMF(RatingService<User> userService, int features, double learningRate, double regularization){
        super(userService, features, learningRate, regularization);
        this.margin = new double[users.length][R-1];
        for(int i = 0; i< users.length; i++){
            for(int j = 0; j < R-1; j++){
                margin[i][j] = (j + 1) * 0.5;
            }
        }
    }

    @Override
    //tak naprawde to GD bo SGD nie dziala mi cos XD
    //GD tez nie dziala
    protected void sgd_step() {
        double[][] hk = new double[users.length][movies.length];
        double[][] hkT = new double[movies.length][users.length];
        double[][] hj = new double[users.length][R-1];

        for(int u = 0; u< users.length; u++){
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet()){
                int mid = entry.getKey() - 1;
                for(int k = 0; k < R-1; k++){
                    double rating = entry.getValue();
                    double predictedRating = vectorMultiplication(users[u],movies[mid]);
                    int h = HingeLoss(rating,predictedRating,u,k);
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

        for(double[] user: users){
            for(double f: user){
                lu += Math.pow(f,2);
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
    }

    private int HingeLoss(double rating, double predictedRating, int uid, int k){
        if(k+1 >= rating && margin[uid][k] <= predictedRating + 1) return -1;
        if(k+1 <= rating && margin[uid][k] >= predictedRating - 1) return 1;
        return 0;
    }

    protected void calcLoss(){
        double regLoss = 0.0;
        double hingeLoss = 0.0;

        for (double[] u : users) {
            for (double val : u) {
                regLoss += val * val;
            }
        }

        for (double[] m : movies) {
            for (double val : m) {
                regLoss += val * val;
            }
        }

        for(int u = 0; u < users.length; u++)
        {
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            for(Map.Entry<Integer, Double> entry : ratings.entrySet())
            {
                for(int k = 0; k < R-1; k++){
                    double predicted = vectorMultiplication(users[u],movies[entry.getKey()-1]);
                    hingeLoss += Math.max(0,1-(margin[u][k]-predicted));
                }
            }
        }

        System.out.println("Reg Loss :" +regLoss*regularization + " Hinge Loss: " + hingeLoss);
    }

}
