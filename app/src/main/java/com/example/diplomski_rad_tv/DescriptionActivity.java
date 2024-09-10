package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Picasso;

import android.text.SpannableString;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DescriptionActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Element element;
    Language language;
    Theme theme;
    Clock format;
    View focusedView;
    boolean fullscreenMode = false;
    String focusedLayout = ""; // "rating", "bigWorkingHours" or ""
    View layoutFocusedView;
    String temperatureUnit = "";
    boolean daytime = false;
    int weatherCode = 0;
    double temperature = -999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();
        this.format = this.sharedPreferencesService.getClockFormat();
        GeoPoint coordinates = this.sharedPreferencesService.getEstateCoordinates();
        this.temperatureUnit = this.sharedPreferencesService.getTemperatureUnit();


        Bundle bundle = getIntent().getExtras();
        String jsonElement = bundle.getString("element");

        Gson gson = new Gson();
        Element element = gson.fromJson(jsonElement, Element.class);

        this.element = element;
        if (this.element.template == 1) setContentView(R.layout.activity_description_1);
        else if (this.element.template == 2) setContentView(R.layout.activity_description_2);
        else setContentView(R.layout.activity_description_3);

        if (coordinates != null) getWeatherAndTemperature(coordinates);

        {
            TextView title = findViewById(R.id.descriptionTitle);

            this.setupTitle(getApplicationContext(), title, this.element.title, this.language, this.theme);
        }

        {
            ConstraintLayout background = findViewById(R.id.background);
            setupBackground(getApplicationContext(), background, theme);
        }

        if (this.element.template == 1) this.setupDescriptionActivity1();
        else if (this.element.template == 2) this.setupDescriptionActivity2();
        else this.setupDescriptionActivity3();

        {
            Button ratingButton = findViewById(R.id.ratingButton);
            ImageView ratingIcon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);

            ratingButton.setOnClickListener(new View.OnClickListener() {
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
        }

        {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);

            languageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    sharedPreferencesService.setLanguage(language);

                    boolean hasWeatherButton = weatherCode != 0;
                    updateView(element.template, hasWeatherButton, 0);
                    updateView(element.template, hasWeatherButton, 1);
                    updateView(element.template, hasWeatherButton, 2);

                    TextView title = findViewById(R.id.descriptionTitle);
                    setupTitle(getApplicationContext(), title, element.title, language, theme);

                    TextView content = findViewById(R.id.descriptionContent);
                    setupDescription(getApplicationContext(), content, element.description, focusedView, language, theme);

                    setupLinks();
                }
            });
        }

        {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);

            themeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme = theme.next();
                    sharedPreferencesService.setTheme(theme);

                    // updateView(element.template, 2);

                    ConstraintLayout background = findViewById(R.id.background);
                    setupBackground(getApplicationContext(), background, theme);

                    setupHeaderButtons();

                    TextView descriptionTitle = findViewById(R.id.descriptionTitle);
                    setupTitle(getApplicationContext(), descriptionTitle, element.title, language, theme);

                    TextView descriptionContent = findViewById(R.id.descriptionContent);
                    setupDescription(getApplicationContext(), descriptionContent, element.description, focusedView, language, theme);

                    setupLinks();

                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, element, focusedView, language, theme);
                }
            });
        }

        {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);

            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    format = format.next();
                    sharedPreferencesService.setClockFormat(format);

                    switch (format) {
                        case h24:
                            ((TextClock)focusedView).setFormat12Hour("HH:mm:ss");
                            break;
                        case h12:
                            ((TextClock)focusedView).setFormat12Hour("hh:mm:ss a");
                            break;
                    }
                }
            });
        }

        {

            // Weather button
            Button button = findViewById(R.id.weatherButton);
            ImageView icon = findViewById(R.id.weatherIcon);

            WeatherHeaderButton.setupWeatherButton(getApplicationContext(), button, icon, focusedView, daytime, weatherCode, temperature, temperatureUnit, theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (temperatureUnit.equals("C")) temperatureUnit = "F";
                    else temperatureUnit = "C";

                    sharedPreferencesService.setTemperatureUnit(temperatureUnit);

                    Button weatherButton = findViewById(R.id.weatherButton);;
                    ImageView weatherIcon = findViewById(R.id.weatherIcon);;

                    WeatherHeaderButton.setupWeatherButton(getApplicationContext(), weatherButton, weatherIcon, focusedView, daytime, weatherCode, temperature, temperatureUnit, theme);
                }
            });
        }

        {
            Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
            TextView workingHours = findViewById(R.id.workingHours);
            TextView entryFee = findViewById(R.id.entryFee);
            TextView minimalAge = findViewById(R.id.minimalAge);

            SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);

            smallWorkingHours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusedLayout = "bigWorkingHours";

                    FrameLayout bigWorkingHoursLayout = findViewById(R.id.bigWorkingHoursLayout);
                    ConstraintLayout background = findViewById(R.id.bigWorkingHoursMain);
                    TextView bigWorkingHoursTitle = findViewById(R.id.bigWorkingHoursTitle);
                    TextView mondayTitle = findViewById(R.id.mondayTitle);
                    TextView mondayContent = findViewById(R.id.mondayContent);
                    TextView tuesdayTitle = findViewById(R.id.tuesdayTitle);
                    TextView tuesdayContent = findViewById(R.id.tuesdayContent);
                    TextView wednesdayTitle = findViewById(R.id.wednesdayTitle);
                    TextView wednesdayContent = findViewById(R.id.wednesdayContent);
                    TextView thursdayTitle = findViewById(R.id.thursdayTitle);
                    TextView thursdayContent = findViewById(R.id.thursdayContent);
                    TextView fridayTitle = findViewById(R.id.fridayTitle);
                    TextView fridayContent = findViewById(R.id.fridayContent);
                    TextView saturdayTitle = findViewById(R.id.saturdayTitle);
                    TextView saturdayContent = findViewById(R.id.saturdayContent);
                    TextView sundayTitle = findViewById(R.id.sundayTitle);
                    TextView sundayContent = findViewById(R.id.sundayContent);

                    BigWorkingHoursLayout.setupBigWorkingHoursButton(
                        getApplicationContext(), bigWorkingHoursLayout,
                        background, bigWorkingHoursTitle,
                        mondayTitle, mondayContent,
                        tuesdayTitle, tuesdayContent,
                        wednesdayTitle, wednesdayContent,
                        thursdayTitle, thursdayContent,
                        fridayTitle, fridayContent,
                        saturdayTitle, saturdayContent,
                        sundayTitle, sundayContent,
                        true, element.workingHours,
                        language, theme
                    );


                    CustomScrollView scrollView = findViewById(R.id.scrollView);

                    scrollToCenterView(scrollView, findViewById(R.id.bigWorkingHoursLayout));
                }
            });
        }

        this.setupLinks();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        if (this.fullscreenMode) {
            ViewPager2 viewPager = findViewById(R.id.viewPager);
            int currentItem = viewPager.getCurrentItem();

            if (keyCode == 21) {
                if (currentItem > 0) viewPager.setCurrentItem(currentItem - 1);
            } else if (keyCode == 22) {
                if (currentItem < this.element.images.size() - 1) viewPager.setCurrentItem(currentItem + 1);
            } else if (keyCode == 4) {
                viewPager.setVisibility(View.INVISIBLE);

                this.fullscreenMode = false;

                CustomScrollView scrollView = findViewById(R.id.scrollView);
                this.scrollToCenterView(scrollView, this.focusedView);

                return false;
            }

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        else if (this.focusedLayout.equals("bigWorkingHours")) {
            if (keyCode == 4) {
                focusedLayout = "";

                FrameLayout bigWorkingHoursLayout = findViewById(R.id.bigWorkingHoursLayout);
                ConstraintLayout background = findViewById(R.id.bigWorkingHoursMain);
                TextView bigWorkingHoursTitle = findViewById(R.id.bigWorkingHoursTitle);
                TextView mondayTitle = findViewById(R.id.mondayTitle);
                TextView mondayContent = findViewById(R.id.mondayContent);
                TextView tuesdayTitle = findViewById(R.id.tuesdayTitle);
                TextView tuesdayContent = findViewById(R.id.tuesdayContent);
                TextView wednesdayTitle = findViewById(R.id.wednesdayTitle);
                TextView wednesdayContent = findViewById(R.id.wednesdayContent);
                TextView thursdayTitle = findViewById(R.id.thursdayTitle);
                TextView thursdayContent = findViewById(R.id.thursdayContent);
                TextView fridayTitle = findViewById(R.id.fridayTitle);
                TextView fridayContent = findViewById(R.id.fridayContent);
                TextView saturdayTitle = findViewById(R.id.saturdayTitle);
                TextView saturdayContent = findViewById(R.id.saturdayContent);
                TextView sundayTitle = findViewById(R.id.sundayTitle);
                TextView sundayContent = findViewById(R.id.sundayContent);

                BigWorkingHoursLayout.setupBigWorkingHoursButton(
                    getApplicationContext(), bigWorkingHoursLayout,
                    background, bigWorkingHoursTitle,
                    mondayTitle, mondayContent,
                    tuesdayTitle, tuesdayContent,
                    wednesdayTitle, wednesdayContent,
                    thursdayTitle, thursdayContent,
                    fridayTitle, fridayContent,
                    saturdayTitle, saturdayContent,
                    sundayTitle, sundayContent,
                    false, element.workingHours,
                    language, theme
                );

                CustomScrollView scrollView = findViewById(R.id.scrollView);
                this.scrollToCenterView(scrollView, this.focusedView);

                return false;
            }
            return true;

        ////////////////////////////////////////////////////////////////////////////////////////////

        } else if (this.focusedLayout.equals("bigDescriptionTemplate")) {
            if (keyCode == 4) {
                focusedLayout = "";

                TextView bigWorkingHoursLayout = findViewById(R.id.bigDescriptionContent);

                BigDescriptionTemplate2.setupBigDescriptionTemplate2(getApplicationContext(), bigWorkingHoursLayout, false, element.description, language, theme);

                CustomScrollView scrollView = findViewById(R.id.scrollView);
                this.scrollToCenterView(scrollView, bigWorkingHoursLayout);

                return false;
            }
            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

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

        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            if (specialCaseNavigation(oldFocusedViewId, keyCode - 19)) return true;

            int newFocusedViewId = DescriptionNavigation.navigateOverActivity(this.element.template, weatherCode != 0, oldFocusedViewId, keyCode - 19);
            while (!checkViewExistence(newFocusedViewId) && newFocusedViewId != 0)
                newFocusedViewId = DescriptionNavigation.navigateOverActivity(this.element.template, weatherCode != 0, newFocusedViewId, keyCode - 19);

            // If == 0, focusedView will stay the same
            if (newFocusedViewId == 0) return true;

            // If != 0, focusedView will change its value
            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = DescriptionNavigation.getRowWithId(this.element.template, weatherCode != 0, oldFocusedViewId);
            updateView(this.element.template, weatherCode != 0, row);

            // Add focus to new View
            row = DescriptionNavigation.getRowWithId(this.element.template, weatherCode != 0, newFocusedViewId);
            updateView(this.element.template, weatherCode != 0, row);

            CustomScrollView scrollView = findViewById(R.id.scrollView);
            this.scrollToCenterView(scrollView, this.focusedView);
        } else if (keyCode == 4) {
            sharedPreferencesService.setElementId("");
            startActivity(new Intent(getApplicationContext(), ElementListActivity.class));
        }
        // Enter button
        else if (keyCode == 23) this.focusedView.callOnClick();
        return true;
    }

    // TODO!
    boolean checkViewExistence(int focusedViewId) {
        if (focusedViewId == R.id.ratingButton) return true;
        if (focusedViewId == R.id.languageButton) return true;
        if (focusedViewId == R.id.themeButton) return true;
        if (focusedViewId == R.id.textClock) return true;
        if (focusedViewId == R.id.weatherButton) return weatherCode != 0;
        if (focusedViewId == R.id.descriptionContent) return this.element.description != null && !this.element.description.isEmpty();
        if (focusedViewId == R.id.descriptionImage1) return this.element.images.size() > 0 && !this.element.images.get(0).isEmpty();
        if (focusedViewId == R.id.descriptionImage2) return this.element.images.size() > 1 && !this.element.images.get(1).isEmpty();
        if (focusedViewId == R.id.descriptionImage3) return this.element.images.size() > 2 && !this.element.images.get(2).isEmpty();
        if (focusedViewId == R.id.descriptionImage4) return this.element.images.size() > 3 && !this.element.images.get(3).isEmpty();
        if (focusedViewId == R.id.descriptionImage5) return this.element.images.size() > 4 && !this.element.images.get(4).isEmpty();
        if (focusedViewId == R.id.descriptionImage6) return this.element.images.size() > 5 && !this.element.images.get(5).isEmpty();
        if (focusedViewId == R.id.descriptionImage7) return this.element.images.size() > 6 && !this.element.images.get(6).isEmpty();
        if (focusedViewId == R.id.descriptionImage8) return this.element.images.size() > 7 && !this.element.images.get(7).isEmpty();
        if (focusedViewId == R.id.descriptionImage9) return this.element.images.size() > 8 && !this.element.images.get(8).isEmpty();
        if (focusedViewId == R.id.viewPager) return this.element.images.size() > 0 && !this.element.images.get(0).isEmpty();
        if (focusedViewId == R.id.smallWorkingHours) return this.element.workingHours.size() > 0;
        if (focusedViewId == R.id.descriptionLink1) return this.checkLinkExistence(0);
        if (focusedViewId == R.id.descriptionLink2) return this.checkLinkExistence(1);
        if (focusedViewId == R.id.descriptionLink3) return this.checkLinkExistence(2);
        return false;
    }

    void setupBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
    }

    void setupTitle(Context ctx, TextView titleText, HashMap<String, String> title, Language language, Theme theme) {
        if (titleText == null) return;

        switch (language) {
            case german:
                if (title == null) {
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_de));
                    break;
                }

                String titleDe = title.get("de");

                if (titleDe != null && !titleDe.isEmpty()) titleText.setText(titleDe);
                else titleText.setText(ContextCompat.getString(ctx, R.string.element_name_de));
                break;
            case croatian:
                if (title == null) {
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_hr));
                    break;
                }

                String titleHr = title.get("hr");

                if (titleHr != null && !titleHr.isEmpty()) titleText.setText(titleHr);
                else titleText.setText(ContextCompat.getString(ctx, R.string.element_name_hr));
                break;
            default:
                if (title == null) {
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_en));
                    break;
                }

                String titleEn = title.get("en");

                if (titleEn != null && !titleEn.isEmpty()) titleText.setText(titleEn);
                else titleText.setText(ContextCompat.getString(ctx, R.string.category_name_en));
                break;
        }

        if (theme == Theme.light) titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    void setupDescription(Context ctx, TextView descriptionView, HashMap<String, String> description, View focusedView, Language language, Theme theme) {
        if (descriptionView == null) return;

        switch (language) {
            case german:
                String descriptionDe = description.get("de");
                if (descriptionDe == null || descriptionDe.isEmpty()) {
                    descriptionView.setText("");
                    descriptionView.setVisibility(View.INVISIBLE);
                } else descriptionView.setText(descriptionDe);
                break;
            case croatian:
                String descriptionHr = description.get("hr");
                if (descriptionHr == null || descriptionHr.isEmpty()) {
                    descriptionView.setText("");
                    descriptionView.setVisibility(View.INVISIBLE);
                } else descriptionView.setText(descriptionHr);
                break;
            default:
                String descriptionEn = description.get("en");
                if (descriptionEn == null || descriptionEn.isEmpty()) {
                    descriptionView.setText("");
                    descriptionView.setVisibility(View.INVISIBLE);
                } else descriptionView.setText(descriptionEn);
        }

        if (focusedView.getId() == R.id.descriptionContent) {
            if (theme == Theme.dark) {
                descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
                descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                descriptionView.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
                descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                descriptionView.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        } else {
            if (theme == Theme.dark) {
                descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
                descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                descriptionView.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
                descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                descriptionView.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        }
    }

    void setupImages() {
        int imagesLength = (this.element.images != null) ? this.element.images.size() : 0;
        ImageView image;

        image = findViewById(R.id.descriptionImage1);
        if (0 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(0);
            }
        });

        image = findViewById(R.id.descriptionImage2);
        if (1 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(1), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(1);
            }
        });

        image = findViewById(R.id.descriptionImage3);
        if (2 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(2), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(2);
            }
        });

        image = findViewById(R.id.descriptionImage4);
        if (3 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(3), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(3);
            }
        });

        image = findViewById(R.id.descriptionImage5);
        if (4 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(4), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(4);
            }
        });

        image = findViewById(R.id.descriptionImage6);
        if (5 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(5), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(5);
            }
        });

        image = findViewById(R.id.descriptionImage7);
        if (6 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(6), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(6);
            }
        });

        image = findViewById(R.id.descriptionImage8);
        if (7 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(7), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(7);
            }
        });

        image = findViewById(R.id.descriptionImage9);
        if (8 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(8), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(8);
            }
        });
    }

    void setupImage(Context ctx, ImageView image, String imageUrl, View focusedView, Theme theme) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            image.setVisibility(View.GONE);
            return;
        }

        if (focusedView.getId() == image.getId()) {
            if (theme == Theme.light) image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
            else image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
        } else image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));

        try {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .into(image);
        } catch (Exception e) {
            e.printStackTrace();
            image.setVisibility(View.GONE);
        }
    }

    void setupLinks() {
        int linksLength = (this.element.links != null) ? this.element.links.size() : 0;
        Button button;

        button = findViewById(R.id.descriptionLink1);
        if (0 < linksLength) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
        else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 0; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        button = findViewById(R.id.descriptionLink2);
        if (1 < linksLength) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
        else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 1; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        button = findViewById(R.id.descriptionLink3);
        if (2 < linksLength) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>)this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
        else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 2; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }

    void setupLink(Context ctx, Button button, LinkedTreeMap<String, String> linkTitles, String linkUrl, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        String title = null;
        if (linkTitles != null) {
            switch (language) {
                case german:
                    title = linkTitles.get("de");
                    break;
                case croatian:
                    title = linkTitles.get("hr");
                    break;
                default:
                    title = linkTitles.get("en");
            }
        }

        if (title == null || title.isEmpty() || linkUrl == null || linkUrl.isEmpty()) {
            button.setVisibility(View.GONE);
            return;
        }

        if (focusedView.getId() == button.getId()) {
            if (theme == Theme.light) {
                button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
                button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            } else {
                button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
                button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            }
        } else {
            button.setBackground(ContextCompat.getDrawable(ctx, R.color.transparent));
            if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }

        SpannableString content = new SpannableString(title);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        button.setText(content);
        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    void updateView(long template, boolean hasWeatherButton, int row) {
        if (!hasWeatherButton) {
            if (template == 1) {
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
                    TextView description = findViewById(R.id.descriptionContent);

                    this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);
                } else if (row == 5) {
                    ImageView image = findViewById(R.id.descriptionImage1);

                    if (0 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 6) {
                    ImageView image = findViewById(R.id.descriptionImage2);

                    if (1 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(1), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 7) {
                    ImageView image = findViewById(R.id.descriptionImage3);

                    if (2 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(2), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 8) {
                    ImageView image = findViewById(R.id.descriptionImage4);

                    if (3 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(3), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 9) {
                    ImageView image = findViewById(R.id.descriptionImage5);

                    if (4 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(4), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 10) {
                    ImageView image = findViewById(R.id.descriptionImage6);

                    if (5 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(5), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 11) {
                    ImageView image = findViewById(R.id.descriptionImage7);

                    if (6 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(6), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 12) {
                    ImageView image = findViewById(R.id.descriptionImage8);

                    if (7 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(7), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 13) {
                    ImageView image = findViewById(R.id.descriptionImage9);

                    if (8 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(8), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 14) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                } else if (row == 15) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 16) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 17) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            } else if (template == 2) {
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
                    TextView description = findViewById(R.id.descriptionContent);

                    this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);
                } else if (row == 5) {
                    ImageView image = findViewById(R.id.descriptionImage1);

                    if (0 < this.element.images.size())
                        this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
                    else
                        this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                } else if (row == 6) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                } else if (row == 7) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 8) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 9) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            } else {
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
                    ViewPager2 viewPager = findViewById(R.id.viewPager);

                    this.setupViewPager(getApplicationContext(), viewPager, this.element.images.size(), this.focusedView, this.theme);
                } else if (row == 5) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                } else if (row == 6) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 7) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                } else if (row == 8) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size())
                        this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else
                        this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            }
        } else {
            if (template == 1) {
                if (row == 0) {
                    Button ratingButton = findViewById(R.id.ratingButton);
                    ImageView ratingIcon = findViewById(R.id.ratingIcon);

                    RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);
                }
                else if (row == 1) {
                    Button languageButton = findViewById(R.id.languageButton);
                    ImageView languageIcon = findViewById(R.id.languageIcon);

                    LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 2) {
                    Button themeButton = findViewById(R.id.themeButton);
                    ImageView themeIcon = findViewById(R.id.themeIcon);

                    ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 3) {
                    TextClock textClock = findViewById(R.id.textClock);

                    ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
                }
                else if (row == 4) {
                    Button weatherButton = findViewById(R.id.weatherButton);
                    ImageView weatherIcon = findViewById(R.id.weatherIcon);

                    WeatherHeaderButton.setupWeatherButton(getApplicationContext(), weatherButton, weatherIcon, this.focusedView, this.daytime, this.weatherCode, this.temperature, this.temperatureUnit, this.theme);
                }
                else if (row == 5) {
                    TextView description = findViewById(R.id.descriptionContent);

                    this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);
                }
                else if (row == 6) {
                    ImageView image = findViewById(R.id.descriptionImage1);

                    if (0 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 7) {
                    ImageView image = findViewById(R.id.descriptionImage2);

                    if (1 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(1), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 8) {
                    ImageView image = findViewById(R.id.descriptionImage3);

                    if (2 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(2), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 9) {
                    ImageView image = findViewById(R.id.descriptionImage4);

                    if (3 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(3), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 10) {
                    ImageView image = findViewById(R.id.descriptionImage5);

                    if (4 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(4), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 11) {
                    ImageView image = findViewById(R.id.descriptionImage6);

                    if (5 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(5), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 12) {
                    ImageView image = findViewById(R.id.descriptionImage7);

                    if (6 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(6), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 13) {
                    ImageView image = findViewById(R.id.descriptionImage8);

                    if (7 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(7), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 14) {
                    ImageView image = findViewById(R.id.descriptionImage9);

                    if (8 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(8), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 15) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                }
                else if (row == 16) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 17) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 18) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            } else if (template == 2) {
                if (row == 0) {
                    Button ratingButton = findViewById(R.id.ratingButton);
                    ImageView ratingIcon = findViewById(R.id.ratingIcon);

                    RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);
                }
                else if (row == 1) {
                    Button languageButton = findViewById(R.id.languageButton);
                    ImageView languageIcon = findViewById(R.id.languageIcon);

                    LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 2) {
                    Button themeButton = findViewById(R.id.themeButton);
                    ImageView themeIcon = findViewById(R.id.themeIcon);

                    ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 3) {
                    TextClock textClock = findViewById(R.id.textClock);

                    ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
                }
                else if (row == 4) {
                    Button weatherButton = findViewById(R.id.weatherButton);
                    ImageView weatherIcon = findViewById(R.id.weatherIcon);

                    WeatherHeaderButton.setupWeatherButton(getApplicationContext(), weatherButton, weatherIcon, this.focusedView, this.daytime, this.weatherCode, this.temperature, this.temperatureUnit, this.theme);
                }
                else if (row == 5) {
                    TextView description = findViewById(R.id.descriptionContent);

                    this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);
                }
                else if (row == 6) {
                    ImageView image = findViewById(R.id.descriptionImage1);

                    if (0 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
                    else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
                }
                else if (row == 7) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                }
                else if (row == 8) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 9) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 10) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            } else {
                if (row == 0) {
                    Button ratingButton = findViewById(R.id.ratingButton);
                    ImageView ratingIcon = findViewById(R.id.ratingIcon);

                    RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language, this.theme);
                }
                else if (row == 1) {
                    Button languageButton = findViewById(R.id.languageButton);
                    ImageView languageIcon = findViewById(R.id.languageIcon);

                    LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 2) {
                    Button themeButton = findViewById(R.id.themeButton);
                    ImageView themeIcon = findViewById(R.id.themeIcon);

                    ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
                }
                else if (row == 3) {
                    TextClock textClock = findViewById(R.id.textClock);

                    ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
                }
                else if (row == 4) {
                    Button weatherButton = findViewById(R.id.weatherButton);
                    ImageView weatherIcon = findViewById(R.id.weatherIcon);

                    WeatherHeaderButton.setupWeatherButton(getApplicationContext(), weatherButton, weatherIcon, this.focusedView, this.daytime, this.weatherCode, this.temperature, this.temperatureUnit, this.theme);
                }
                else if (row == 5) {
                    ViewPager2 viewPager = findViewById(R.id.viewPager);

                    this.setupViewPager(getApplicationContext(), viewPager, this.element.images.size(), this.focusedView, this.theme);
                }
                else if (row == 6) {
                    Button smallWorkingHours = findViewById(R.id.smallWorkingHours);
                    TextView workingHours = findViewById(R.id.workingHours);
                    TextView entryFee = findViewById(R.id.entryFee);
                    TextView minimalAge = findViewById(R.id.minimalAge);

                    SmallWorkingHours.setupSmallWorkingHours(getApplicationContext(), smallWorkingHours, workingHours, entryFee, minimalAge, this.element, this.focusedView, this.language, this.theme);
                }
                else if (row == 7) {
                    Button button = findViewById(R.id.descriptionLink1);

                    if (0 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(0).get("title"), (String) this.element.links.get(0).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 8) {
                    Button button = findViewById(R.id.descriptionLink2);

                    if (1 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(1).get("title"), (String) this.element.links.get(1).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
                else if (row == 9) {
                    Button button = findViewById(R.id.descriptionLink3);

                    if (2 < this.element.links.size()) this.setupLink(getApplicationContext(), button, (LinkedTreeMap<String, String>) this.element.links.get(2).get("title"), (String) this.element.links.get(2).get("url"), this.focusedView, this.language, this.theme);
                    else this.setupLink(getApplicationContext(), button, null, "", this.focusedView, this.language, this.theme);
                }
            }
        }
    }

    void scrollToCenterView(CustomScrollView scrollView, View focusedView) {
        if (scrollView == null || focusedView == null) return;

        int scrollViewHeight = scrollView.getHeight();
        int viewTop = focusedView.getTop();
        int viewHeight = focusedView.getHeight();
        int scrollToY = viewTop - (scrollViewHeight / 2) + (viewHeight / 2);

        scrollView.smoothScrollTo(0, scrollToY);
    }

    void showViewPager(int imageIndex) {
        Adapter adapter = new Adapter(this.element.images);
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imageIndex);
        viewPager.setVisibility(View.VISIBLE);
        this.fullscreenMode = true;

        CustomScrollView scrollView = findViewById(R.id.scrollView);

        this.scrollToCenterView(scrollView, viewPager);
    }

    boolean checkLinkExistence(int index) {
        if (this.element.links.size() <= index) return false;

        HashMap<String, Object> tmpMap = (HashMap<String, Object>) this.element.links.get(index);
        if (tmpMap == null) return false;

        String targetUrl = (String) tmpMap.get("url");
        if (targetUrl == null || targetUrl.isEmpty()) return false;

        LinkedTreeMap<String, String> tmpTitles = (LinkedTreeMap<String, String>) tmpMap.get("title");
        if (tmpTitles == null) return false;

        String targetTitle = null;
        if (this.language == Language.german) targetTitle = tmpTitles.get("de");
        else if (this.language == Language.croatian) targetTitle = tmpTitles.get("hr");
        else targetTitle = tmpTitles.get("en");
        if (targetTitle == null || targetTitle.isEmpty()) return false;

        return true;
    }

    void setupDescriptionActivity1() {
        TextView description = findViewById(R.id.descriptionContent);
        this.focusedView = description;
        this.focusedView.requestFocus();

        this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change amount of lines displayed on descriptionText
                if (description.getMaxLines() == 16) description.setMaxLines(32);
                else if (description.getMaxLines() == 32) description.setMaxLines(16);
            }
        });

        this.setupImages();
    }

    void setupDescriptionActivity2() {
        TextView description = findViewById(R.id.descriptionContent);
        this.focusedView = description;
        this.focusedView.requestFocus();

        this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.language, this.theme);

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusedLayout = "bigDescriptionTemplate";

                TextView bigDescriptionTemplate = findViewById(R.id.bigDescriptionContent);
                BigDescriptionTemplate2.setupBigDescriptionTemplate2(getApplicationContext(), bigDescriptionTemplate, true, element.description, language, theme);
            }
        });

        ImageView descriptionImage1 = findViewById(R.id.descriptionImage1);
        if (this.element.images.size() > 0) this.setupImage(getApplicationContext(), descriptionImage1, this.element.images.get(0), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), descriptionImage1, "", this.focusedView, this.theme);

        descriptionImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewPager(0);
            }
        });
    }

    void setupDescriptionActivity3() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        this.focusedView = viewPager;
        this.focusedView.requestFocus();

        if (this.element.images == null) this.element.images = new ArrayList<>();
        Adapter adapter = new Adapter(this.element.images);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        this.setupViewPager(getApplicationContext(), viewPager, element.images.size(), focusedView, theme);
    }

    void setupViewPager(Context ctx, ViewPager2 viewPager, int imagesLength, View focusedView, Theme theme) {
        if (viewPager == null) return;

        if (imagesLength == 0) {
            viewPager.setVisibility(View.GONE);
        }
        viewPager.setVisibility(View.VISIBLE);

        if (focusedView.getId() == R.id.viewPager) {
            if (theme == Theme.light) viewPager.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
            else viewPager.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
        } else viewPager.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
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
    boolean specialCaseNavigation(int oldFocusedViewId, int direction) {
        if (this.element.template != 3) return false;
        if (oldFocusedViewId != R.id.viewPager) return false;

        ViewPager2 viewPager = findViewById(oldFocusedViewId);
        int currentItem = viewPager.getCurrentItem();

        // Direction left
        if (direction == 2) {
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1);
                return true;
            }
        }
        // Direction right
        else if (direction == 3) {
            if (currentItem < this.element.images.size() - 1) {
                viewPager.setCurrentItem(currentItem + 1);
                return true;
            }
        }
        return false;
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

        Button weatherButton = findViewById(R.id.weatherButton);
        ImageView weatherIcon = findViewById(R.id.weatherIcon);

        WeatherHeaderButton.setupWeatherButton(getApplicationContext(), weatherButton, weatherIcon, this.focusedView, this.daytime, this.weatherCode, this.temperature, this.temperatureUnit, this.theme);
    }

    void getWeatherAndTemperature(GeoPoint coordinates) {
        OpenWeatherMap.sendPostRequest("https://api.openweathermap.org/data/2.5/weather?lat=" + coordinates.getLatitude() + "&lon=" + coordinates.getLongitude() + "&appid=60a327a5990e24e4c309de648bd01fbe", new OpenWeatherMap.WeatherCallback() {
            @Override
            public void onWeatherDataReceived(JSONObject weatherData) {
                try {
                    daytime = weatherData.getJSONObject("sys").getLong("sunrise") < weatherData.getLong("dt") && weatherData.getLong("dt") < weatherData.getJSONObject("sys").getLong("sunset");
                    weatherCode = weatherData.getJSONArray("weather").getJSONObject(0).getInt("id");
                    temperature = weatherData.getJSONObject("main").getDouble("temp");

                    Button button = findViewById(R.id.weatherButton);
                    ImageView icon = findViewById(R.id.weatherIcon);

                    WeatherHeaderButton.setupWeatherButton(getApplicationContext(), button, icon, focusedView, daytime, weatherCode, temperature, temperatureUnit, theme);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        });
    }
}
