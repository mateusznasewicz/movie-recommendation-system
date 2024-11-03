package system.recommendation.matrixfactorization;

import system.recommendation.models.User;
import system.recommendation.service.RatingService;

import java.util.Map;

public class MMMF extends MatrixFactorization {
    private final double[][] margin;
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
    protected void sgd_step() {
        for(int u = 0; u < users.length; u++)
        {
            User user = userService.getEntity(u+1);
            Map<Integer, Double> ratings = user.getRatings();
            double[] uf = this.users[u];
            for(Map.Entry<Integer, Double> entry : ratings.entrySet())
            {
                int mid = entry.getKey();
                double[] mf = this.movies[mid - 1];
                double rating = entry.getValue();
                double predictedRating = vectorMultiplication(uf,mf);
                for(int k = 0; k < R - 1; k++)
                {
                    int h = H(rating,predictedRating,u+1,k);
                    if(h == 0) continue;

                    this.margin[u][k] -= this.learningRate*h;
                    for(int f = 0; f < uf.length; f++)
                    {
                        double du = 2*this.regularization*uf[f] - h*mf[f];
                        double dm = 2*this.regularization*mf[f] - h*uf[f];
                        uf[f] -= this.learningRate*du;
                        mf[f] -= this.learningRate*dm;
                    }
                }
            }
        }
    }

    private void printMargin(){
        double[] um = margin[0];
        for(double m: um){
            System.out.print(m + " ");
        }
        System.out.println();
    }

    private int H(double rating, double predictedRating, int uid, int k){
        if(k+1 >= rating && margin[uid-1][k] <= predictedRating + 1) return -1;
        if(k+1 <= rating && margin[uid-1][k] >= predictedRating - 1) return 1;
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
