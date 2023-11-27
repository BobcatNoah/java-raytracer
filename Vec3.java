public class Vec3 {
    public double[] e = new double[3];

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
        return new Vec3(e[0] + v.e[0], e[1] + v.e[1], e[2] + v.e[2]);
    }

    public void plusEquals(final Vec3 v) {
        e[0] += v.e[0];
        e[1] += v.e[1];
        e[2] += v.e[2];
    }

    public Vec3 multiply(double t) {
        return new Vec3(e[0] * t, e[1] * t, e[2] * t);
    }

    public Vec3 multiply(Vec3 v) {
        return new Vec3(v.e[0] * e[0], v.e[1] * e[1], v.e[2] * e[2]);
    }

    public Vec3 divideBy(double t) {
        return new Vec3(e[0] / t, e[1] / t, e[2] / t);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return e[0] * e[0] + e[1] * e[1] + e[2] * e[2];
    }

    public boolean nearZero() {
        // Return true if the vector is close to zero in all dimensions.
        double s = 1e-8;
        return (Math.abs(e[0]) < s) && (Math.abs(e[1]) < s) && (Math.abs(e[2]) < s);

    }

    public String toString() {
        return String.format("(%s %s %s)", e[0], e[1], e[2]);
    }

    public Vec3 minus(Vec3 v) {
        return new Vec3(e[0] - v.e[0], e[1] - v.e[1], e[2] - v.e[2]);
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

    public static Vec3 randomInUnitSphere() {
        while (true) {
            Vec3 p = Vec3.random(-1,1);
            if (p.lengthSquared() < 1) {
                return p;
            }
        }
    }

    public static Vec3 randomUnitVector() {
        return unit_vector(randomInUnitSphere());
    }
    
    public static Vec3 randomOnHemisphere(final Vec3 normal) {
        Vec3 onUnitSphere = randomUnitVector();
        if (dot(onUnitSphere, normal) > 0.0) {
            return onUnitSphere;
        } else {
            return onUnitSphere.negate();
        }
    }

    public static Vec3 reflect(final Vec3 v, final Vec3 n) {
        return v.minus(n.multiply(2).multiply(dot(v,n)));
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
