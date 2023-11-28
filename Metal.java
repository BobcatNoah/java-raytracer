public class Metal implements Material {
    private Vec3 albedo;
    private Ray latestScattered =  new Ray();
    private double fuzz;

    public Metal(final Vec3 a, double f) {
        this.albedo = a;
        this.fuzz = f < 1 ? f : 1;
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
        scattered = new Ray(rec.p, reflected.plus(Vec3.randomUnitVector().multiply(fuzz)));
        attenuation = albedo;
        latestScattered = scattered;
        return (Vec3.dot(scattered.direction(), rec.normal) > 0);
    }
}
