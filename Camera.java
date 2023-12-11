import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


public class Camera {
    public volatile double aspectRatio = 1.0;
    public volatile int imageWidth = 100;
    public volatile int samplesPerPixel = 10;
    public volatile int maxDepth = 10;

    public volatile double vfov = 90;                        // Vertical view angle (field of view)
    public volatile Vec3 lookFrom =  new Vec3(0,0,-1); // Point camera is looking from
    public volatile Vec3 lookAt = new Vec3(0,0,0);  // Point camera is looking at
    public volatile Vec3 vup = new Vec3(0,1,0);     // Camera-relative "up" direction
    private volatile Vec3 u, v, w;        // Camera frame basis vectors

    private volatile int imageHeight;
    private volatile Vec3 center;
    private volatile Vec3 pixel00Loc;
    private volatile Vec3 pixelDeltaU;
    private volatile Vec3 pixelDeltaV;
    private volatile Vec3 defocusDiskU;  // Defocus disk horizontal radius
    private volatile Vec3 defocusDiskV;  // Defocus disk vertical radius

    public volatile double defocusAngle = 0;  // Variation angle of rays through each pixel
    public volatile double focusDist = 10;    // Distance from camera lookfrom point to plane of perfect focus

    public void multiThreadedRender(final HittableList world, int threads) {
        initialize();
        samplesPerPixel /= threads;
        
        System.out.print("P3\n" + imageWidth + ' ' + imageHeight + "\n255\n");
        Vec3[] finalImage = new Vec3[imageWidth * imageHeight];
        for (int i = 0; i < finalImage.length; i++) {
            finalImage[i] = new Vec3();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        List<Future<Vec3[]>> futures = new ArrayList<>();

        for (int thread = 0; thread < threads; thread++) {
            HittableList worldCopy = world.createCopy();
            Callable<Vec3[]> renderTask = () -> {
                long start = System.currentTimeMillis();
                Vec3[] partialImage = new Vec3[imageWidth * imageHeight];

                for (int j = 0; j < imageHeight; j++) {
                    System.err.print("\rScanlines remaining: " + (imageHeight - j) + ' ');
                    System.err.flush();
                    for (int i = 0; i < imageWidth; i++) {
                        Vec3 pixelColor = new Vec3(0,0,0);
                        Vec3 pixelCenter = pixel00Loc
                        .plus(
                            pixelDeltaU.multiply(i)
                        ).plus(
                            pixelDeltaV.multiply(j)  
                        );
                        for (int sample = 0; sample < samplesPerPixel; sample++) {
                            Ray r = getRay(i, j, pixelCenter);
                            pixelColor.plusEquals(rayColor(r, maxDepth, worldCopy));
                        }
                        partialImage[j*imageWidth+i] = pixelColor;
                    }
                }
                System.err.println("Render time (ms): " + (System.currentTimeMillis() - start));
                return partialImage;
            };  
            futures.add(executorService.submit(renderTask));            
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle interruption
            e.printStackTrace();
        }

         for (Future<Vec3[]> future : futures) {
            try {
                Vec3[] partialImage = future.get();
                for (int i = 0; i < finalImage.length; i++) {
                    finalImage[i].plusEquals(partialImage[i]);
                }
            } catch (InterruptedException | ExecutionException e ) {
                // Handle exceptions
                e.printStackTrace();
            }
        }

        for (int i = 0; i < finalImage.length; i++) {
            System.out.print(Color.getColor(finalImage[i], samplesPerPixel * threads));
        }
    }

    public void render(final HittableList world) {
        initialize();

        System.out.print("P3\n" + imageWidth + ' ' + imageHeight + "\n255\n");
        StringBuilder scanLineBuilder = new StringBuilder();
        Vec3 pixelColor = new Vec3(0,0,0);

        for (int j = 0; j < imageHeight; j++) {
            scanLineBuilder.setLength(0);
            System.err.print("\rScanlines remaining: " + (imageHeight - j) + ' ');
            System.err.flush();
            for (int i = 0; i < imageWidth; i++) {
                pixelColor.set(0, 0, 0);
                Vec3 pixelCenter = pixel00Loc
                .plus(
                    pixelDeltaU.multiply(i)
                ).plus(
                    pixelDeltaV.multiply(j)  
                );
                for (int sample = 0; sample < samplesPerPixel; sample++) {
                    Ray r = getRay(i, j, pixelCenter);
                    pixelColor.plusEquals(rayColor(r, maxDepth, world));
                }
                scanLineBuilder.append(Color.getColor(pixelColor, samplesPerPixel));
            }
            System.out.print(scanLineBuilder.toString());
        }

        System.err.print("\rDone.                           \n");

    }

    private void initialize() {
        imageHeight = (int)(imageWidth / aspectRatio);
        imageHeight = (imageHeight < 1) ? 1 : imageHeight;

        center =  lookFrom;

        double theta = RTWeekend.degreesToRadians(vfov);
        double h = Math.tan(theta/2);
        double viewportHeight = 2 * h * focusDist;
        double viewportWidth = viewportHeight * ((double)imageWidth / imageHeight);

        // Calculate the u,v,w unit basis vectors for the camera coordinate frame.
        w = Vec3.unit_vector(lookFrom.minus(lookAt));
        u = Vec3.unit_vector(Vec3.cross(vup, w));
        v = Vec3.cross(w, u);

        // Calculate the vectors across the horizontal and down the vertical viewport edges.
        Vec3 viewportU = u.multiply(viewportWidth);             // Vector across viewport horizontal edge
        Vec3 viewportV = v.multiply(viewportHeight).negate();   // Vector down viewport vertical edge

        // Calculate the horizontal and vertical delta vectors from pixel to pixel.
        pixelDeltaU = viewportU.divideBy(imageWidth);
        pixelDeltaV = viewportV.divideBy(imageHeight);

        // Calculate the location of the upper left pixel.
        Vec3 viewportUpperLeft = center.minus(
            w.multiply(focusDist)
        ).minus(
            viewportU.divideBy(2)
        ).minus(
            viewportV.divideBy(2)
        );
        pixel00Loc = (pixelDeltaU.plus(pixelDeltaV)).multiply(0.5).plus(viewportUpperLeft);

        // Calculate the camera defocus disk basis vectors.
        double defocusRadius = focusDist * Math.tan(Math.toRadians(defocusAngle / 2));
        defocusDiskU = u.multiply(defocusRadius);
        defocusDiskV = v.multiply(defocusRadius);
    }

    public static Vec3 rayColor(final Ray r, int depth, final HittableList world) {
        HitRecord rec = new HitRecord();

        if (depth <= 0) {
            return new Vec3(0,0,0);
        }

        if (world.hit(r, new Interval(0.000001 , RTWeekend.infinity), rec)) {
            rec = world.getLatestHitRecord();
            Ray scattered = new Ray();
            Vec3 attenuation = new Vec3();
            if (rec.mat.scatter(r, rec, attenuation, scattered)) {
                scattered = rec.mat.getScattered();
                attenuation = rec.mat.getAttenuation();
                return attenuation.multiply(rayColor(scattered, depth - 1, world));
            }
            return new Vec3(0,0,0);
        }
        

        Vec3 unit_direction = Vec3.unit_vector(r.direction());
        double a = 0.5 * (unit_direction.y() + 1.0);
        return new Vec3(1.0,1.0,1.0).multiply(1.0 - a).plus(new Vec3(0.5, 0.7, 1.0).multiply(a));
    }

    private Ray getRay(int i, int j, Vec3 pixelCenter) {
        // Get a randomly-sampled camera ray for the pixel at location i,j, originating from
        // the camera defocus disk.
        Vec3 pixelSample = pixelSampleSquare().plus(pixelCenter);

        Vec3 rayOrigin = (defocusAngle <= 0) ? center : defocusDiskSample();
        Vec3 rayDirection = pixelSample.minus(rayOrigin);

        return new Ray(rayOrigin, rayDirection);
    }

    Vec3 defocusDiskSample() {
        // Returns a random point in the camera defocus disk.
        Vec3 p = Vec3.randomInUnitDisk();
        return center.plus(
            defocusDiskU.multiply(p.x())
        ).plus(
            defocusDiskV.multiply(p.y())
        );
    }

    private Vec3 pixelSampleSquare() {
        // Returns a random point in the square surrounding a pixel at the origin.
        double px = -0.5 * ThreadLocalRandom.current().nextDouble();
        double py = -0.5 * ThreadLocalRandom.current().nextDouble();
        return pixelDeltaU.multiply(px).plus(pixelDeltaV.multiply(py));
    }
}
