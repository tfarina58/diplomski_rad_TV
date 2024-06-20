package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WelcomeActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Language language;
    Theme theme;
    String guest = "";
    Timestamp to;
    boolean skipFunction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));

        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();

        this.setupTimer();
        this.getGuestFromFirebase();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        // Enter button
        if (keyCode == 23) {
            skipFunction = true;

            Intent i = new Intent(getApplicationContext(), EstateListActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    void getGuestFromFirebase() {
        String estateId = this.sharedPreferencesService.getEstateId();
        if (estateId.isEmpty()) return;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference query = firestore.collection("estates").document(estateId);

        query.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                    ArrayList<HashMap<String, Object>> guests = (ArrayList<HashMap<String, Object>>)queryDocumentSnapshots.get("guests"); // ArrayList<HashMap<String, Object>>
                    if (guests == null || guests.size() == 0) return;

                    int guestsLength = guests.size();
                    for (int i = 0; i < guestsLength; ++i) {
                        Timestamp fromStamp = (Timestamp)(guests.get(i).getOrDefault("from", null));
                        Timestamp toStamp = (Timestamp)(guests.get(i).getOrDefault("to", null));
                        if (fromStamp == null || toStamp == null) continue;

                        long epochFrom = fromStamp.getSeconds();
                        long epochNow = System.currentTimeMillis() / 1000;
                        long epochTo = toStamp.getSeconds();

                        if (epochNow - epochFrom >= 0 && epochTo - epochNow > 0) {
                            guest = (String)(guests.get(i).getOrDefault("name", ""));
                            to = toStamp;
                            break;
                        }
                    }

                    setContentView(R.layout.activity_welcome);
                    displayGuestName();
                    setupHintText();
                }
            });
    }

    public void displayGuestName() {
        TextView welcomeText = findViewById(R.id.welcomeText);
        ImageView welcomeBackground = findViewById(R.id.welcomeBackground);
        if (welcomeText == null || welcomeBackground == null) return;

        switch (language) {
            case german:
                welcomeText.setText(getResources().getString(R.string.welcome_de) + " " + guest);
                break;
            case croatian:
                welcomeText.setText(getResources().getString(R.string.welcome_hr) + " " + guest);
                break;
            default:
                welcomeText.setText(getResources().getString(R.string.welcome_en) + " " + guest);
        }

        if (this.theme == Theme.dark) welcomeBackground.setBackground(getResources().getDrawable(R.color.dark_theme));
        else if (this.theme == Theme.light) welcomeBackground.setBackground(getResources().getDrawable(R.color.light_theme));
    }

    public void setupHintText() {
        TextView hintText = findViewById(R.id.hintText);
        if (hintText == null) return;

        switch (language) {
            case german:
                hintText.setText(getResources().getString(R.string.hint_skip_welcome_de));
                break;
            case croatian:
                hintText.setText(getResources().getString(R.string.hint_skip_welcome_hr));
                break;
            default:
                hintText.setText(getResources().getString(R.string.hint_skip_welcome_en));
        }
    }

    public void setupTimer() {
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    if (skipFunction) return;

                    long toInEpoch = 0;
                    long now = new Date().getTime();
                    if (to != null) toInEpoch = (to.getSeconds() - 86400) * 1000; // (to - 1 day) in milliseconds

                    if (to != null && toInEpoch < now) startActivity(new Intent(getApplicationContext(), RatingActivity.class));
                    else startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                }
            },
            6000
        );
    }
}