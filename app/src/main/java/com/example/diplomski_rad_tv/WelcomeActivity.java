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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WelcomeActivity extends Activity {

    Theme theme;
    Language language;
    String guest = "";
    Timestamp to;
    boolean skipFunction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        this.language = this.setLanguage(sharedPreferences);
        this.theme = this.setTheme(sharedPreferences);
        this.setGuest(sharedPreferences);

        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    if (skipFunction) return;

                    long toInEpoch = 0;
                    long now = new Date().getTime();
                    if (to != null) toInEpoch = (to.getSeconds() - 86400) * 1000; // (to - 1 day) in milliseconds

                    if (to != null && toInEpoch < now) {
                        Intent i = new Intent(getApplicationContext(), RatingActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                        startActivity(i);
                    }
                }
            },
            5000
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Enter button
        if (keyCode == 23) {
            skipFunction = true;

            Intent i = new Intent(getApplicationContext(), EstateListActivity.class);
            startActivity(i);
            return true;
        }
        return false;
    }

    Language setLanguage(SharedPreferences sharedPreferences) {
        String language = sharedPreferences.getString("language", "en");

        switch (language) {
            case "de":
                return Language.germany;
            case "hr":
                return Language.croatia;
            default:
                return Language.united_kingdom;
        }
    }

    Theme setTheme(SharedPreferences sharedPreferences) {
        String theme = sharedPreferences.getString("theme", "dark");

        if (theme.equals("light")) return Theme.light;
        return Theme.dark;
    }

    void setGuest(SharedPreferences sharedPreferences) {
        String estateId = sharedPreferences.getString("estateId", "");
        if (estateId.isEmpty()) return;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference query = firestore.collection("estates").document(estateId);

        query.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                    HashMap<String, Object> variables = (HashMap<String, Object>)queryDocumentSnapshots.get("variables");
                    if (variables == null || variables.size() == 0) return;

                    ArrayList<HashMap<String, Object>> guests = (ArrayList<HashMap<String, Object>>)variables.get("guests"); // ArrayList<HashMap<String, Object>>
                    if (guests == null || guests.size() == 0) return;

                    int guestsLength = guests.size();
                    Timestamp currentTimestamp = Timestamp.now();
                    for (int i = 0; i < guestsLength; ++i) {
                        Timestamp fromStamp = (Timestamp)(guests.get(i).getOrDefault("from", null));
                        Timestamp toStamp = (Timestamp)(guests.get(i).getOrDefault("to", null));
                        if (fromStamp == null || toStamp == null) continue;

                        if (fromStamp.compareTo(currentTimestamp) < 0 && toStamp.compareTo(currentTimestamp) > 0) {
                            guest = (String)(guests.get(i).getOrDefault("name", ""));
                            to = toStamp;
                            break;
                        }
                    }

                    setContentView(R.layout.activity_welcome);

                    TextView welcomeText = findViewById(R.id.welcomeText);
                    ImageView welcomeBackground = findViewById(R.id.welcomeBackground);

                    switch (language) {
                        case united_kingdom:
                            welcomeText.setText(getResources().getString(R.string.welcome_en) + " " + guest);
                            break;
                        case germany:
                            welcomeText.setText(getResources().getString(R.string.welcome_de) + " " + guest);
                            break;
                        case croatia:
                            welcomeText.setText(getResources().getString(R.string.welcome_hr) + " " + guest);
                            break;
                    }

                    switch (theme) {
                        case dark:
                            welcomeBackground.setBackground(getResources().getDrawable(R.color.dark_theme));
                            break;
                        case light:
                            welcomeBackground.setBackground(getResources().getDrawable(R.color.light_theme));
                            break;
                    }
                }
            });
    }
}