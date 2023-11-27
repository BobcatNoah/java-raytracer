class Main {
    public static void main(String[] args) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        // World
        HittableList world = new HittableList();

        world.add(new Sphere(new Vec3(0,0,-1), 0.5));
        //world.add(new Sphere(new Vec3(0, -100.5,-1), 100));

        //world.add(new Sphere(new Vec3(-0.5, -0.25, -1.75), 0.25));
        //world.add(new Sphere(new Vec3(2, -0.5, -3), 0.25));
        //world.add(new Sphere(new Vec3(1, -0.4, -2.25), 0.25));

        Camera cam = new Camera();

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 400;
        cam.samplesPerPixel = 100;

        long start = System.currentTimeMillis();
        cam.render(world);
        //cam.multiThreadedRender(world, 4);
        System.err.println("Total time: " + (System.currentTimeMillis() - start));
    }
}