package ibrahim.ansari.LearnMotion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private Button pebButton, myoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pebButton = (Button) findViewById(R.id.pebble_button);
        myoButton = (Button) findViewById(R.id.myo_button);

        pebButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PebbleActivity.class);
                startActivity(i);
            }

        });

        myoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MyoActivity.class);
                startActivity(i);
            }

        });

    }
}
