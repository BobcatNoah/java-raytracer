import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;

class Main {
    public static void main(String[] args) {
        int availableThreads = Runtime.getRuntime().availableProcessors();
        // World
        HittableList world = new HittableList();
        Lambertian groundMaterial = new Lambertian(new Vec3(0.5, 0.5, 0.5));
        world.add(new Sphere(new Vec3(0, -1000, 0), 1000, groundMaterial));

        for (int a = -8; a < 8; a++) {
            for (int b = -5; b < 5; b++) {
                double chooseMat = Math.random();
                Vec3 center = new Vec3(a + 0.9*Math.random(), 0.2 , b + 0.9*Math.random());
                if (center.minus(new Vec3(4, 0.2, 0)).length()  > 0.9) {
                    Material sphereMaterial;
                    if (chooseMat < 0.8) {
                        //diffuse
                        Vec3 albedo = Vec3.random().multiply(Vec3.random());
                        sphereMaterial = new Lambertian(albedo);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    } else if (chooseMat < 0.95) {
                        // metal
                        Vec3 albedo = Vec3.random(0.5, 1);
                        double fuzz = RTWeekend.randomDouble(0, 0.5);
                        sphereMaterial = new Metal(albedo, fuzz);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    } else {
                        // glass
                        sphereMaterial = new Dielectric(1.5);
                        world.add(new Sphere(center, 0.2, sphereMaterial));
                    }
                }
            }
        }

        Dielectric material1 = new Dielectric(1.5);
        world.add(new Sphere(new Vec3(0, 1, 0), 1.0, material1));

        Material material2 = new Lambertian(new Vec3(0.4, 0.2, 0.1));
        world.add(new Sphere(new Vec3(-4, 1, 0), 1.0, material2));

        Material material3 = new Metal(new Vec3(0.7, 0.6, 0.5), 0.0);
        world.add(new Sphere(new Vec3(4, 1, 0), 1.0, material3));

        try {
            InputStream objInputStream = new FileInputStream(Paths.get("data", "suzanne.obj").toString());
            Obj suzanne = ObjReader.read(objInputStream);
            VertexGeometry monkey = new VertexGeometry(suzanne, new Vec3(5,1,0), 1, new TestMat());
            world.add(monkey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        


        Camera cam = new Camera();

        cam.aspectRatio = 16.0 / 9.0;
        cam.imageWidth = 400;
        cam.samplesPerPixel = 64;
        cam.maxDepth = 12;

        cam.vfov = 20;
        cam.lookFrom = new Vec3(13,2,3);
        cam.lookAt = new Vec3(0,0,0);
        cam.vup = new Vec3(0,1,0);
        cam.defocusAngle = 0.6;
        cam.focusDist = 10.0;

        long start = System.currentTimeMillis();
        //cam.render(world);
        cam.multiThreadedRender(world, 4);
        System.err.println("Total time: " + (System.currentTimeMillis() - start));
    }
}