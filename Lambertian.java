public class Lambertian implements Material {
    private Vec3 albedo;
    private Ray latestScattered = new Ray();
    

    public Lambertian(final Vec3 a) {
        this.albedo = a;
    }

    public Vec3 getAttenuation() {
        return albedo;
    }

    public Ray getScattered() {
        return latestScattered;
    }

    @Override
    public boolean scatter(final Ray rIn, final HitRecord rec, Vec3 attenuation, Ray scattered) {
        Vec3 scatterDirection = rec.normal.plus(Vec3.randomUnitVector());

        // Catch degenerate scatter direction
        if (scatterDirection.nearZero()) {
            scatterDirection = rec.normal;
        }

        scattered = new Ray(rec.p, scatterDirection);
        attenuation = albedo;
        latestScattered = scattered;
        return true;
    }

    @Override
    public Material createCopy() {
        return new Lambertian(new Vec3(albedo.e[0], albedo.e[1], albedo.e[2]));
    }
}
