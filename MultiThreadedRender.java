public class MultiThreadedRender implements Runnable {
    private StringBuilder output = new StringBuilder();
    public Interval scanLinesToBeRendered = new Interval(0,0);

    private HittableList world;
    private Vec3 pixel00Loc;
    private Vec3 pixelDeltaU;
    private Vec3 pixelDeltaV;
    private int imageWidth = 100;
    private Vec3 center = new Vec3(0,0,0);
    private int samplesPerPixel = 1;
    private long totalTime = 0;

    public MultiThreadedRender(final HittableList world, final Interval pixels, Vec3 pixel00Loc, Vec3 pixelDeltaU, Vec3 pixelDeltaV, int imageWidth, Vec3 center, int samples) {
        this.world = world;
        this.scanLinesToBeRendered = pixels;
        this.pixel00Loc = pixel00Loc;
        this.pixelDeltaU = pixelDeltaU;
        this.pixelDeltaV = pixelDeltaV;
        this.imageWidth = imageWidth;
        this.center = center;
        this.samplesPerPixel = samples;
    }

    public String getOutput() {
        return output.toString();
    }

    public long getTime() {
        return totalTime;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        int startRow = (int)scanLinesToBeRendered.min;
        int endRow = (int)scanLinesToBeRendered.max;
        Vec3 pixelColor = new Vec3(0,0,0);

        for (int j = startRow; j < endRow; j++) {
            //System.err.print("\rScanlines remaining: " + (imageHeight - j) + ' ');
            //System.err.flush();
            for (int i = 0; i < imageWidth; i++) {
               pixelColor.set(0, 0, 0);
                for (int sample = 0; sample < samplesPerPixel; sample++) {
                    Ray r = getRay(i, j);
                    pixelColor.plusEquals(Camera.rayColor(r, world));
                }
                output.append(Color.getColor(pixelColor, samplesPerPixel));
            }

        }
        long end = System.currentTimeMillis();
        totalTime = end - start;
    }

    private Ray getRay(int i, int j) {
        // Get a randomly sampled camera ray for the pixel at location i,j.
        Vec3 pixelCenter = pixel00Loc
                .plus(
                    pixelDeltaU.multiply(i)
                ).plus(
                    pixelDeltaV.multiply(j)  
                );
        Vec3 pixelSample = pixelSampleSquare().plus(pixelCenter);

        Vec3 rayOrigin = center;
        Vec3 rayDirection = pixelSample.minus(rayOrigin);

        return new Ray(rayOrigin, rayDirection);
    }

    private Vec3 pixelSampleSquare() {
        // Returns a random point in the square surrounding a pixel at the origin.
        double px = -0.5 * Math.random();
        double py = -0.5 * Math.random();
        return pixelDeltaU.multiply(px).plus(pixelDeltaV.multiply(py));
    }
}


