package ibrahim.ansari.LearnMotion;

public class MyoData {
    private String name;
    private double[] accelStreamx;
    private double[] accelStreamy;
    private double[] accelStreamz;
    private double[] orientationoffset;

    public void setOrientationoffset(double[] orientationoffset) {
        this.orientationoffset = orientationoffset;
    }

    public void setAccelStreamz(double[] accelStreamz) {

        this.accelStreamz = accelStreamz;
    }

    public void setAccelStreamy(double[] accelStreamy) {

        this.accelStreamy = accelStreamy;
    }

    public void setAccelStreamx(double[] accelStreamx) {

        this.accelStreamx = accelStreamx;
    }

    public void setName(String name) {

        this.name = name;
    }

    public double[] getOrientationoffset() {

        return orientationoffset;
    }

    public double[] getAccelStreamz() {

        return accelStreamz;
    }

    public double[] getAccelStreamy() {

        return accelStreamy;
    }

    public double[] getAccelStreamx() {

        return accelStreamx;
    }

    public String getName() {

        return name;
    }
}
