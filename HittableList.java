import java.util.ArrayList;
import java.util.List;

public class HittableList implements Hittable {
    public List<Hittable> objects = new ArrayList<Hittable>();
    private HitRecord latestHitRecord = new HitRecord();

    public HittableList() {};
    public HittableList(Hittable object) {
        add(object);
    }

    public void clear() {
        objects.clear();
    }

    public void add(Hittable object) {
        objects.add(object);
    }

    public HitRecord getLatestHitRecord() {
        return latestHitRecord;
    }

    public HittableList createCopy() {
        HittableList copy = new HittableList();
        for (Hittable i : objects) {
            copy.add(i);
        }
        return copy;
    }

    @Override
    public boolean hit(Ray r, Interval ray_t, HitRecord rec) {
        HitRecord tempRec = new HitRecord();
        boolean hitAnything = false;
        double closestSoFar = ray_t.max;

        // Each object implements Hittable. Thus, each object can have its own hit method.
        for (Hittable object : objects) {
            // object.hit updates tempRec within the hit method.
            if(object.hit(r, new Interval(ray_t.min, closestSoFar), tempRec)) {
                hitAnything = true;
                closestSoFar = tempRec.t;
                latestHitRecord = tempRec;
                //rec = tempRec;
                //latestHitRecord = rec;
            }
        }
        return hitAnything;
    }

}
