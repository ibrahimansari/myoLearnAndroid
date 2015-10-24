package ibrahim.ansari.LearnMotion;

import android.app.Application;

import com.firebase.client.Firebase;

public class myoLearn extends Application {
    @Override
    public void onCreate() {
        Firebase.setAndroidContext(this);
        super.onCreate();
    }
}

