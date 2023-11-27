public class Color {
    public static String getColor(Vec3 pixelColor, int samplesPerPixel) {
        double r = pixelColor.x();
        double g = pixelColor.y();
        double b = pixelColor.z();
        
        // Divide the color by the number of samples.
        r /= samplesPerPixel;
        g /= samplesPerPixel;
        b /= samplesPerPixel;

        r = linearToGamma(r);
        g = linearToGamma(g);
        b = linearToGamma(b);

        // Write the translated [0,255] value of each color component.
        final Interval intensity = new Interval(0.000, 0.999);
        return String.format("%d %d %d\n", (int)(256 * intensity.clamp(r)), (int)(256 * intensity.clamp(g)), (int)(256 * intensity.clamp(b)));
    }

    public static double linearToGamma(double linearComponent) {
        return Math.sqrt(linearComponent);
    }

}
