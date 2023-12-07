import java.util.concurrent.ThreadLocalRandom;

public class Dielectric implements Material {
    // Refraction index
    private double ir;
    private Ray latestScattered;
    private Vec3 latestAttenuation;

    public Dielectric(double indexOfRefraction) {
        this.ir = indexOfRefraction;
    }

    public Vec3 getAttenuation(){
        return latestAttenuation;
    };

    public Ray getScattered() {
        return latestScattered;
    }

    public boolean scatter(final Ray rIn, final HitRecord rec, Vec3 attenuation, Ray scattered) {
        latestAttenuation = new Vec3(1.0, 1.0, 1.0);
        double refractionRatio = rec.front_face ? (1.0/ir) : ir;

        Vec3 unitDirection = Vec3.unit_vector(rIn.direction());
        double cosTheta = Math.min(Vec3.dot(unitDirection.negate(), rec.normal), 1.0);
        double sinTheta = Math.sqrt(1.0 - cosTheta*cosTheta);
        
        boolean cannotRefract = refractionRatio * sinTheta > 1.0;
        Vec3 direction;

        if (cannotRefract ||  reflectance(cosTheta, refractionRatio) > ThreadLocalRandom.current().nextDouble()) {
            direction = Vec3.reflect(unitDirection, rec.normal);
        } else {
            direction = Vec3.refract(unitDirection, rec.normal, refractionRatio);
        }

        latestScattered = new Ray(rec.p, direction);
        return true;
    }

    private static double reflectance(double cosine, double ref_idx) {
        // Use Schlick's approximation for reflectance.
        double r0 = (1-ref_idx) / (1+ref_idx);
        r0 = r0*r0;
        return r0 + (1-r0)*Math.pow((1 - cosine),5);
    }

    @Override
    public Material createCopy() {
        return new Dielectric(ir);
    }
}
