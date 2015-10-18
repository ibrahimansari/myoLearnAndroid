package ibrahim.ansari.myoLearn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.value_status) TextView mTextViewValueStatus;

    @InjectView(R.id.button_record) Button mButtonRecord;

    @InjectView(R.id.chart) MyoLineChart mLineChart;

    @InjectView(R.id.chart_chooser) Spinner mSpinnerChartType;

    private ConnectionListener mConnectionListener;

    private MyoRecorder mRecorder;

    static Firebase fbRef = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://myosport.firebaseio.com/");

        Hub hub = Hub.getInstance();
         if (!hub.init(this)) {
            Toast.makeText(this,"Could not init hub",Toast.LENGTH_SHORT).show();
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
            stopRecording();
        } else {
            startRecording();
        }
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

        for (Quaternion rotation : mRecorder.getOrienationData()) {
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
            mTextViewValueStatus.setText(R.string.status_connected);
            mButtonRecord.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            mTextViewValueStatus.setText(R.string.status_disconnected);
            mButtonRecord.setVisibility(View.INVISIBLE);
        }
    }
}

