package ibrahim.ansari.LearnMotion;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MyoComparer extends AbstractDeviceListener {
    private final Hub hub;

    //Gobal Control Variables
    private boolean next = false;

    //Stage A Orientation Check variables
    private boolean matching = false;
    private double check = 0.1;
    private double[] orientationData = new double[4];
    private double[] fireOriData = new double[4];

    //Stage B Acceleration DTW variables
    private ArrayList<Double> fireAccelDatax = new ArrayList<>();
    private ArrayList<Double> fireAccelDatay = new ArrayList<>();
    private ArrayList<Double> fireAccelDataz = new ArrayList<>();
    private ArrayList<Double> accelerometerDatax = new ArrayList<>();
    private ArrayList<Double> accelerometerDatay = new ArrayList<>();
    private ArrayList<Double> accelerometerDataz = new ArrayList<>();
    private double[] dtw = new double[3];
    private int checkRate = 50;
    double passVal = 0.06;
    private int count = 0;

    //Initializatiion
    public MyoComparer(Hub hub) {
        this.hub = hub;
    }

    //Init comparer function
    public void start(Quaternion oridata, ArrayList<Double> acceldatax, ArrayList<Double> acceldatay, ArrayList<Double> acceldataz) {
        hub.addListener(this);
        stagea = true;
        stageb = false;
        fireOriData[0] = oridata.w;
        fireOriData[1] = oridata.x;
        fireOriData[2] = oridata.y;
        fireOriData[3] = oridata.z;
        fireAccelDatax = acceldatax;
        fireAccelDatay = acceldatay;
        fireAccelDataz = acceldataz;
    }

    public void stop() {
        hub.removeListener(this);
    }

    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion oridata) {
        if(!next) {
            orientationData[0] = oridata.w;
            orientationData[1] = oridata.x;
            orientationData[2] = oridata.y;
            orientationData[3] = oridata.z;
            matching = true;
            for (int i = 0; i < orientationData.length; i++)
                if((orientationData[i] - fireOriData[i]) < -check || (orientationData[i] - fireOriData[i]) > check)
                    matching = false;
            if(matching) {
                myo.vibrate("short");
                myo.vibrate("short");
                next = true;
            }
        }
    }

    //Stage B Acceleration DTW variables
    //private List<Vector3> fireAccelData;
    //private int checkRate = 50;
    //double passVal = 0.06;
    //private List<Vector3> accelerometerData = new ArrayList<>();
    //private double[] dtw = new double[];
    //private int count = 0;

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
        if(next) {
            accelerometerDatax.add(accel.x);
            accelerometerDatay.add(accel.y);
            accelerometerDataz.add(accel.z);
            if(count % checkRate == 0 && count > 0) {
                double[] firex = new double[fireAccelDatax.subList(0, count).size()];
                for (int i = 0; i < firex.length; i++) {
                    firex[i] = fireAccelDatax.subList(0, count).get(i);
                }
                double[] firey = new double[fireAccelDatay.subList(0, count).size()];
                for (int i = 0; i < firey.length; i++) {
                    firey[i] = fireAccelDatay.subList(0, count).get(i);
                }
                double[] firez = new double[fireAccelDataz.subList(0, count).size()];
                for (int i = 0; i < firez.length; i++) {
                    firez[i] = fireAccelDataz.subList(0, count).get(i);
                }
                double[] accx = new double[accelerometerDatax.size()];
                for (int i = 0; i < accx.length; i++) {
                    accx[i] = accelerometerDatax.get(i);
                }
                double[] accy = new double[accelerometerDatay.size()];
                for (int i = 0; i < accy.length; i++) {
                    accy[i] = accelerometerDatay.get(i);
                }
                double[] accz = new double[accelerometerDataz.size()];
                for (int i = 0; i < accz.length; i++) {
                    accz[i] = accelerometerDataz.get(i);
                }
                dtw[0] = new DTW(firex, accx).warpingDistance;
                dtw[1] = new DTW(firey, accy).warpingDistance;
                dtw[2] = new DTW(firez, accz).warpingDistance;
                if(dtw[0] > passVal) {
                    myo.vibrate("short");
                    myo.vibrate("long");
                    //console.speak("You fucked up in the forward and backward direction");
                } else if(dtw[1] > passVal) {
                    myo.vibrate("short");
                    myo.vibrate("long");
                    //console.speak("You fucked up in the left and right direction");
                } else if(dtw[2] > passVal) {
                    myo.vibrate("short");
                    myo.vibrate("long");
                    //console.speak("You fucked up in the up and down direction");
                }
                if (count > firex.length) {
                    next = false;
                }
            }
        }
    }
}

/*
    public boolean readyToCompare() {
        double[] recordedOri = {0.5,0.5,0.5,0.5};
        double[] currentOri = {0,0,0,0};
        boolean matching = false;
        double check = 0.1;
        while(true) { // Simulate the Myo Orientation Listener
            currentOri[0] = data["w"]; // Simulate currentOri getting current orientation data from Myo
            currentOri[1] = data["x"]; // Simulate currentOri getting current orientation data from Myo
            currentOri[2] = data["y"]; // Simulate currentOri getting current orientation data from Myo
            currentOri[3] = data["z"]; // Simulate currentOri getting current orientation data from Myo
            matching = true;
            for (int i = 0; i < currentOri.length; i++)
                if((currentOri[i] - recordedOri[i]) < -check || (currentOri[i] - recordedOri[i]) > check)
                    matching = false;
            if(matching) {
                while(false); // Simulate Orientation Data getting being turned off
                vibrate(); vibrate(); // Simulate two short vibrations from Myo
                return matching;
            }
        }
    }

    if(readyToCompare())
        compare();

    ArrayList<Double> recaccelx = new ArrayList<Double>(); //simulate getting a previously done recording's accelstreamx
    ArrayList<Double> recaccely = new ArrayList<Double>(); //simulate getting a previously done recording's accelstreamy
    ArrayList<Double> recaccelz = new ArrayList<Double>(); //simulate getting a previously done recording's accelstreamz

    int total = 1; // The number of times users called comparing
    int passed = 0; // The number of times they used got a successful recording done
    int checkRate = 50; // How often in milliseconds
    double passVal = 0.6; // The amount the dtw must yeild below to pass the test

    public boolean compare() {
        ArrayList<Double> accelx = new ArrayList<Double>(); //Local recording to do dtw against
        ArrayList<Double> accely = new ArrayList<Double>(); //Local recording to do dtw against
        ArrayList<Double> accelz = new ArrayList<Double>(); //Local recording to do dtw against
        double dtwx = 0;
        double dtwy = 0;
        double dtwz = 0;
        int count = 0;
        while(true) { // Simulate the Myo Accelerometer Listener
            accelx.push(data['x']); // Simulate pushing Accelerometer Data to accelx from Myo
            accely.push(data['y']); // Simulate pushing Accelerometer Data to accely from Myo
            accelz.push(data['z']); // Simulate pushing Accelerometer Data to accelz from Myo
            if(count % checkRate == 0 && count > 0) {
                dtwx = dtw.compute(recaccelx.subList(0, count), accelx);
                dtwy = dtw.compute(recaccely.subList(0, count), accely);
                dtwz = dtw.compute(recaccelz.subList(0, count), accelz);
                if(dtwx > passVal) {
                    vibrate(); vibrate(); // Simulate a short and a medium vibration from Myo
                    console.speak("You fucked up in the forward and backward direction");
                } else if(dtwy > passVal) {
                    vibrate(); vibrate(); // Simulate a short and a medium vibration from Myo
                    console.speak("You fucked up in the left and right direction");
                } else if(dtwz > passVal) {
                    vibrate(); vibrate(); // Simulate a short and a medium vibration from Myo
                    console.speak("You fucked up in the up and down direction");
                }
            }
            if (count > recaccelx.length) {
                while(false); // Simulate turning off myo accelerometer data
                return true; // Returning True means that the user successfully completed the recording
                passed++;
            }
            count++;
        }
        total++;
        return false;
    }
    */