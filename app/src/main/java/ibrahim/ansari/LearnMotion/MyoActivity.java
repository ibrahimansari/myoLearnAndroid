package ibrahim.ansari.LearnMotion;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyoActivity extends ListActivity {

    private Firebase mainRef = null;
    private List<String> dataList = new ArrayList<>();
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myo);

        Firebase.setAndroidContext(this);
        mainRef = new Firebase("https://myosport.firebaseio.com/duttaoindril/recordings");

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
                startActivity(new Intent(MyoActivity.this, MyoSession.class));
            }
        });
    }
}
