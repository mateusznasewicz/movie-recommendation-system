package system.recommendation;

public class Utils {
    public static double[][] deepCopy(double[][] arr){
        double[][] deepCopy = new double[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            deepCopy[i] = arr[i].clone();
        }
        return deepCopy;
    }
}
