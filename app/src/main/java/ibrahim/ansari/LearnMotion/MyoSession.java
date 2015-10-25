package ibrahim.ansari.LearnMotion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MyoSession extends AppCompatActivity {

    @InjectView(R.id.button_record)
    Button mButtonRecord;
    @InjectView(R.id.chart)
    MyoLineChart mLineChart;
    @InjectView(R.id.chart_chooser)
    Spinner mSpinnerChartType;
    @InjectView(R.id.button_save)
    Button mButtonSave;
    @InjectView(R.id.button_compare)
    Button mButtonCompare;

    private ConnectionListener mConnectionListener;
    private MyoRecorder mRecorder;
    private long timestamp = 0;
    static Firebase mainRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myo_session);

        ButterKnife.inject(this);
        Firebase.setAndroidContext(this);
        mainRef = new Firebase("https://myosport.firebaseio.com/duttaoindril/recordings");

        mButtonSave.setEnabled(false);
        mButtonCompare.setEnabled(false);

        Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Toast.makeText(this, "Could not init Myo hub", Toast.LENGTH_SHORT).show();
            finish();
        }

        mRecorder = new MyoRecorder(hub);
        mConnectionListener = new ConnectionListener(hub);
        mConnectionListener.start();

        createChartTypeSpinner();

        hub.attachToAdjacentMyo();
    }

    private void createChartTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.charts, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerChartType.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRecorder.stop();
        mConnectionListener.stop();

        if (isFinishing()) {
            Hub.getInstance().shutdown();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_record)
    public void onClickRecord() {
        if (mRecorder.isRecording()) {
            timestamp = System.currentTimeMillis() - timestamp;
            stopRecording();
            mButtonSave.setEnabled(true);
        } else {
            startRecording();
            mButtonSave.setEnabled(false);
            timestamp = System.currentTimeMillis();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_save)
    public void onClickSave() {
        double[] accelX = new double[mRecorder.getAccelerometerData().size()];
        double[] accelY = new double[mRecorder.getAccelerometerData().size()];
        double[] accelZ = new double[mRecorder.getAccelerometerData().size()];
        double[] orient = new double[4];
        Firebase pushRef = mainRef.push();

        for (int i = 0; i < mRecorder.getAccelerometerData().size(); i++) {
            accelX[i] = mRecorder.getAccelerometerData().get(i).x();
            accelY[i] = mRecorder.getAccelerometerData().get(i).y();
            accelZ[i] = mRecorder.getAccelerometerData().get(i).z();
        }
        orient[0] = mRecorder.getOrientationData().get(0).w();
        orient[1] = mRecorder.getOrientationData().get(0).x();
        orient[2] = mRecorder.getOrientationData().get(0).y();
        orient[3] = mRecorder.getOrientationData().get(0).z();

        Map<String, double[]> dataFB = new HashMap<>();

        dataFB.put("accelStreamx", accelX);
        dataFB.put("accelStreamy", accelY);
        dataFB.put("accelStreamz", accelZ);
        dataFB.put("orientationoffset", orient);

        pushRef.setValue(dataFB);

        String key = mainRef.getKey();
        Map<String, Object> nameFB = new HashMap<>();
        Map<String, Object> timeFB = new HashMap<>();

        nameFB.put("name", "Jagdrill");
        timeFB.put("time", timestamp);

        pushRef.updateChildren(nameFB);
        pushRef.updateChildren(timeFB);

        mButtonSave.setEnabled(false);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.button_compare)
    public void onClickCompare() {
        mButtonSave.setEnabled(false);
        onClickRecord();


    }

    @SuppressWarnings("unused")
    @OnItemSelected(R.id.chart_chooser)
    public void onChartSelected(int position) {
        displayData();
    }

    private void stopRecording() {
        mRecorder.stop();
        mButtonRecord.setText(R.string.recording_start);
        mLineChart.reset();
        setChartData();
        displayData();
    }

    private void startRecording() {
        mRecorder.reset();
        mRecorder.start();
        mButtonRecord.setText(R.string.recording_stop);
    }

    private void setChartData() {
        for (Vector3 accel : mRecorder.getAccelerometerData()) {
            mLineChart.addAccelerometerData(accel);
        }

        for (Vector3 gyro : mRecorder.getGyroscopeData()) {
            mLineChart.addGyroscopeData(gyro);
        }

        for (Quaternion rotation : mRecorder.getOrientationData()) {
            mLineChart.addOrientationData(rotation);
        }
    }

    private void displayData() {
        switch (mSpinnerChartType.getSelectedItemPosition()) {
            case 0: {
                mLineChart.displayAccelerometerData();
                break;
            }
            case 1: {
                mLineChart.displayGyroscopeData();
                break;
            }
            case 2: {
                mLineChart.displayOrientationData();
                break;
            }
            default: {
                mLineChart.displayAccelerometerData();
            }
        }
    }

    private class ConnectionListener extends AbstractDeviceListener {
        private Hub hub;

        private ConnectionListener(Hub hub) {
            this.hub = hub;
        }

        public void start() {
            hub.addListener(this);
        }

        public void stop() {
            hub.removeListener(this);
        }

        @Override
        public void onConnect(Myo myo, long timestamp) {
            Toast.makeText(getApplicationContext(), "Myo Connection Established", Toast.LENGTH_SHORT).show();
            mButtonRecord.setClickable(true);
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Toast.makeText(getApplicationContext(), "Myo Connection Lost", Toast.LENGTH_SHORT).show();
            mButtonRecord.setClickable(false);
        }
    }
}

