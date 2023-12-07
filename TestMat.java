public class TestMat implements Material {
    private Vec3 albedo;
    private Ray latestScattered =  new Ray();

    public TestMat() {
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
        albedo = rec.normal;
        latestScattered = scattered;
        return true;
    }

    @Override
    public Material createCopy() {
        return new TestMat();
    }
}
