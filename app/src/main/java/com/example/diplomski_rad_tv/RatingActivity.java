package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsProvider;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.io.ObjectStreamException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class RatingActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Language language;
    Theme theme;
    Clock format = Clock.h12;
    View focusedView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<HashMap<String, Object>> ratings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.language = sharedPreferencesService.getLanguage();
        this.theme = sharedPreferencesService.getTheme();
        this.focusedView = findViewById(R.id.ratingButton);

        this.setupBackground();
        this.setupContent();
        this.getRatings();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode == 4) {
            this.focusedView.clearFocus();
            this.focusedView.setFocusable(false);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(this.focusedView.getWindowToken(), 0);
            }
            // TODO

        } else if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = RatingNavigation.navigateOverActivity(focusedView.getId(), keyCode - 19);
            if (newFocusedViewId == 0) return false;

            focusedView = findViewById(newFocusedViewId);

            // Remove focus from old View
            int row = RatingNavigation.getRowWithId(oldFocusedViewId);
            this.updateView(row);

            // Add focus to new View
            row = RatingNavigation.getRowWithId(newFocusedViewId);
            this.updateView(row);
        }
        // Enter button
        else if (keyCode == 23) {
            if (oldFocusedViewId == R.id.languageButton) {
                language = language.next();
                focusedView = findViewById(R.id.languageButton);

                this.updateView(0);
                this.updateView(1);
                this.updateView(3);
                this.updateView(5);
                this.updateView(6);
            } else if (oldFocusedViewId == R.id.themeButton) {
                theme = theme.next();
                focusedView = findViewById(R.id.themeButton);

                this.setupBackground();
                this.updateView(3);
                this.updateView(4);
            } else if (oldFocusedViewId == R.id.textClock) {
                format = format.next();
                focusedView = findViewById(R.id.textClock);
                switch (format) {
                    case h24:
                        ((TextClock)focusedView).setFormat12Hour("HH:mm:ss");
                        break;
                    case h12:
                        ((TextClock)focusedView).setFormat12Hour("hh:mm:ss a");
                        break;
                }
            } else if (oldFocusedViewId == R.id.ratingContent) {
                EditText email = findViewById(R.id.ratingContent);
                email.setFocusable(true);
                email.requestFocus();
                // TODO

            } else if (oldFocusedViewId == R.id.ratingBar) {
                RatingBar email = findViewById(R.id.ratingBar);
                email.setFocusable(true);
                email.requestFocus();
                // TODO

            } else if (oldFocusedViewId == R.id.cancelButton) {
                startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
            } else if (oldFocusedViewId == R.id.ratingButton) {
                this.saveRatings();
            }
        }
        return true;
    }

    void getRatings() {
        DocumentReference docRef = firestore.collection("estates").document(this.sharedPreferencesService.getEstateId());

        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot docSnap = task.getResult();
                        ratings = (ArrayList<HashMap<String, Object>>)docSnap.get("ratings");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG);
                    }
                });
    }

    void saveRatings() {
        String ratingContent = ((EditText)findViewById(R.id.ratingContent)).getText().toString();
        int ratingBar = (int)((RatingBar)findViewById(R.id.ratingBar)).getRating();
        Timestamp created =  new Timestamp(Instant.ofEpochSecond(System.currentTimeMillis() / 1000));

        HashMap<String, Object> currentRating = new HashMap<>();
        currentRating.put("content", ratingContent);
        currentRating.put("grade", ratingBar);
        currentRating.put("created", created);

        ratings.add(currentRating);

        DocumentReference docRef = firestore.collection("estates").document(this.sharedPreferencesService.getEstateId());

        docRef.update("ratings", this.ratings)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG);
                        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG);
                    }
                });
    }

    void setupBackground() {
        ConstraintLayout layout = findViewById(R.id.main);

        if (this.theme == Theme.dark) layout.setBackground(getResources().getDrawable(R.color.dark_theme));
        else if (this.theme == Theme.light) layout.setBackground(getResources().getDrawable(R.color.light_theme));
    }

    void updateView(int row) {
        switch (row) {
            case 0:
                Button languageButton = findViewById(R.id.languageButton);
                ImageView languageIcon = findViewById(R.id.languageIcon);
                RatingNavigation.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
                break;
            case 1:
                Button themeButton = findViewById(R.id.themeButton);
                ImageView themeIcon = findViewById(R.id.themeIcon);
                RatingNavigation.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
                break;
            case 2:
                TextClock textClock = findViewById(R.id.textClock);
                RatingNavigation.setupTextClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
                break;
            case 3:
                EditText ratingContent = findViewById(R.id.ratingContent);
                RatingNavigation.setupRatingContentField(getApplicationContext(), ratingContent, this.focusedView, this.language, this.theme);
                break;
            case 4:
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                RatingNavigation.setupRatingBarField(getApplicationContext(), ratingBar, this.focusedView, this.theme);
                break;
            case 5:
                Button cancelButton = findViewById(R.id.cancelButton);
                RatingNavigation.setupCancelButton(getApplicationContext(), cancelButton, this.focusedView);
                break;
            case 6:
                Button ratingButton = findViewById(R.id.ratingButton);
                int id = ratingButton.getId();
                RatingNavigation.setupRatingButton(getApplicationContext(), ratingButton, this.focusedView);
                break;
        }
    }

    void setupContent() {
        for (int i = 0; i <= 6; ++i) this.updateView(i);
    }
}
