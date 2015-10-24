package ibrahim.ansari.LearnMotion;

import com.firebase.client.Firebase;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MyoRecorder extends AbstractDeviceListener {
    private final Hub hub;

    private boolean recording = false;
    private List<Vector3> accelerometerData = new ArrayList<>();
    private List<Quaternion> orientationData = new ArrayList<>();
    private List<Vector3> gyroscopeData = new ArrayList<>();
    private Firebase defUser = Session.mainRef.child("duttaoindril");

    public MyoRecorder(Hub hub) {
        this.hub = hub;
    }

    public void start() {
        hub.addListener(this);
        defUser.child("connected").setValue(true);
        recording = true;
    }

    public void stop() {
        hub.removeListener(this);
        defUser.child("connected").setValue(false);
        recording = false;
    }

    public boolean isRecording() {
        return recording;
    }

    public void reset() {
        accelerometerData.clear();
        orientationData.clear();
        gyroscopeData.clear();
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
        accelerometerData.add(accel);
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        orientationData.add(rotation);
    }

    @Override
    public void onGyroscopeData(Myo myo, long timestamp, Vector3 gyro) {
        gyroscopeData.add(gyro);
    }

    public List<Vector3> getAccelerometerData() {
        return accelerometerData;
    }

    public List<Quaternion> getOrientationData() {
        return orientationData;
    }

    public List<Vector3> getGyroscopeData() {
        return gyroscopeData;
    }


}
