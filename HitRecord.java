public class HitRecord {
    // point3
    public Vec3 p;
    public Vec3 normal;
    public double t;
    public boolean front_face;
    public Material mat;

    public void set_face_normal(Ray r, Vec3 outward_normal) {
        // Sets the hit record normal vector.
        // NOTE: the parameter `outward_normal` is assumed to have unit length.

        front_face = Vec3.dot(r.direction(), outward_normal) < 0;
        normal = front_face ? outward_normal : outward_normal.negate();
    }
}
