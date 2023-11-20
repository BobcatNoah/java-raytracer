public class Ray {
    private Vec3 orig;
    private Vec3 dir;

    public Ray() {}

    public Ray(Vec3 origin, Vec3 direction) {
        orig = origin;
        dir = direction;
    }

    public Vec3 origin() {
        return orig;
    }

    public Vec3 direction() {
        return dir;
    }

    public Vec3 at(double t) {
        return orig.plus(dir.multiply(t));
    }


}
