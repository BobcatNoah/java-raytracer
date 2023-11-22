public class Camera {
    public double aspectRatio = 1.0;
    public int imageWidth = 100;
    private int imageHeight;
    private Vec3 center;
    private Vec3 pixel00Loc;
    private Vec3 pixelDeltaU;
    private Vec3 pixelDeltaV;

    public int getImageHeight() {
        return imageHeight;
    }

    public Vec3 getCenter() {
        return center;
    }

    public Vec3 getPixel00Loc() {
        return pixel00Loc;
    }

    public Vec3 getPixelDeltaU() {
        return pixelDeltaU;
    }

    public Vec3 getPixelDeltaV() {
        return pixelDeltaV;
    }
    

    public void multiThreadedRender(final HittableList world, int threads) {
        initialize();
        Thread[] jobs = new Thread[threads];
        MultiThreadedRender[] chunks = new MultiThreadedRender[threads];
        for (int i = 0; i < threads - 1; i++) {
            HittableList worldCopy = world.createCopy();
            Interval currentRows = new Interval(i * imageHeight / threads, (i + 1) * imageHeight / threads);
            MultiThreadedRender chunk = new MultiThreadedRender(worldCopy, currentRows, pixel00Loc, pixelDeltaU, pixelDeltaV, imageWidth);
            Thread thread = new Thread(chunk, "thread");
            chunks[i] = chunk;
            jobs[i] = thread;
            thread.start();
        }
        
        HittableList worldCopy = world.createCopy();
        MultiThreadedRender chunk = new MultiThreadedRender(worldCopy, new Interval((threads - 1) * imageHeight / threads, imageHeight), pixel00Loc, pixelDeltaU, pixelDeltaV, imageWidth);
        Thread thread = new Thread(chunk, "finalthread");
        jobs[threads - 1] = thread;
        chunks[threads - 1] = chunk;
        thread.start();

        System.out.print("P3\n" + imageWidth + ' ' + imageHeight + "\n255\n");

        // Wait for threads to finish
        for (int i = 0; i < threads; i++) {
            try {
                jobs[i].join();
                System.out.print(chunks[i].getOutput());
                System.err.print("\rChunk: " + i + " Done.  ");
            } catch (InterruptedException e) {
                // TODO: handle exception
            }
        }
    }

    public void render(final HittableList world) {
        initialize();

        System.out.print("P3\n" + imageWidth + ' ' + imageHeight + "\n255\n");
        StringBuilder scanLineBuilder = new StringBuilder();

        for (int j = 0; j < imageHeight; j++) {
            scanLineBuilder.setLength(0);
            System.err.print("\rScanlines remaining: " + (imageHeight - j) + ' ');
            System.err.flush();
            for (int i = 0; i < imageWidth; i++) {
                Vec3 pixel_center = pixel00Loc
                .plus(
                    pixelDeltaU.multiply(i)
                ).plus(
                    pixelDeltaV.multiply(j)  
                );
                Vec3 ray_direction = pixel_center.minus(center);
                Ray r = new Ray(center, ray_direction);

                Vec3 pixel_color = rayColor(r, world);
                scanLineBuilder.append(String.format("%d %d %d ", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z())));
            }
            System.out.print(scanLineBuilder.toString() + "\n");
        }

        System.err.print("\rDone.                           \n");

    }

    private void initialize() {
        imageHeight = (int)(imageWidth / aspectRatio);
        imageHeight = (imageHeight < 1) ? 1 : imageHeight;

        center =  new Vec3(0, 0, 0);

        double focalLength  = 2;
        double viewportHeight = 2.0;
        double viewportWidth = viewportHeight * ((double)imageWidth / imageHeight);

        // Calculate the vectors across the horizontal and down the vertical viewport edges.
        Vec3 viewportU = new Vec3(viewportWidth, 0, 0);
        Vec3 viewportV = new Vec3(0, -viewportHeight, 0);

        // Calculate the horizontal and vertical delta vectors from pixel to pixel.
        pixelDeltaU = viewportU.divideBy(imageWidth);
        pixelDeltaV = viewportV.divideBy(imageHeight);

        // Calculate the location of the upper left pixel.
        Vec3 viewportUpperLeft = center
        .minus(
            new Vec3(0, 0, focalLength)
        ).minus(
            viewportU.divideBy(2)
        ).minus(
            viewportV.divideBy(2)
        );
        pixel00Loc = (pixelDeltaU.plus(pixelDeltaV)).multiply(0.5).plus(viewportUpperLeft);

    }

    private Vec3 rayColor(final Ray r, final HittableList world) {
        HitRecord rec = new HitRecord();
        if (world.hit(r, new Interval(0 , RTWeekend.infinity), rec)) {
            rec = world.getLatestHitRecord();
            return rec.normal.plus(new Vec3(1,1,1)).multiply(0.5);
        }
        

        Vec3 unit_direction = Vec3.unit_vector(r.direction());
        double a = 0.5 * (unit_direction.y() + 1.0);
        return new Vec3(1.0,1.0,1.0).multiply(1.0 - a).plus(new Vec3(0.5, 0.7, 1.0).multiply(a));
    }
}
