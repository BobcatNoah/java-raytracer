import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjUtils;

public class VertexGeometry implements Hittable {
    private Obj obj;
    private Material mat;
    private Vec3 origin;

    public VertexGeometry(Obj obj, Material mat) {
        this.obj = ObjUtils.triangulate(obj);
        this.mat = mat;
    }

    @Override
    public boolean hit(Ray r, Interval ray_t, HitRecord rec) {
        int faceCount = obj.getNumFaces();
        double closestSoFar = ray_t.max;
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;

        for (int i = 0; i < faceCount; i++) {
            ObjFace face = obj.getFace(i);
            FloatTuple p0 = obj.getVertex(face.getVertexIndex(0));
            FloatTuple p1 = obj.getVertex(face.getVertexIndex(1));
            FloatTuple p2 = obj.getVertex(face.getVertexIndex(2));
            Vec3 a = new Vec3(p0.get(0), p0.get(1), p0.get(2));
            Vec3 b = new Vec3(p1.get(0), p1.get(1), p1.get(2));
            Vec3 c = new Vec3(p2.get(0), p2.get(1), p2.get(2));

            Vec3 faceNormal = Vec3.unit_vector(Vec3.cross(b.minus(a), c.minus(a)));
            double d = Vec3.dot(faceNormal, a);

            boolean isOccluded = Vec3.dot(a.minus(r.origin()), faceNormal) >= 0;

            if (!isOccluded) {
                double t = (d - Vec3.dot(faceNormal, r.origin()))/Vec3.dot(faceNormal, r.direction());
                Vec3 hit = r.at(t);
                if (t < closestSoFar &&
                    Vec3.dot(Vec3.cross(b.minus(a), hit.minus(a)), faceNormal) >= 0 &&
                    Vec3.dot(Vec3.cross(c.minus(b), hit.minus(b)), faceNormal) >= 0 &&
                    Vec3.dot(Vec3.cross(a.minus(c), hit.minus(c)), faceNormal) >= 0 ) {
                    closestSoFar = t;
                    tempRec.normal = faceNormal;
                    hitAnything = true;
                }
            }
        }
        if (hitAnything && ray_t.surrounds(closestSoFar)) {
            rec.t = closestSoFar;
            rec.p = r.at(closestSoFar);
            rec.set_face_normal(r, tempRec.normal);
            rec.mat = this.mat;
            if (Vec3.dot(tempRec.normal, r.direction()) == 0) {
                return false;
            }
            return true;
        }
        return false;
    }
}
