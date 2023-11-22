public class MultiThreadedRender implements Runnable {
    //private volatile String output = "";
    private StringBuilder output = new StringBuilder();
    public Interval scanLinesToBeRendered = new Interval(0,0);
    private HittableList world;
    private Vec3 pixel00Loc;
    private Vec3 pixelDeltaU;
    private Vec3 pixelDeltaV;
    private int imageWidth = 100;
    private Vec3 center = new Vec3(0,0,0);

    public MultiThreadedRender(final HittableList world, final Interval pixels, Vec3 pixel00Loc, Vec3 pixelDeltaU, Vec3 pixelDeltaV, int imageWidth) {
        this.world = world;
        this.scanLinesToBeRendered = pixels;
        this.pixel00Loc = pixel00Loc;
        this.pixelDeltaU = pixelDeltaU;
        this.pixelDeltaV = pixelDeltaV;
        this.imageWidth = imageWidth;
    }

    public String getOutput() {
        return output.toString();
    }

    @Override
    public void run() {
        int startRow = (int)scanLinesToBeRendered.min;
        int endRow = (int)scanLinesToBeRendered.max;

        for (int j = startRow; j < endRow; j++) {
            //System.err.print("\rScanlines remaining: " + (imageHeight - j) + ' ');
            //System.err.flush();
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
                //scanLine += String.format("%d %d %d ", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z()));
                output.append(String.format("%d %d %d\n", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z())));
            }

        }
        //System.err.print("\rDone.                           \n");
    }

    private Vec3 rayColor(final Ray r, final HittableList world) {
        HitRecord rec = new HitRecord();
        if (world.hit(r, new Interval(0 , Double.POSITIVE_INFINITY), rec)) {
            rec = world.getLatestHitRecord();
            return rec.normal.plus(new Vec3(1,1,1)).multiply(0.5);
        }
        

        Vec3 unit_direction = Vec3.unit_vector(r.direction());
        double a = 0.5 * (unit_direction.y() + 1.0);
        return new Vec3(1.0,1.0,1.0).multiply(1.0 - a).plus(new Vec3(0.5, 0.7, 1.0).multiply(a));
    }
}


