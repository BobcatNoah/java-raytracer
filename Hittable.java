public interface Hittable {
    public boolean hit(Ray r, double ray_tmin, double ray_tmax, HitRecord rec);
}
