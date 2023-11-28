class Main {
    public static void main(String[] args) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        // World
        HittableList world = new HittableList();

        // Create a lambertian material with color
        Lambertian materialGround = new Lambertian(new Vec3(0.8, 0.8, 0.0));
        Lambertian materialCenter = new Lambertian(new Vec3(0.1, 0.2, 0.5));
        Dielectric materialLeft = new Dielectric(1.5);
        Metal materialRight = new Metal(new Vec3(0.8, 0.6, 0.2), 0.0);

        world.add(new Sphere(new Vec3(0,0,-1), 0.5, materialCenter));
        world.add(new Sphere(new Vec3(0, -100.5,-1), 100, materialGround));
        world.add(new Sphere(new Vec3(-1,0,-1), 0.5, materialLeft));
        world.add(new Sphere(new Vec3(-1,0,-1), -0.4, materialLeft));
        world.add(new Sphere(new Vec3(1,0,-1), 0.5, materialRight));


        //world.add(new Sphere(new Vec3(0.75,-0.5,-1), -0.25, materialLeft));

        Camera cam = new Camera();

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 400;
        cam.samplesPerPixel = 100;
        cam.maxDepth = 50;

        cam.vfov = 20;
        cam.lookFrom = new Vec3(-2,2,1);
        cam.lookAt = new Vec3(0,0,-1);
        cam.vup = new Vec3(0,1,0);

        long start = System.currentTimeMillis();
        cam.render(world);
        //cam.multiThreadedRender(world, 4);
        System.err.println("Total time: " + (System.currentTimeMillis() - start));
    }
}