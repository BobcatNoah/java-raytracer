public class Camera {
    public double aspectRatio = 1.0;
    public int imageWidth = 100;
    public int samplesPerPixel = 10;
    public int maxDepth = 10;

    public double vfov = 90;                        // Vertical view angle (field of view)
    public Vec3 lookFrom =  new Vec3(0,0,-1); // Point camera is looking from
    public Vec3 lookAt = new Vec3(0,0,0);  // Point camera is looking at
    public Vec3 vup = new Vec3(0,1,0);     // Camera-relative "up" direction
    private Vec3 u, v, w;        // Camera frame basis vectors

    private int imageHeight;
    private Vec3 center;
    private Vec3 pixel00Loc;
    private Vec3 pixelDeltaU;
    private Vec3 pixelDeltaV;

    public void multiThreadedRender(final HittableList world, int threads) {
        initialize();
        
        System.out.print("P3\n" + imageWidth + ' ' + imageHeight + "\n255\n");
        Vec3[] finalImage = new Vec3[imageWidth * imageHeight];
        for (int i = 0; i < finalImage.length; i++) {
            finalImage[i] = new Vec3();
        }
        Vec3 pixelColor = new Vec3(0,0,0);

        for (int thread = 0; thread < threads; thread++) {
            for (int j = 0; j < imageHeight; j++) {
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
                    finalImage[j*imageWidth + i] = finalImage[j*imageWidth + i].plus(pixelColor).divideBy(2);
                }
            }
            System.err.print("\rDone.                           \n");
        }
        for (int i = 0; i < finalImage.length; i++) {
            System.out.print(Color.getColor(finalImage[i], samplesPerPixel));
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

        double focalLength  = lookFrom.minus(lookAt).length();
        double theta = RTWeekend.degreesToRadians(vfov);
        double h = Math.tan(theta/2);
        double viewportHeight = 2 * h * focalLength;
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
            w.multiply(focalLength)
        ).minus(
            viewportU.divideBy(2)
        ).minus(
            viewportV.divideBy(2)
        );
        pixel00Loc = (pixelDeltaU.plus(pixelDeltaV)).multiply(0.5).plus(viewportUpperLeft);

    }

    public static Vec3 rayColor(final Ray r, int depth, final HittableList world) {
        HitRecord rec = new HitRecord();

        if (depth <= 0) {
            return new Vec3(0,0,0);
        }

        if (world.hit(r, new Interval(0.001 , RTWeekend.infinity), rec)) {
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
        // Get a randomly sampled camera ray for the pixel at location i,j.
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
