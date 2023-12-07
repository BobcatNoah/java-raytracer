public interface Hittable {
    public boolean hit(Ray r, Interval ray_t, HitRecord rec);
    public Hittable createCopy();
}
