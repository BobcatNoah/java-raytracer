import de.javagl.obj.FloatTuple;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjFace;
import de.javagl.obj.ObjUtils;

public class VertexGeometry implements Hittable {
    private Obj obj;
    private Material mat;
    private Vec3 origin;
    private Vec3[][] faces;
    private Vec3[] normals;
    private double[] d;
    private double scale = 1;
    private double radius;
    private Vec3 center;

    public VertexGeometry(Obj obj, Vec3 origin, double scale, Material mat) {
        this.obj = ObjUtils.triangulate(obj);
        int faceCount = this.obj.getNumFaces();
        this.mat = mat;
        this.scale = scale;
        this.origin = origin;
        this.faces = new Vec3[faceCount][3];
        this.normals = new Vec3[faceCount];
        this.d = new double[faceCount];

        Interval width = new Interval();
        Interval height = new Interval();
        Interval depth = new Interval();

        
        for (int i = 0; i < faceCount; i++) {
            ObjFace face = this.obj.getFace(i);
            FloatTuple p0 = this.obj.getVertex(face.getVertexIndex(0));
            FloatTuple p1 = this.obj.getVertex(face.getVertexIndex(1));
            FloatTuple p2 = this.obj.getVertex(face.getVertexIndex(2));
            this.faces[i][0] = new Vec3(p0.get(0), p0.get(1), p0.get(2));
            this.faces[i][1] = new Vec3(p1.get(0), p1.get(1), p1.get(2));
            this.faces[i][2] = new Vec3(p2.get(0), p2.get(1), p2.get(2));
            // Scale and translate model
            this.faces[i][0] = this.faces[i][0].multiply(scale).plus(origin);
            this.faces[i][1] = this.faces[i][1].multiply(scale).plus(origin);
            this.faces[i][2] = this.faces[i][2].multiply(scale).plus(origin);

            // Find the minimum and maximum width, height, and depth
            for (int k = 0; k < 3; k++) {
                width.min = Math.min(width.min, this.faces[i][k].x());
                width.max = Math.max(width.max, this.faces[i][k].x());
                height.min = Math.min(height.min, this.faces[i][k].y());
                height.max = Math.max(height.max, this.faces[i][k].y());
                depth.min = Math.min(depth.min, this.faces[i][k].z());
                depth.max = Math.max(depth.max, this.faces[i][k].z());
            }

            // unit_vector((B - A) X (C - A))
            this.normals[i] = Vec3.unit_vector(Vec3.cross(this.faces[i][1].minus(this.faces[i][0]), this.faces[i][2].minus(this.faces[i][0]))); 
            this.d[i] = Vec3.dot(this.normals[i], this.faces[i][0]);
        }

        double maxDistance = 0;
        for (int face = 0; face < this.faces.length; face++) {
            for (int vertex = 0; vertex < 3; vertex++) {
                for (int face2 = 0; face2 < this.faces.length; face2++) {
                    for (int vertex2 = 0; vertex2 < 3; vertex2++) {
                        double x1 = this.faces[face][vertex].x();
                        double x2 = this.faces[face2][vertex2].x();
                        double y1 = this.faces[face][vertex].y();
                        double y2 = this.faces[face2][vertex2].y();
                        double z1 = this.faces[face][vertex].z();
                        double z2 = this.faces[face2][vertex2].z();
                        maxDistance = Math.max(maxDistance, Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1) * (z2 - z1)));
                    }
                }
            }
        }

        this.radius = maxDistance / 2;
        // the center is at the origin plus half the width, height, and depth
            this.center = new Vec3((width.max + width.min) / 2.0, (height.max + height.min) / 2.0, (depth.max + depth.min) / 2.0);

    }

    @Override
    public boolean hit(Ray r, Interval ray_t, HitRecord rec) {
        Vec3 oc = r.origin().minus(center);
        double a1 = r.direction().lengthSquared();
        double half_b = Vec3.dot(oc, r.direction());
        double c1 = oc.lengthSquared() - this.radius * this.radius;
        double discriminant = half_b*half_b - a1*c1;

        if (discriminant < 0) {
            return false;
        }

        int faceCount = faces.length;
        double closestSoFar = ray_t.max;
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;

        for (int i = 0; i < faceCount; i++) {
            Vec3 a = this.faces[i][0];
            Vec3 b = this.faces[i][1];
            Vec3 c = this.faces[i][2];

            Vec3 faceNormal = this.normals[i];
            double d = this.d[i];

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

    @Override
    public Hittable createCopy() {
        // I'll figure out how to copy an obj later.
        return new VertexGeometry(obj, new Vec3(origin.e[0], origin.e[1], origin.e[2]), this.scale, mat.createCopy());
    }
}
