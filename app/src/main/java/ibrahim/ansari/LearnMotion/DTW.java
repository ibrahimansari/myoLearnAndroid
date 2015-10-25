package ibrahim.ansari.LearnMotion;

import java.util.ArrayList;

public class DTW {

    protected double[] seq1;
    protected double[] seq2;
    protected int[][] warpingPath;

    protected int n;
    protected int m;
    protected int K;

    protected double warpingDistance;

    public DTW(double[] sample, double[] templete) {
        seq1 = sample;
        seq2 = templete;

        n = seq1.length;
        m = seq2.length;
        K = 1;

        warpingPath = new int[n + m][2];    // max(n, m) <= K < n + m
        warpingDistance = 0.0;

        this.compute();
    }

    public void compute() {
        double accumulatedDistance = 0.0;

        double[][] d = new double[n][m];    // local distances
        double[][] D = new double[n][m];    // global distances

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                d[i][j] = distanceBetween(seq1[i], seq2[j]);
            }
        }

        D[0][0] = d[0][0];

        for (int i = 1; i < n; i++) {
            D[i][0] = d[i][0] + D[i - 1][0];
        }

        for (int j = 1; j < m; j++) {
            D[0][j] = d[0][j] + D[0][j - 1];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                accumulatedDistance = Math.min(Math.min(D[i - 1][j], D[i - 1][j - 1]), D[i][j - 1]);
                accumulatedDistance += d[i][j];
                D[i][j] = accumulatedDistance;
            }
        }
        accumulatedDistance = D[n - 1][m - 1];

        int i = n - 1;
        int j = m - 1;
        int minIndex = 1;

        warpingPath[K - 1][0] = i;
        warpingPath[K - 1][1] = j;

        while ((i + j) != 0) {
            if (i == 0) {
                j -= 1;
            } else if (j == 0) {
                i -= 1;
            } else {    // i != 0 && j != 0
                double[] array = {D[i - 1][j], D[i][j - 1], D[i - 1][j - 1]};
                minIndex = this.getIndexOfMinimum(array);

                if (minIndex == 0) {
                    i -= 1;
                } else if (minIndex == 1) {
                    j -= 1;
                } else if (minIndex == 2) {
                    i -= 1;
                    j -= 1;
                }
            } // end else
            K++;
            warpingPath[K - 1][0] = i;
            warpingPath[K - 1][1] = j;
        } // end while
        warpingDistance = accumulatedDistance / K;

        this.reversePath(warpingPath);
    }

    protected void reversePath(int[][] path) {
        int[][] newPath = new int[K][2];
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < 2; j++) {
                newPath[i][j] = path[K - i - 1][j];
            }
        }
        warpingPath = newPath;
    }

    public double getDistance() {
        return warpingDistance;
    }

    protected double distanceBetween(double p1, double p2) {
        return (p1 - p2) * (p1 - p2);
    }

    protected int getIndexOfMinimum(double[] array) {
        int index = 0;
        double val = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] < val) {
                val = array[i];
                index = i;
            }
        }
        return index;
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
}