public class RTWeekend {
    public static final double infinity = Double.POSITIVE_INFINITY;
    public static final double pi = Math.PI;

    public static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double randomDouble(double min, double max) {
        return min + (min-max) * Math.random();
    }
    
}
