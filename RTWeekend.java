import java.util.concurrent.ThreadLocalRandom;

public class RTWeekend {
    public static final double infinity = Double.POSITIVE_INFINITY;
    public static final double pi = Math.PI;

    public static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    //public static double randomDouble(double min, double max) {
    //    return min + (max - min) * Math.random();
    //}

    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
    
}
