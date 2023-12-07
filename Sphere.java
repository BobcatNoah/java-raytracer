public class Sphere implements Hittable {
    private Vec3 center;
    private double radius;
    private Material mat;

    public Sphere(Vec3 _center, double _radius, Material _material) {
        this.center = _center;
        this.radius = _radius;
        this.mat = _material;
    }

    public Sphere createCopy() {
        return new Sphere(new Vec3(center.e[0], center.e[1], center.e[2]), radius, mat.createCopy());
    }

    public boolean hit(Ray r, Interval ray_t, HitRecord rec) {
        Vec3 oc = r.origin().minus(center);
        double a = r.direction().lengthSquared();
        double half_b = Vec3.dot(oc, r.direction());
        double c = oc.lengthSquared() - radius * radius;
        double discriminant = half_b*half_b - a*c;

        if (discriminant < 0) {
            return false;
        }
        double sqrtd = Math.sqrt(discriminant);

        // Find the nearest root that lies in the acceptable range.
        double root = (-half_b - sqrtd) / a;
        if (!ray_t.surrounds(root)) {
            root = (-half_b + sqrtd) / a;
            if (!ray_t.surrounds(root))  {
                return false;
            }
        }

        rec.t = root;
        rec.p = r.at(rec.t);
        Vec3 outward_normal = rec.p.minus(center).divideBy(radius);
        // The surface normal is the hit point minus the center, then divided by radius in order to normalize on scale [0,1]
        rec.set_face_normal(r, outward_normal);
        rec.mat = mat;

        return true;
    }
}
