package ibrahim.ansari.myoLearn;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;


public class MyoLineChart extends LineChart {
    private LineData mAccelerometerLineData;
    private LineData mGyroscopeLineData;
    private LineData mOrientationLineData;


    public MyoLineChart(Context context) {
        super(context);
    }

    public MyoLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyoLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        setDrawXLabels(false);
        setDrawYValues(false);
        setDrawGridBackground(false);
        setDrawVerticalGrid(false);
        setDrawHorizontalGrid(false);

        initAccelerometerLineData();
        initGyroscopeLineData();
        initOrientationLineData();
    }

    private void initAccelerometerLineData() {
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"x"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"y"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"z"));

        initLineDataSet(lineDataSets.get(0), Color.BLUE);
        initLineDataSet(lineDataSets.get(1),Color.GREEN);
        initLineDataSet(lineDataSets.get(2),Color.RED);

        mAccelerometerLineData = new LineData(new ArrayList<String>(),lineDataSets);
    }

    private void initGyroscopeLineData() {
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"x"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"y"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(),"z"));

        initLineDataSet(lineDataSets.get(0), Color.BLUE);
        initLineDataSet(lineDataSets.get(1),Color.GREEN);
        initLineDataSet(lineDataSets.get(2),Color.RED);

        mGyroscopeLineData = new LineData(new ArrayList<String>(),lineDataSets);
    }

    private void initOrientationLineData(){
        ArrayList<LineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(), "roll"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(), "pitch"));
        lineDataSets.add(new LineDataSet(new ArrayList<Entry>(), "yaw"));

        initLineDataSet(lineDataSets.get(0), Color.BLUE);
        initLineDataSet(lineDataSets.get(1),Color.GREEN);
        initLineDataSet(lineDataSets.get(2),Color.RED);

        mOrientationLineData = new LineData(new ArrayList<String>(),lineDataSets);
    }

    private void initLineDataSet(LineDataSet lineDataSet,int color) {
        lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setColor(color);
        lineDataSet.setHighLightColor(Color.GRAY);

    }

    public void reset() {
        initAccelerometerLineData();
        initGyroscopeLineData();
        initOrientationLineData();
    }

    public void addAccelerometerData(Vector3 accel) {
        int index = mAccelerometerLineData.getXValCount() + 1;

        mAccelerometerLineData.getXVals().add(index+"");
        mAccelerometerLineData.addEntry(new Entry((float)accel.x(), index),0);
        mAccelerometerLineData.addEntry(new Entry((float)accel.y(), index),1);
        mAccelerometerLineData.addEntry(new Entry((float)accel.z(), index),2);
    }

    public void addGyroscopeData(Vector3 gyro) {
        int index = mGyroscopeLineData.getXValCount() + 1;

        mGyroscopeLineData.getXVals().add(index+"");
        mGyroscopeLineData.addEntry(new Entry((float)gyro.x(), index),0);
        mGyroscopeLineData.addEntry(new Entry((float)gyro.y(), index),1);
        mGyroscopeLineData.addEntry(new Entry((float)gyro.z(), index),2);
    }

    public void addOrientationData(Quaternion rotation) {
        int index = mOrientationLineData.getXValCount() + 1;

        mOrientationLineData.getXVals().add(index+"");
        mOrientationLineData.addEntry(new Entry((float)Quaternion.roll(rotation), index),0);
        mOrientationLineData.addEntry(new Entry((float)Quaternion.pitch(rotation), index),1);
        mOrientationLineData.addEntry(new Entry((float)Quaternion.yaw(rotation), index),2);
    }

    public void displayAccelerometerData() {
        displayData(mAccelerometerLineData);
    }

    public void displayGyroscopeData() {
        displayData(mGyroscopeLineData);
    }

    public void displayOrientationData() {
        displayData(mOrientationLineData);
    }

    private void displayData(LineData lineData) {
        setData(lineData);
        setStartAtZero(false);
        notifyDataSetChanged();
        invalidate();
    }
}
