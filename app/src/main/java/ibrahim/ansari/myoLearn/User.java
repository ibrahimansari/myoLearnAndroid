package ibrahim.ansari.myoLearn;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username, email, UID, firebaseRef;
    private List<MyoData> recordings = new ArrayList<>();

    public User(String UID) {
        this.UID = UID;
    }

    public String getFirebaseRef() {
        return firebaseRef;
    }

    public String getUID() {
        return UID;
    }

    public List<MyoData> getRecordings() {
        return recordings;

    }

    public String getEmail() {

        return email;
    }

    public String getUsername() {

        return username;
    }
}
