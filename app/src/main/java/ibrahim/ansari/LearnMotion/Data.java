package ibrahim.ansari.LearnMotion;

public class Data {
    private long time;
    private String name;
    private double[] accelx, accely, accelz, orient;

    public double[] getAccelx() {
        return accelx;
    }

    public double[] getAccely() {
        return accely;
    }

    public double[] getAccelz() {
        return accelz;
    }

    public double[] getOrient() {
        return orient;
    }

    public long getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public void setAccelx(double[] accelx) {
        this.accelx = accelx;
    }

    public void setAccely(double[] accely) {
        this.accely = accely;
    }

    public void setAccelz(double[] accelz) {
        this.accelz = accelz;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrient(double[] orient) {
        this.orient = orient;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

