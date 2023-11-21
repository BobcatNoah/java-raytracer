class Main {
    private static Vec3 ray_color(final Ray r, final HittableList world) {
        HitRecord rec = new HitRecord();
        if (world.hit(r, 0, RTWeekend.infinity, rec)) {
            rec = world.getLatestHitRecord();
            return rec.normal.plus(new Vec3(1,1,1)).multiply(0.5);
        }
        

        Vec3 unit_direction = Vec3.unit_vector(r.direction());
        double a = 0.5 * (unit_direction.y() + 1.0);
        return new Vec3(1.0,1.0,1.0).multiply(1.0 - a).plus(new Vec3(0.5, 0.7, 1.0).multiply(a));
    }

    public static void main(String[] args) {
        double aspect_ratio = 16.0 / 9.0;
        int image_width = 1280;
        int image_height = (int)(image_width / aspect_ratio);
        image_height = (image_height < 1) ? 1 : image_height;

        // World
        HittableList world = new HittableList();

        world.add(new Sphere(new Vec3(0,0,-2), 0.5));
        world.add(new Sphere(new Vec3(0, -100.5,-1), 100));

        world.add(new Sphere(new Vec3(-0.5, -0.25, -1.75), 0.25));
        world.add(new Sphere(new Vec3(3, -0.25, -4), 0.25));

        // Camera
        // Viewport widths less than one are ok since they are real valued.
        double focal_length  = 2;
        double viewport_height = 2.0;
        double viewport_width = viewport_height * ((double)image_width / image_height);
        Vec3 camera_center = new Vec3(0,0,0);

        // Calculate the vectors across the horizontal and down the vertical viewport edges.
        Vec3 viewport_u = new Vec3(viewport_width, 0, 0);
        Vec3 viewport_v = new Vec3(0, -viewport_height, 0);

        // Calculate the horizontal and vertical delta vectors from pixel to pixel.
        Vec3 pixel_delta_u = viewport_u.divideBy(image_width);
        Vec3 pixel_delta_v = viewport_v.divideBy(image_height);

        // Calculate the location of the upper left pixel.
        Vec3 viewport_upper_left = camera_center
        .minus(
            new Vec3(0, 0, focal_length)
        ).minus(
            viewport_u.divideBy(2)
        ).minus(
            viewport_v.divideBy(2)
        );
        Vec3 pixel00_loc = (pixel_delta_u.plus(pixel_delta_v)).multiply(0.5).plus(viewport_upper_left);

        

        System.out.print("P3\n" + image_width + ' ' + image_height + "\n255\n");
        StringBuilder scanLineBuilder = new StringBuilder();

        for (int j = 0; j < image_height; j++) {
            //String scanLine = "";
            scanLineBuilder.setLength(0);
            System.err.print("\rScanlines remaining: " + (image_height - j) + ' ');
            System.err.flush();
            for (int i = 0; i < image_width; i++) {
                Vec3 pixel_center = pixel00_loc
                .plus(
                    pixel_delta_u.multiply(i)
                ).plus(
                    pixel_delta_v.multiply(j)  
                );
                Vec3 ray_direction = pixel_center.minus(camera_center);
                Ray r = new Ray(camera_center, ray_direction);

                Vec3 pixel_color = ray_color(r, world);
                //Color.write_color(pixel_color);
                //scanLine += String.format("%d %d %d ", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z()));
                scanLineBuilder.append(String.format("%d %d %d ", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z())));
            }
            //System.out.print(scanLine + "\n");
            System.out.print(scanLineBuilder.toString() + "\n");
        }

        System.err.print("\rDone.                           \n");
    }
}