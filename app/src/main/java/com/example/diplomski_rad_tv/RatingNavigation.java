package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextClock;

import androidx.core.content.ContextCompat;

public class RatingNavigation {
    private static final int[][] navigationRating = {
            {0,                   R.id.ratingContent, 0,                   R.id.themeButton},  // languageButton
            {0,                   R.id.ratingContent, R.id.languageButton, R.id.textClock},    // themeButton
            {0,                   R.id.ratingContent, R.id.themeButton,    0},                 // textClock
            {R.id.languageButton, R.id.ratingBar,     0,                   0},                 // ratingContent
            {R.id.ratingContent,  0,                  0,                   R.id.cancelButton}, // ratingBar
            {R.id.ratingContent,  0,                  R.id.ratingBar,      R.id.ratingButton}, // cancelButton
            {R.id.ratingContent,  0,                  R.id.cancelButton,   0},                 // ratingButton
    };
    public static int navigateOverActivity(int currentViewId, int direction) {
        return navigationRating[getRowWithId(currentViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.languageButton) return 0;
        if (currentViewId == R.id.themeButton) return 1;
        if (currentViewId == R.id.textClock) return 2;
        if (currentViewId == R.id.ratingContent) return 3;
        if (currentViewId == R.id.ratingBar) return 4;
        if (currentViewId == R.id.cancelButton) return 5;
        if (currentViewId == R.id.ratingButton) return 6;
        return -1;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.textClock;
    }

    public static boolean isLowerButtons(int viewId) {
        return viewId == R.id.ratingBar || viewId == R.id.cancelButton || viewId == R.id.ratingButton;
    }

    public static void setupLanguageButton(Context ctx, Button languageButton, ImageView languageIcon, View focusedView, Language language, Theme theme) {
        if (languageButton == null || languageIcon == null) return;

        switch (language) {
            case english:
                languageButton.setText(R.string.language_en);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.english));
                break;
            case german:
                languageButton.setText(R.string.language_de);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.german));
                break;
            case croatian:
                languageButton.setText(R.string.language_hr);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.croatian));
                break;
        }

        if (focusedView.getId() == R.id.languageButton) languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }

    public static void setupThemeButton(Context ctx, Button themeButton, ImageView themeIcon, View focusedView, Language language, Theme theme) {
        if (themeButton == null || themeIcon == null) return;

        switch (theme) {
            case light:
                if (language == Language.english) themeButton.setText(R.string.light_theme_en);
                else if (language == Language.german) themeButton.setText(R.string.light_theme_de);
                else if (language == Language.croatian) themeButton.setText(R.string.light_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.sun));
                break;
            case dark:
                if (language == Language.english) themeButton.setText(R.string.dark_theme_en);
                else if (language == Language.german) themeButton.setText(R.string.dark_theme_de);
                else if (language == Language.croatian) themeButton.setText(R.string.dark_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.moon));
                break;
        }

        if (focusedView.getId() == R.id.themeButton) themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }
    public static void setupTextClockButton(Context ctx, TextClock clockButton, View focusedView, Clock format, Theme theme) {
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

        if (focusedView.getId() == R.id.textClock) clockButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else clockButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }

    public static void setupRatingContentField(Context ctx, EditText ratingContent, View focusedView, Language language, Theme theme) {
        if (ratingContent == null) return;

        switch (language) {
            case german:
                ratingContent.setText(R.string.rating_content_de);
                break;
            case croatian:
                ratingContent.setText(R.string.rating_content_hr);
                break;
            default:
                ratingContent.setText(R.string.rating_content_en);
        }

        if (focusedView.getId() == R.id.ratingContent) {
            if (theme == Theme.dark) ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
            else ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
        } else ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
    }

    public static void setupRatingBarField(Context ctx, RatingBar ratingBar, View focusedView, Theme theme) {
        if (ratingBar == null) return;

        if (focusedView.getId() == R.id.ratingBar) {
            if (theme == Theme.dark) ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
            else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
        } else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
    }


    public static void setupCancelButton(Context ctx, Button button, View focusedView) {
        if (button == null) return;

        if (focusedView.getId() == R.id.cancelButton) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }

    public static void setupRatingButton(Context ctx, Button button, View focusedView) {
        if (button == null) return;

        if (focusedView.getId() == R.id.ratingButton) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }
}
