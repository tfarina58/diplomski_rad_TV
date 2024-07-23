package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextClock;
import android.widget.TextView;
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
import com.google.firebase.firestore.Query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyRatingActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Language language;
    Theme theme;
    Clock format;
    View focusedView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<HashMap<String, Object>> ratings = new ArrayList<>();
    boolean anonymousCheckbox = true;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.language = sharedPreferencesService.getLanguage();
        this.theme = sharedPreferencesService.getTheme();
        this.format = sharedPreferencesService.getClockFormat();

        this.focusedView = findViewById(R.id.ratingSubmitButton);
        this.focusedView.requestFocus();

        {
            ConstraintLayout background = findViewById(R.id.main);
            setupBackground(getApplicationContext(), background, this.theme);
        }

        {
            TextView titleView = findViewById(R.id.ratingTitle);
            setupTitle(getApplicationContext(), titleView, this.language, this.theme);
        }

        this.setupContent();
        this.getCurrentUserName();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode == 4) {
            // startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
        } else if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = MyRatingNavigation.navigateOverActivity(focusedView.getId(), keyCode - 19);
            if (newFocusedViewId == 0) return false;

            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = MyRatingNavigation.getRowWithId(oldFocusedViewId);
            this.updateView(row);

            // Add focus to new View
            row = MyRatingNavigation.getRowWithId(newFocusedViewId);
            this.updateView(row);

        }
        return true;
    }

    void setupBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.dark) background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
        else if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
    }


    void setupTitle(Context ctx, TextView title, Language language, Theme theme) {
        if (title == null) return;

        switch (language) {
            case german:
                title.setText(R.string.rating_de);
                break;
            case croatian:
                title.setText(R.string.rating_hr);
                break;
            default:
                title.setText(R.string.rating_en);
        }

        if (theme == Theme.dark) title.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        else if (theme == Theme.light) title.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
    }

    void updateView(int row) {
        switch (row) {
            case 0:
                Button languageButton = findViewById(R.id.languageButton);
                ImageView languageIcon = findViewById(R.id.languageIcon);
                LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language);

                languageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        language = language.next();
                        sharedPreferencesService.setLanguage(language);

                        {
                            TextView titleView = findViewById(R.id.ratingTitle);
                            setupTitle(getApplicationContext(), titleView, language, theme);
                        }

                        updateView(0);
                        updateView(1);
                        updateView(3);
                        updateView(5);
                        updateView(6);
                    }
                });
                break;
            case 1:
                Button themeButton = findViewById(R.id.themeButton);
                ImageView themeIcon = findViewById(R.id.themeIcon);
                ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);

                if (!themeButton.hasOnClickListeners()) {
                    themeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            theme = theme.next();
                            sharedPreferencesService.setTheme(theme);

                            {
                                ConstraintLayout background = findViewById(R.id.main);
                                setupBackground(getApplicationContext(), background, theme);
                            }

                            {
                                TextView titleView = findViewById(R.id.ratingTitle);
                                setupTitle(getApplicationContext(), titleView, language, theme);
                            }

                            updateView(3);
                            updateView(4);
                        }
                    });
                }
                break;
            case 2:
                TextClock textClock = findViewById(R.id.textClock);
                ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);

                textClock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        format = format.next();
                        sharedPreferencesService.setClockFormat(format);

                        if (Objects.requireNonNull(format) == Clock.h12) ((TextClock) focusedView).setFormat12Hour("hh:mm:ss a");
                        else ((TextClock) focusedView).setFormat12Hour("HH:mm:ss");
                    }
                });
                break;
            case 3:
                EditText ratingContent = findViewById(R.id.ratingContent);
                MyRatingNavigation.setupRatingContentField(getApplicationContext(), ratingContent, this.focusedView, this.language, this.theme);
                break;
            case 4:
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                MyRatingNavigation.setupRatingBarField(getApplicationContext(), ratingBar, this.focusedView, this.theme);
            case 5:
                Button cancelButton = findViewById(R.id.cancelButton);
                MyRatingNavigation.setupCancelButton(getApplicationContext(), cancelButton, this.focusedView, this.language);

                if (!cancelButton.hasOnClickListeners()) {
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                        }
                    });
                }
                break;
            case 6:
                Button ratingSubmitButton = findViewById(R.id.ratingSubmitButton);
                MyRatingNavigation.setupRatingSubmitButton(getApplicationContext(), ratingSubmitButton, this.focusedView, this.language);

                if (!ratingSubmitButton.hasOnClickListeners()) {
                    ratingSubmitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            EditText content = findViewById(R.id.ratingContent);
                            RatingBar grade = findViewById(R.id.ratingBar);

                            saveRatings(content, grade);
                        }
                    });
                }
                break;
        }
    }

    void setupContent() {
        for (int i = 0; i <= 6; ++i) this.updateView(i);
    }

    void saveRatings(EditText content, RatingBar grade) {
        if (content == null || grade == null) return;

        String ratingContent = content.getText().toString();
        int ratingBar = (int)grade.getRating();
        long now = System.currentTimeMillis() / 1000;
        Timestamp created =  new Timestamp(Instant.ofEpochSecond(now));

        HashMap<String, Object> currentRating = new HashMap<>();
        currentRating.put("comment", ratingContent);
        currentRating.put("rating", ratingBar);
        currentRating.put("created", created);
        currentRating.put("estateId", this.sharedPreferencesService.getEstateId());
        currentRating.put("username",  this.anonymousCheckbox ? "" : username);

        firestore.collection("ratings").add(currentRating)
            .addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    sharedPreferencesService.setLastRatingDate(now);
                    startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    switch (language) {
                        case german:
                            Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.cannot_uploading_rating_de), Toast.LENGTH_LONG).show();
                            break;
                        case croatian:
                            Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.cannot_uploading_rating_hr), Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.cannot_uploading_rating_en), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });
    }

    void getCurrentUserName() {
        DocumentReference ref = firestore.collection("estates").document(this.sharedPreferencesService.getEstateId());

        ref.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                    ArrayList<HashMap<String, Object>> guests = (ArrayList<HashMap<String, Object>>)queryDocumentSnapshots.get("guests");
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
                            username = (String)(guests.get(i).getOrDefault("name", ""));
                            break;
                        }
                    }
                }
            });
    }
}
