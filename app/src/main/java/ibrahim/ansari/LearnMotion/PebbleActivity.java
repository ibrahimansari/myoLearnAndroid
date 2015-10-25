package ibrahim.ansari.LearnMotion;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PebbleActivity extends ListActivity {

    //Constants
    private static final int NUM_SAMPLES = 15;

    //State
    private int[] latest_data;
    ArrayList<Integer> list = new ArrayList<Integer>();
    double average = 0;
    ArrayList<Integer> x = new ArrayList<Integer>(), y = new ArrayList<Integer>(), z = new ArrayList<Integer>();

    private double time = System.currentTimeMillis();
    //Other members
    private PebbleKit.PebbleDataReceiver receiver;
    private UUID uuid = UUID.fromString("64b2d2c2-bc1c-4a6d-bd56-0e0ffb936403");
    private Handler handler = new Handler();
    private HashMap<String, Object> params = new HashMap<String, Object>();
    private Firebase mainRef = null;
    private List<String> dataList = new ArrayList<>();
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myo);

        Firebase.setAndroidContext(this);
        mainRef = new Firebase("https://myosport.firebaseio.com/ibrah/recordings");

        PebbleDictionary dict = new PebbleDictionary();
        Toast.makeText(getApplicationContext(), "Paired with Pebble", Toast.LENGTH_SHORT).show();
        dict.addInt32(0, 0);
        PebbleKit.sendDataToPebble(getApplicationContext(), uuid, dict);

        mainRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    HashMap map = (HashMap) postSnapshot.getValue();
                    Iterator it = map.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        int i = 0;
                        if (pair.getKey().equals("name")) {
                            dataList.add(pair.getValue().toString());
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.listview_item, R.id.action_name, dataList);
        setListAdapter(myAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PebbleActivity.this, PebbleSession.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        receiver = new PebbleKit.PebbleDataReceiver(uuid) {

            @Override
            public void receiveData(Context context, int transactionId, PebbleDictionary data) {
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);

                //Get data
                latest_data = new int[3 * NUM_SAMPLES];
                for (int i = 0; i < NUM_SAMPLES; i++) {
                    for (int j = 0; j < 3; j++) {
                        try {
                            latest_data[(3 * i) + j] = data.getInteger((3 * i) + j).intValue();
                        } catch (Exception e) {
                            latest_data[(3 * i) + j] = -1;
                        }
                    }
                }

                //Show
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (x.size() > 15) {
                            x.remove(0);
                            y.remove(0);
                            z.remove(0);
                        }
                        x.add(latest_data[0]);
                        y.add(latest_data[1]);
                        z.add(latest_data[2]);


                    }
                });
            }
        };

        PebbleKit.registerReceivedDataHandler(this, receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }
}
