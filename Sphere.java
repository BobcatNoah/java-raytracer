public class Sphere implements Hittable {
    private Vec3 center;
    private double radius;

    public Sphere(Vec3 _center, double _radius) {
        this.center = _center;
        this.radius = _radius;
    }

    public boolean hit(Ray r, double ray_tmin, double ray_tmax, HitRecord rec) {
        Vec3 oc = r.origin().minus(center);
        double a = r.direction().length_squared();
        double half_b = Vec3.dot(oc, r.direction());
        double c = oc.length_squared() - radius * radius;
        double discriminant = half_b*half_b - a*c;

        if (discriminant < 0) {
            return false;
        }
        double sqrtd = Math.sqrt(discriminant);

        // Find the nearest root that lies in the acceptable range.
        double root = (-half_b - sqrtd) / a;
        if (root <= ray_tmin || root >= ray_tmax) {
            root = (-half_b + sqrtd) / a;
            if (root <= ray_tmin || root >= ray_tmax)  {
                return false;
            }
        }

        rec.t = root;
        rec.p = r.at(rec.t);
        Vec3 outward_normal = rec.p.minus(center).divideBy(radius);
        // The surface normal is the hit point minus the center, then divided by radius in order to normalize on scale [0,1]
        rec.set_face_normal(r, outward_normal);

        return true;
    }
}
