package ibrahim.ansari.myoLearn;

import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MyoData {
    private List<Vector3> accelerometerData = new ArrayList<>();
    private List<Quaternion> orientationData = new ArrayList<>();
    private List<Vector3> gyroscopeData = new ArrayList<>();

    public List<Vector3> getGyroscopeData() {
        return gyroscopeData;
    }

    public List<Quaternion> getOrientationData() {

        return orientationData;
    }

    public List<Vector3> getAccelerometerData() {
        return accelerometerData;
    }
}
