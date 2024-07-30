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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyRatingActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Language language;
    Theme theme;
    Clock format;
    View focusedView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String username = "";
    String ratingId = "";
    String focusedLayout = ""; // "rating" or ""
    View layoutFocusedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_rating);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.language = sharedPreferencesService.getLanguage();
        this.theme = sharedPreferencesService.getTheme();
        this.format = sharedPreferencesService.getClockFormat();
        this.ratingId = sharedPreferencesService.getRatingId();

        this.focusedView = findViewById(R.id.ratingContent);
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
        this.getRatingFromFirebase();
        if (username.isEmpty()) this.getCurrentUserName();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);

        if (this.focusedLayout.equals("rating")) {
            int oldFocusedViewId = this.layoutFocusedView.getId();

            if (keyCode >= 19 && keyCode <= 22) {
                int newFocusedViewId = ChooseRatingLayoutNavigation.navigateOverLayout(oldFocusedViewId, keyCode - 19);

                if (newFocusedViewId == 0) return true;

                this.layoutFocusedView = findViewById(newFocusedViewId);
                this.layoutFocusedView.requestFocus();

                // Remove focus from old View
                int row = ChooseRatingLayoutNavigation.getRowWithId(oldFocusedViewId);
                updateLayoutView(row);

                // Add focus to new View
                row = ChooseRatingLayoutNavigation.getRowWithId(newFocusedViewId);
                updateLayoutView(row);

            } else if (keyCode == 4) {
                FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                ConstraintLayout background = findViewById(R.id.ratingMain);
                TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                Button showRatingsButton = findViewById(R.id.showRatingsButton);
                Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                Button submitRatingButton = findViewById(R.id.submitRatingButton);

                this.focusedLayout = "";
                this.layoutFocusedView = null;
                this.focusedView.requestFocus();

                this.setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, false, this.layoutFocusedView, this.language, this.theme);
            }
            // Enter button
            else if (keyCode == 23) this.layoutFocusedView.callOnClick();

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode == 4) {
            /*if (!id.isEmpty()) {
                startActivity(new Intent(getApplicationContext(), DescriptionActivity.class));
                return true;
            }*/

            String id = this.sharedPreferencesService.getCategoryId();
            if (!id.isEmpty()) {
                startActivity(new Intent(getApplicationContext(), ElementListActivity.class));
                return true;
            }

            startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));


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
                title.setText(R.string.your_rating_de);
                break;
            case croatian:
                title.setText(R.string.your_rating_hr);
                break;
            default:
                title.setText(R.string.your_rating_en);
        }

        if (theme == Theme.dark) title.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        else if (theme == Theme.light) title.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
    }

    void updateView(int row) {
        switch (row) {
            case 0:
                Button headerButton = findViewById(R.id.ratingButton);
                ImageView headerIcon = findViewById(R.id.ratingIcon);

                RatingHeaderButton.setupRatingButton(getApplicationContext(), headerButton, headerIcon, true, this.focusedView, this.language, this.theme);

                headerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        focusedLayout = "rating";

                        FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                        ConstraintLayout background = findViewById(R.id.ratingMain);
                        TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                        Button showRatingsButton = findViewById(R.id.showRatingsButton);
                        Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                        Button submitRatingButton = findViewById(R.id.submitRatingButton);

                        layoutFocusedView = showRatingsButton;

                        setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, true, layoutFocusedView, language, theme);
                    }
                });
                break;
            case 1:
                Button languageButton = findViewById(R.id.languageButton);
                ImageView languageIcon = findViewById(R.id.languageIcon);
                LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);

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
                        updateView(2);
                        updateView(4);
                        updateView(6);
                        updateView(7);
                    }
                });
                break;
            case 2:
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

                            setupHeaderButtons();

                            updateView(4);
                            updateView(5);
                            updateView(6);
                            updateView(7);
                        }
                    });
                }
                break;
            case 3:
                TextClock textClock = findViewById(R.id.textClock);
                ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);

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
            case 4:
                EditText ratingContent = findViewById(R.id.ratingContent);
                MyRatingNavigation.setupRatingContentField(getApplicationContext(), ratingContent, this.focusedView, this.language, this.theme);
                break;
            case 5:
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                MyRatingNavigation.setupRatingBarField(getApplicationContext(), ratingBar, this.focusedView, this.theme, ratingBar.getRating());
            case 6:
                Button cancelButton = findViewById(R.id.cancelButton);
                MyRatingNavigation.setupCancelButton(getApplicationContext(), cancelButton, this.focusedView, this.language, this.theme);

                if (!cancelButton.hasOnClickListeners()) {
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                        }
                    });
                }
                break;
            case 7:
                Button ratingSubmitButton = findViewById(R.id.ratingSubmitButton);
                MyRatingNavigation.setupRatingSubmitButton(getApplicationContext(), ratingSubmitButton, this.focusedView, this.language, this.theme);

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
        for (int i = 0; i <= 7; ++i) this.updateView(i);
    }

    void saveRatings(EditText content, RatingBar grade) {
        if (content == null || grade == null) return;

        String ratingContent = content.getText().toString();
        int rating = (int)grade.getRating();
        long now = System.currentTimeMillis() / 1000;
        Timestamp created =  new Timestamp(Instant.ofEpochSecond(now));

        Map<String, Object> currentRating = new HashMap<>();
        currentRating.put("comment", ratingContent);
        currentRating.put("rating", rating);
        currentRating.put("created", created);
        currentRating.put("estateId", this.sharedPreferencesService.getEstateId());
        currentRating.put("username", username);

        if (ratingId.isEmpty()) {
            firestore.collection("ratings").add(currentRating)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        DocumentReference ref = (DocumentReference)o;
                        String ratingId = ref.getId();

                        sharedPreferencesService.setRatingId(ratingId);

                        switch (language) {
                            case german:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_created_rating_de), Toast.LENGTH_LONG).show();
                                break;
                            case croatian:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_created_rating_hr), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_created_rating_en), Toast.LENGTH_LONG).show();
                                break;
                        }

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
        } else {
            firestore.collection("ratings").document(ratingId).update(currentRating)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        switch (language) {
                            case german:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_updated_rating_de), Toast.LENGTH_LONG).show();
                                break;
                            case croatian:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_updated_rating_hr), Toast.LENGTH_LONG).show();;
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.successfully_updated_rating_en), Toast.LENGTH_LONG).show();;
                        }

                        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        switch (language) {
                            case german:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.error_while_updating_rating_de), Toast.LENGTH_LONG).show();
                                break;
                            case croatian:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.error_while_updating_rating_hr), Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.error_while_updating_rating_en), Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
        }
    }

    void getRatingFromFirebase() {
        if (ratingId.isEmpty()) return;

        DocumentReference ref = firestore.collection("ratings").document(ratingId);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                        String comment = queryDocumentSnapshots.getString("comment");
                        long rating = (long)queryDocumentSnapshots.get("rating");
                        String tmpUsername = queryDocumentSnapshots.getString("username");

                        EditText ratingContent = findViewById(R.id.ratingContent);
                        ratingContent.setText(comment);

                        RatingBar ratingBar = findViewById(R.id.ratingBar);
                        ratingBar.setRating(rating);

                        username = tmpUsername;
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

                    // if (username == null || username.isEmpty()) startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
                }
            });
    }

    public void updateLayoutView(int row) {
        switch (row) {
            case 0:
                Button showRatingsButton = findViewById(R.id.showRatingsButton);

                ChooseRatingLayout.setupShowRatingButton(getApplicationContext(), showRatingsButton, this.layoutFocusedView, this.language, this.theme);
                break;
            case 1:
                Button cancelButtonRating = findViewById(R.id.cancelButtonRating);

                ChooseRatingLayout.setupCancelButton(getApplicationContext(), cancelButtonRating, this.layoutFocusedView, this.language, this.theme);
                break;
            case 2:
                Button submitRatingButton = findViewById(R.id.submitRatingButton);

                ChooseRatingLayout.setupMyRatingButton(getApplicationContext(), submitRatingButton, this.layoutFocusedView, this.language, this.theme);
                break;
        }

    }

    void setupChooseRatingLayout(Context ctx, FrameLayout chooseRatingLayout, ConstraintLayout background, TextView chooseRatingTitle, Button showRatingsButton, Button cancelButtonRating, Button submitRatingButton, boolean visible, View layoutFocusedView, Language language, Theme theme) {
        if (chooseRatingLayout == null || background == null || chooseRatingTitle == null || showRatingsButton == null || cancelButtonRating == null || submitRatingButton == null) return;

        if (!visible) {
            chooseRatingLayout.setVisibility(View.INVISIBLE);
            return;
        }
        chooseRatingLayout.setVisibility(View.VISIBLE);

        ChooseRatingLayout.setupLayoutTitle(ctx, chooseRatingTitle, language, theme);
        ChooseRatingLayout.setupLayoutBackground(ctx, background, theme);
        ChooseRatingLayout.setupShowRatingButton(ctx, showRatingsButton, layoutFocusedView, language, theme);
        showRatingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RatingListActivity.class));
            }
        });

        ChooseRatingLayout.setupCancelButton(ctx, cancelButtonRating, layoutFocusedView, language, theme);
        cancelButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusedLayout = "";

                FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                ConstraintLayout background = findViewById(R.id.ratingMain);
                TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                Button showRatingsButton = findViewById(R.id.showRatingsButton);
                Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                Button submitRatingButton = findViewById(R.id.submitRatingButton);

                // layoutFocusedView = null;

                setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, false, layoutFocusedView, language, theme);

            }
        });

        ChooseRatingLayout.setupMyRatingButton(getApplicationContext(), submitRatingButton, layoutFocusedView, language, theme);
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyRatingActivity.class));
            }
        });
    }

    void setupHeaderButtons() {
        Button ratingButton = findViewById(R.id.ratingButton);
        ImageView ratingIcon = findViewById(R.id.ratingIcon);

        RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);

        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);

        Button themeButton = findViewById(R.id.themeButton);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);

        TextClock textClock = findViewById(R.id.textClock);

        ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
    }
}
