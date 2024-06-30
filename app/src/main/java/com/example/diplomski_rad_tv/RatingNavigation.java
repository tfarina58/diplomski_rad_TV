package com.example.diplomski_rad_tv;

import android.content.Context;
import android.os.Build;
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
            {R.id.ratingContent,  R.id.cancelButton,  0,                   0},                 // ratingBar
            {R.id.ratingBar,      0,                  0,                   R.id.ratingButton}, // cancelButton
            {R.id.ratingBar,      0,                  R.id.cancelButton,   0},                 // ratingButton
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
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        } else {
            ratingContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
            if (theme == Theme.dark) {
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_dark_mode));
            } else {
                ratingContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                ratingContent.setHintTextColor(ContextCompat.getColor(ctx, R.color.hint_color_light_mode));
            }
        }
    }

    public static void setupRatingBarField(Context ctx, RatingBar ratingBar, View focusedView, Theme theme) {
        if (ratingBar == null) return;

        if (focusedView.getId() == R.id.ratingBar) {
            if (theme == Theme.dark) ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
            else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
        } else ratingBar.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1);
        ratingBar.setRating(5);
    }


    public static void setupCancelButton(Context ctx, Button button, View focusedView, Language language) {
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

        if (focusedView.getId() == R.id.cancelButton) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }

    public static void setupRatingButton(Context ctx, Button button, View focusedView, Language language) {
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

        if (focusedView.getId() == R.id.ratingButton) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }
}
