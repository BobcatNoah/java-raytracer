class Main {
    public static void main(String[] args) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        // World
        HittableList world = new HittableList();

        // Create a lambertian material with color
        Lambertian materialGround = new Lambertian(new Vec3(0.8, 0.8, 0.0));
        Lambertian materialCenter = new Lambertian(new Vec3(0.7, 0.3, 0.3));
        Metal materialLeft = new Metal(new Vec3(0.8, 0.8, 0.8));
        Metal materialRight= new Metal(new Vec3(0.8, 0.6, 0.2));

        world.add(new Sphere(new Vec3(0,0,-1), 0.5, materialCenter));
        world.add(new Sphere(new Vec3(0, -100.5,-1), 100, materialGround));
        world.add(new Sphere(new Vec3(-1,0,-1), 0.5, materialLeft));
        world.add(new Sphere(new Vec3(1,0,-1), 0.5, materialRight));


        world.add(new Sphere(new Vec3(0.75,-0.5,-1), 0.25, materialCenter));

        Camera cam = new Camera();

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 400;
        cam.samplesPerPixel = 100;
        cam.maxDepth = 50;

        long start = System.currentTimeMillis();
        cam.render(world);
        //cam.multiThreadedRender(world, 4);
        System.err.println("Total time: " + (System.currentTimeMillis() - start));
    }
}