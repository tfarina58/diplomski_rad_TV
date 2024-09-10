package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.core.content.ContextCompat;

public class MyRatingNavigation {
    private static final int[][] navigationRating = {
            {0,                   R.id.ratingContent, 0,                   R.id.languageButton},     // ratingButton
            {0,                   R.id.ratingContent, R.id.ratingButton,   R.id.themeButton},        // languageButton
            {0,                   R.id.ratingContent, R.id.languageButton, R.id.textClock},          // themeButton
            {0,                   R.id.ratingContent, R.id.themeButton,    R.id.weatherButton},      // textClock
            {0,                   R.id.ratingContent, R.id.textClock,      0},                       // weatherButton
            {R.id.languageButton, R.id.ratingBar,     0,                   0},                       // ratingContent
            {R.id.ratingContent,  R.id.cancelButton,  0,                   0},                       // ratingBar
            {R.id.ratingBar,      0,                  0,                   R.id.ratingSubmitButton}, // cancelButton
            {R.id.ratingBar,      0,                  R.id.cancelButton,   0},                       // ratingSubmitButton
    };

    private static final int[][] navigationRatingWithoutWeather = {
            {0,                   R.id.ratingContent, 0,                   R.id.languageButton},     // ratingButton
            {0,                   R.id.ratingContent, R.id.ratingButton,   R.id.themeButton},        // languageButton
            {0,                   R.id.ratingContent, R.id.languageButton, R.id.textClock},          // themeButton
            {0,                   R.id.ratingContent, R.id.themeButton,    0},                       // textClock
            {R.id.languageButton, R.id.ratingBar,     0,                   0},                       // ratingContent
            {R.id.ratingContent,  R.id.cancelButton,  0,                   0},                       // ratingBar
            {R.id.ratingBar,      0,                  0,                   R.id.ratingSubmitButton}, // cancelButton
            {R.id.ratingBar,      0,                  R.id.cancelButton,   0},                       // ratingSubmitButton
    };
    public static int navigateOverActivity(boolean hasWeatherButton, int currentViewId, int direction) {
        if (!hasWeatherButton) return navigationRatingWithoutWeather[getRowWithId(false, currentViewId)][direction];
        else return navigationRating[getRowWithId(true, currentViewId)][direction];
    }
    public static int getRowWithId(boolean hasWeatherButton, int currentViewId) {
        if (!hasWeatherButton) {
            if (currentViewId == R.id.ratingButton) return 0;
            if (currentViewId == R.id.languageButton) return 1;
            if (currentViewId == R.id.themeButton) return 2;
            if (currentViewId == R.id.textClock) return 3;
            if (currentViewId == R.id.ratingContent) return 4;
            if (currentViewId == R.id.ratingBar) return 5;
            if (currentViewId == R.id.cancelButton) return 6;
            if (currentViewId == R.id.ratingSubmitButton) return 7;
        } else {
            if (currentViewId == R.id.ratingButton) return 0;
            if (currentViewId == R.id.languageButton) return 1;
            if (currentViewId == R.id.themeButton) return 2;
            if (currentViewId == R.id.textClock) return 3;
            if (currentViewId == R.id.weatherButton) return 4;
            if (currentViewId == R.id.ratingContent) return 5;
            if (currentViewId == R.id.ratingBar) return 6;
            if (currentViewId == R.id.cancelButton) return 7;
            if (currentViewId == R.id.ratingSubmitButton) return 8;
        }

        return -1;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.textClock;
    }

    public static boolean isLowerButtons(int viewId) {
        return viewId == R.id.ratingBar || viewId == R.id.cancelButton || viewId == R.id.ratingSubmitButton;
    }

    public static void setupRatingContentField(Context ctx, EditText ratingContent, View focusedView, Language language, Theme theme) {
        if (ratingContent == null) return;

        switch (language) {
            case german:
                ratingContent.setHint(R.string.rating_content_de);
                break;
            case croatian:
                ratingContent.setHint(R.string.rating_content_hr);
                break;
            default:
                ratingContent.setHint(R.string.rating_content_en);
        }

        if (focusedView.getId() == R.id.ratingContent) {
            if (theme == Theme.dark) {
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        } else {
            if (theme == Theme.dark) {
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        }
    }

    public static void setupRatingBarField(Context ctx, RatingBar ratingBar, View focusedView, Theme theme, float rating) {
        if (ratingBar == null) return;

        if (focusedView.getId() == R.id.ratingBar) {
            if (theme == Theme.dark) ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
            else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
        } else {
            if (theme == Theme.dark) ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
            else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
        }

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);
        ratingBar.setRating(rating);
    }


    public static void setupCancelButton(Context ctx, Button button, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        switch (language) {
            case german:
                button.setText(R.string.cancel_de);
                break;
            case croatian:
                button.setText(R.string.cancel_hr);
                break;
            default:
                button.setText(R.string.cancel_en);
        }

        if (focusedView.getId() == R.id.cancelButton) {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }

    public static void setupRatingSubmitButton(Context ctx, Button button, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        switch (language) {
            case german:
                button.setText(R.string.send_rating_de);
                break;
            case croatian:
                button.setText(R.string.send_rating_hr);
                break;
            default:
                button.setText(R.string.send_rating_en);
        }

        if (focusedView.getId() == R.id.ratingSubmitButton) {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
