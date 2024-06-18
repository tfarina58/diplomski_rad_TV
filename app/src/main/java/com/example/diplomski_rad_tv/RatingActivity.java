package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class RatingActivity extends Activity {
    Language language ;
    Theme theme;
    Clock format = Clock.h12;
    View focusedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rating);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        this.language = this.setLanguage(sharedPreferences);
        this.theme = this.setTheme(sharedPreferences);

        setNewContentView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(getApplicationContext(), Integer.toString(keyCode), Toast.LENGTH_SHORT).show();
        return true;
    }

    void setupLanguageButton() {
        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        if (languageButton == null) return;
        switch (language) {
            case united_kingdom:
                languageButton.setText(R.string.language_en);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.united_kingdom));
                break;
            case germany:
                languageButton.setText(R.string.language_de);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.germany));
                break;
            case croatia:
                languageButton.setText(R.string.language_hr);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.croatia));
                break;
        }

        if (focusedView.getId() == R.id.languageButton) languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupThemeButton() {
        Button themeButton = findViewById(R.id.themeButton);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        if (themeButton == null) return;
        switch (theme) {
            case light:
                if (this.language == Language.united_kingdom) themeButton.setText(R.string.light_theme_en);
                else if (this.language == Language.germany) themeButton.setText(R.string.light_theme_de);
                else if (this.language == Language.croatia) themeButton.setText(R.string.light_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sun));
                break;
            case dark:
                if (this.language == Language.united_kingdom) themeButton.setText(R.string.dark_theme_en);
                else if (this.language == Language.germany) themeButton.setText(R.string.dark_theme_de);
                else if (this.language == Language.croatia) themeButton.setText(R.string.dark_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                break;
        }

        if (focusedView.getId() == R.id.themeButton) themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupTextClock() {
        TextClock clockButton = findViewById(R.id.textClock);

        if (clockButton == null) return;
        switch (format) {
            case h24:
                clockButton.setFormat12Hour("HH:mm:ss");
                break;
            case h12:
                clockButton.setFormat12Hour("hh:mm:ss a");
                break;
            default:
                return;
        }

        if (focusedView.getId() == R.id.textClock) clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupRatingContent() {
        EditText content = findViewById(R.id.ratingContent);
        if (content == null) return;

        if (this.focusedView == null) {
            this.focusedView = content;
        }

        switch (language) {
            case united_kingdom:
                content.setText(getResources().getString(R.string.rating_content_en));
                break;
            case germany:
                content.setText(getResources().getString(R.string.rating_content_de));
                break;
            case croatia:
                content.setText(getResources().getString(R.string.rating_content_hr));
                break;
        }

        if (this.focusedView == content) {
            if (this.theme == Theme.dark) {
                content.setBackground(getResources().getDrawable(R.drawable.main_border_dark));
                content.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
            } else if (this.theme == Theme.light) {
                content.setBackground(getResources().getDrawable(R.drawable.main_border_light));
                content.setTextColor(getResources().getColor(R.color.text_color_light_mode));
            }
        } else content.setBackground(getResources().getDrawable(R.drawable.image_button));
    }

    void setupSendButton() {
        Button ratingButton = findViewById(R.id.ratingButton);
        if (ratingButton == null) return;

        if (this.focusedView == ratingButton) ratingButton.setBackground(getResources().getDrawable(R.drawable.button_design_focused));
        else ratingButton.setBackground(getResources().getDrawable(R.drawable.button_design));
    }

    void setNewContentView() {
        this.setupBackground();
        this.setupRatingContent();
        this.setupLanguageButton();
        this.setupThemeButton();
        this.setupTextClock();
        this.setupSendButton();
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

    void setupBackground() {
        ConstraintLayout layout = findViewById(R.id.main);
        if (this.theme == Theme.dark) layout.setBackground(getResources().getDrawable(R.color.dark_theme));
        else if (this.theme == Theme.light) layout.setBackground(getResources().getDrawable(R.color.light_theme));
    }
}
