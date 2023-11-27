public class Vec3 {
    private double[] e = new double[3];

    public Vec3() {}
    public Vec3(double e0, double e1, double e2) {
        e[0] = e0;
        e[1] = e1;
        e[2] = e2;
    }

    public double x() {
        return e[0];
    }

    public double y() {
        return e[1];
    }

    public double z() {
        return e[2];
    }

    public Vec3 negate() {
        return new Vec3(-e[0], -e[1], -e[2]);
    }

    public Vec3 plus(Vec3 v) {
        return new Vec3(e[0] + v.x(), e[1] + v.y(), e[2] + v.z());
    }

    public Vec3 multiply(double t) {
        return new Vec3(e[0] * t, e[1] * t, e[2] * t);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(v.x() * e[0], v.y() * e[1], v.z() * e[2]);
    }

    public Vec3 divideBy(double t) {
        return new Vec3(e[0] / t, e[1] / t, e[2] / t);
    }

    public double length() {
        return Math.sqrt(length_squared());
    }

    public double length_squared() {
        return e[0] * e[0] + e[1] * e[1] + e[2] * e[2];
    }

    public String toString() {
        return String.format("(%s %s %s)", e[0], e[1], e[2]);
    }

    public Vec3 minus(Vec3 v) {
        return new Vec3(e[0] - v.x(), e[1] - v.y(), e[2] - v.z());
    }

    public static Vec3 unit_vector(Vec3 v) {
        return v.divideBy(v.length());
    }

    public static double dot(Vec3 u, Vec3 v) {
        return u.e[0] * v.e[0]
             + u.e[1] * v.e[1]
             + u.e[2] * v.e[2];
    }

    public void set(double e0, double e1, double e2) {
        e[0] = e0;
        e[1] = e1;
        e[2] = e2;
    }

    public static Vec3 random() {
        return new Vec3(Math.random(), Math.random(), Math.random());
    }

    public static Vec3 random(double min, double max) {
        return new Vec3(RTWeekend.randomDouble(min, max), RTWeekend.randomDouble(min, max), RTWeekend.randomDouble(min, max));
    }

    // TODO: cross product
    /*  
        inline vec3 cross(const vec3 &u, const vec3 &v) {
            return vec3(u.e[1] * v.e[2] - u.e[2] * v.e[1],
                u.e[2] * v.e[0] - u.e[0] * v.e[2],
                u.e[0] * v.e[1] - u.e[1] * v.e[0]);
        }
     */


}
