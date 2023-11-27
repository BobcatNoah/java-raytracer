public class Metal implements Material {
    private Vec3 albedo;
    private Ray latestScattered =  new Ray();

    public Metal(final Vec3 a) {
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
        Vec3 reflected = Vec3.reflect(Vec3.unit_vector(rIn.direction()), rec.normal);
        scattered = new Ray(rec.p, reflected);
        attenuation = albedo;
        latestScattered = scattered;
        return true;
    }
}
