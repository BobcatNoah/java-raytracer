public interface Material {
    public boolean scatter(final Ray r, final HitRecord rec, Vec3 attenuation, Ray scattered);
    public Ray getScattered();
    public Vec3 getAttenuation();
    public Material createCopy();
}
