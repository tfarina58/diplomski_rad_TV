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

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RatingActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Language language;
    Theme theme;
    Clock format;
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
        this.format = sharedPreferencesService.getClockFormat();

        this.focusedView = findViewById(R.id.ratingButton);
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
        this.getRatings();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode == 4) {
            // startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
        } else if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = RatingNavigation.navigateOverActivity(focusedView.getId(), keyCode - 19);
            if (newFocusedViewId == 0) return false;

            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = RatingNavigation.getRowWithId(oldFocusedViewId);
            this.updateView(row);

            // Add focus to new View
            row = RatingNavigation.getRowWithId(newFocusedViewId);
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

                if (!languageButton.hasOnClickListeners()) {
                    languageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            language = language.next();

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
                }
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

                if (!textClock.hasOnClickListeners()) {
                    textClock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            format = format.next();

                            if (Objects.requireNonNull(format) == Clock.h12) ((TextClock) focusedView).setFormat12Hour("hh:mm:ss a");
                            else ((TextClock) focusedView).setFormat12Hour("HH:mm:ss");
                        }
                    });
                }
                break;
            case 3:
                EditText ratingContent = findViewById(R.id.ratingContent);
                RatingNavigation.setupRatingContentField(getApplicationContext(), ratingContent, this.focusedView, this.language, this.theme);
                break;
            case 4:
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                RatingNavigation.setupRatingBarField(getApplicationContext(), ratingBar, this.focusedView, this.theme);
            case 5:
                Button cancelButton = findViewById(R.id.cancelButton);
                RatingNavigation.setupCancelButton(getApplicationContext(), cancelButton, this.focusedView, this.language);

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
                Button ratingButton = findViewById(R.id.ratingButton);
                RatingNavigation.setupRatingButton(getApplicationContext(), ratingButton, this.focusedView, this.language);

                if (!ratingButton.hasOnClickListeners()) {
                    ratingButton.setOnClickListener(new View.OnClickListener() {
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

    void saveRatings(EditText content, RatingBar grade) {
        if (content == null || grade == null) return;

        String ratingContent = content.getText().toString();
        int ratingBar = (int)grade.getRating();
        long now = System.currentTimeMillis() / 1000;
        Timestamp created =  new Timestamp(Instant.ofEpochSecond(now));

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
}
