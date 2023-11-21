public class Interval {
    public double min, max;
    public static final Interval empty = new Interval(+RTWeekend.infinity, -RTWeekend.infinity);
    public static final Interval universe = new Interval(-RTWeekend.infinity, +RTWeekend.infinity);

    public Interval() {
        this.min = +RTWeekend.infinity;
        this.max = -RTWeekend.infinity;
    }

    public Interval(double _min, double _max) {
        this.min = _min;
        this.max = _max;
    }

    public boolean contains(final double x) {
        return min <= x && x <= max;
    }

    public boolean surrounds(final double x) {
        return min < x && x < max;
    }

}
