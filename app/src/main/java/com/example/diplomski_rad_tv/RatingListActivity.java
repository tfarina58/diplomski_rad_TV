package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RatingListActivity extends Activity {
    String estateId;
    FirebaseFirestore firestore;
    SharedPreferencesService sharedPreferencesService;
    Rating[] ratings;
    Language language;
    Theme theme;
    Clock format;
    View focusedView;
    boolean loadingInProgress = true;
    String focusedLayout = ""; // "rating" or ""
    View layoutFocusedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating_list);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.estateId = this.sharedPreferencesService.getEstateId();
        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();
        this.format = this.sharedPreferencesService.getClockFormat();

        this.firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("ratings")
                .whereEqualTo("estateId", estateId)
                .orderBy("created", Query.Direction.DESCENDING)
                .limit(10);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 0;
                        ratings = new Rating[queryDocumentSnapshots.size()];
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String id = document.getId();
                            String estateId = document.getString("estateId");
                            Timestamp created = (Timestamp) document.get("created");
                            String comment = document.getString("comment");
                            double rating = document.getDouble("rating");
                            String username = document.getString("username");
                            ratings[i] = new Rating(id, estateId, comment, rating, created, username);
                            i++;
                        }
                        loadingInProgress = false;
                        setNewContentView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ratings = new Rating[0];
                        loadingInProgress = false;
                        setNewContentView();
                    }
                });


        this.ratings = new Rating[0];
        setNewContentView();
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

                return false;
            }
            // Enter button
            else if (keyCode == 23) this.layoutFocusedView.callOnClick();

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        int oldFocusedViewId = this.focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode >= 21 && keyCode <= 22) {
            // if (specialCaseNavigation(oldFocusedViewId, keyCode - 19)) return true;

            int newFocusedViewId = RatingListNavigation.navigateOverActivity(oldFocusedViewId, keyCode - 19);

            if (newFocusedViewId == 0 || !checkViewExistence(newFocusedViewId)) return false;

            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = RatingListNavigation.getRowWithId(oldFocusedViewId);
            updateView(row);

            // Add focus to new View
            row = RatingListNavigation.getRowWithId(newFocusedViewId);
            updateView(row);
        }
        // Enter button
        else if (keyCode == 23) this.focusedView.callOnClick();
        else if (keyCode == 4) startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
        return true;
    }

    void setNewContentView() {
        if (this.focusedView == null) {
            this.focusedView = findViewById(R.id.languageButton);
            this.focusedView.requestFocus();
        }

        {
            ConstraintLayout background = findViewById(R.id.background);
            setupBackground(getApplicationContext(), background, theme);
        }

        {
            TextView ratingTitle = findViewById(R.id.ratingTitle);
            setRatingTitle(getApplicationContext(), ratingTitle, language, theme);
        }

        {
            Button button;
            ImageView icon;

            // Rating button
            button = findViewById(R.id.ratingButton);
            icon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), button, icon, true, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (focusedLayout.equals("rating")) {
                        findViewById(layoutFocusedView.getId()).performClick();
                        return;
                    }
                    focusedLayout = "rating";

                    FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                    ConstraintLayout background = findViewById(R.id.ratingMain);
                    TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                    Button showRatingsButton = findViewById(R.id.showRatingsButton);
                    Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                    Button submitRatingButton = findViewById(R.id.submitRatingButton);

                    layoutFocusedView = showRatingsButton;
                    layoutFocusedView.requestFocus();

                    setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, true, layoutFocusedView, language, theme);
                }
            });

            // Language button
            button = findViewById(R.id.languageButton);
            icon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), button, icon, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    sharedPreferencesService.setLanguage(language);

                    TextView ratingTitle = findViewById(R.id.ratingTitle);
                    setRatingTitle(getApplicationContext(), ratingTitle, language, theme);

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, ratings.length);

                    updateView(0);
                    updateView(1);
                    updateView(2);
                    if (ratings.length > 0 && ratings[0].username.isEmpty()) updateView(4);
                    if (ratings.length > 1 && ratings[1].username.isEmpty()) updateView(5);
                    if (ratings.length > 2 && ratings[2].username.isEmpty()) updateView(6);
                    if (ratings.length > 3 && ratings[3].username.isEmpty()) updateView(7);
                    if (ratings.length > 4 && ratings[4].username.isEmpty()) updateView(8);
                    if (ratings.length > 5 && ratings[5].username.isEmpty()) updateView(9);
                    if (ratings.length > 6 && ratings[6].username.isEmpty()) updateView(10);
                    if (ratings.length > 7 && ratings[7].username.isEmpty()) updateView(11);
                    if (ratings.length > 8 && ratings[8].username.isEmpty()) updateView(12);
                    if (ratings.length > 9 && ratings[9].username.isEmpty()) updateView(13);
                }
            });

            // Theme button
            button = findViewById(R.id.themeButton);
            icon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), button, icon, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme = theme.next();
                    sharedPreferencesService.setTheme(theme);

                    // updateView(2);

                    setupHeaderButtons();

                    TextView ratingTitle = findViewById(R.id.ratingTitle);
                    setRatingTitle(getApplicationContext(), ratingTitle, language, theme);

                    ConstraintLayout background = findViewById(R.id.background);
                    setupBackground(getApplicationContext(), background, theme);

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, ratings.length);

                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    ProgressBarLoader.manageProgressBar(getApplicationContext(), progressBar, theme, loadingInProgress);

                    if (ratings.length > 0 && ratings[0].username.isEmpty()) updateView(4);
                    if (ratings.length > 1 && ratings[1].username.isEmpty()) updateView(5);
                    if (ratings.length > 2 && ratings[2].username.isEmpty()) updateView(6);
                    if (ratings.length > 3 && ratings[3].username.isEmpty()) updateView(7);
                    if (ratings.length > 4 && ratings[4].username.isEmpty()) updateView(8);
                    if (ratings.length > 5 && ratings[5].username.isEmpty()) updateView(9);
                    if (ratings.length > 6 && ratings[6].username.isEmpty()) updateView(10);
                    if (ratings.length > 7 && ratings[7].username.isEmpty()) updateView(11);
                    if (ratings.length > 8 && ratings[8].username.isEmpty()) updateView(12);
                    if (ratings.length > 9 && ratings[9].username.isEmpty()) updateView(13);
                }
            });
        }

        {
            // Clock button
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);

            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    format = format.next();
                    sharedPreferencesService.setClockFormat(format);

                    switch (format) {
                        case h24:
                            ((TextClock) focusedView).setFormat12Hour("HH:mm:ss");
                            break;
                        case h12:
                            ((TextClock) focusedView).setFormat12Hour("hh:mm:ss a");
                            break;
                    }
                }
            });
        }

        {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            ProgressBarLoader.manageProgressBar(getApplicationContext(), progressBar, theme, loadingInProgress);
        }

        {
            TextView centerText = findViewById(R.id.centerText);
            CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, ratings.length);
        }

        for (int i = 4; i < 14; ++i) this.updateView(i);
    }

    boolean checkViewExistence(int focusedViewId) {
        if (focusedViewId == R.id.ratingButton) return true;
        if (focusedViewId == R.id.languageButton) return true;
        if (focusedViewId == R.id.themeButton) return true;
        if (focusedViewId == R.id.textClock) return true;
        return false;
    }

    void updateView(int row) {
        if (row == 0) {
            Button ratingButton = findViewById(R.id.ratingButton);
            ImageView ratingIcon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);
        } else if (row == 1) {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
        } else if (row == 2) {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
        } else if (row == 3) {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
        } else if (row == 4) {
            Button ratingButton = findViewById(R.id.ratingButton1);
            TextView ratingUsername = findViewById(R.id.ratingUsername1);
            Button centerLine = findViewById(R.id.centerLine1);
            RatingBar ratingBar = findViewById(R.id.ratingBar1);
            TextView ratingComment =  findViewById(R.id.ratingComment1);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp1);

            if (this.ratings.length > 0) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[0], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 5) {
            Button ratingButton = findViewById(R.id.ratingButton2);
            TextView ratingUsername = findViewById(R.id.ratingUsername2);
            Button centerLine = findViewById(R.id.centerLine2);
            RatingBar ratingBar = findViewById(R.id.ratingBar2);
            TextView ratingComment =  findViewById(R.id.ratingComment2);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp2);

            if (this.ratings.length > 1) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[1], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 6) {
            Button ratingButton = findViewById(R.id.ratingButton3);
            TextView ratingUsername = findViewById(R.id.ratingUsername3);
            Button centerLine = findViewById(R.id.centerLine3);
            RatingBar ratingBar = findViewById(R.id.ratingBar3);
            TextView ratingComment =  findViewById(R.id.ratingComment3);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp3);

            if (this.ratings.length > 2) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[2], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 7) {
            Button ratingButton = findViewById(R.id.ratingButton4);
            TextView ratingUsername = findViewById(R.id.ratingUsername4);
            Button centerLine = findViewById(R.id.centerLine4);
            RatingBar ratingBar = findViewById(R.id.ratingBar4);
            TextView ratingComment =  findViewById(R.id.ratingComment4);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp4);

            if (this.ratings.length > 3) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[3], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 8) {
            Button ratingButton = findViewById(R.id.ratingButton5);
            TextView ratingUsername = findViewById(R.id.ratingUsername5);
            Button centerLine = findViewById(R.id.centerLine5);
            RatingBar ratingBar = findViewById(R.id.ratingBar5);
            TextView ratingComment =  findViewById(R.id.ratingComment5);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp5);

            if (this.ratings.length > 4) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[4], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 9) {
            Button ratingButton = findViewById(R.id.ratingButton6);
            TextView ratingUsername = findViewById(R.id.ratingUsername6);
            Button centerLine = findViewById(R.id.centerLine6);
            RatingBar ratingBar = findViewById(R.id.ratingBar6);
            TextView ratingComment =  findViewById(R.id.ratingComment6);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp6);

            if (this.ratings.length > 5) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[5], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 10) {
            Button ratingButton = findViewById(R.id.ratingButton7);
            TextView ratingUsername = findViewById(R.id.ratingUsername7);
            Button centerLine = findViewById(R.id.centerLine7);
            RatingBar ratingBar = findViewById(R.id.ratingBar7);
            TextView ratingComment =  findViewById(R.id.ratingComment7);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp7);

            if (this.ratings.length > 6) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[6], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 11) {
            Button ratingButton = findViewById(R.id.ratingButton8);
            TextView ratingUsername = findViewById(R.id.ratingUsername8);
            Button centerLine = findViewById(R.id.centerLine8);
            RatingBar ratingBar = findViewById(R.id.ratingBar8);
            TextView ratingComment =  findViewById(R.id.ratingComment8);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp8);

            if (this.ratings.length > 7) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[7], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 12) {
            Button ratingButton = findViewById(R.id.ratingButton9);
            TextView ratingUsername = findViewById(R.id.ratingUsername9);
            Button centerLine = findViewById(R.id.centerLine9);
            RatingBar ratingBar = findViewById(R.id.ratingBar9);
            TextView ratingComment =  findViewById(R.id.ratingComment9);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp9);

            if (this.ratings.length > 8) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[8], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        } else if (row == 13) {
            Button ratingButton = findViewById(R.id.ratingButton10);
            TextView ratingUsername = findViewById(R.id.ratingUsername10);
            Button centerLine = findViewById(R.id.centerLine10);
            RatingBar ratingBar = findViewById(R.id.ratingBar10);
            TextView ratingComment =  findViewById(R.id.ratingComment10);
            TextView ratingTimestamp =  findViewById(R.id.ratingTimestamp10);

            if (this.ratings.length > 9) RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, this.ratings[9], this.language, this.theme);
            else RatingCard.setupRatingCard(getApplicationContext(), ratingButton, ratingUsername, ratingBar, centerLine, ratingComment, ratingTimestamp, null, this.language, this.theme);
        }
    }

    void setRatingTitle(Context ctx, TextView ratingTitle, Language language, Theme theme) {
        if (ratingTitle == null) return;

        switch (language) {
            case german:
                ratingTitle.setText(ContextCompat.getString(ctx, R.string.ratings_de));
                break;
            case croatian:
                ratingTitle.setText(ContextCompat.getString(ctx, R.string.ratings_hr));
                break;
            default:
                ratingTitle.setText(ContextCompat.getString(ctx, R.string.ratings_en));
        }

        if (theme == Theme.light) {
            ratingTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            ratingTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        } else {
            ratingTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            ratingTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
        }
    }

    void updateLayoutView(int row) {
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
                focusedLayout = "";
                focusedView.requestFocus();

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

    void setupBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
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
