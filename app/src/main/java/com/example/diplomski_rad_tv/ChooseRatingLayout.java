package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class ChooseRatingLayout {
    public static void setupMyRatingButton(Context ctx, Button button, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        button.setAllCaps(false);
        switch (language) {
            case german:
                button.setText(R.string.my_rating_de);
                break;
            case croatian:
                button.setText(R.string.my_rating_hr);
                break;
            default:
                button.setText(R.string.my_rating_en);
        }

        if (focusedView.getId() == R.id.submitRatingButton) {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
    public static void setupCancelButton(Context ctx, Button button, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        button.setAllCaps(false);
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

        if (focusedView.getId() == R.id.cancelButtonRating) {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }

    public static void setupShowRatingButton(Context ctx, Button button, View focusedView, Language language, Theme theme) {
        if (button == null) return;

        button.setAllCaps(false);
        switch (language) {
            case german:
                button.setText(R.string.show_ratings_de);
                break;
            case croatian:
                button.setText(R.string.show_ratings_hr);
                break;
            default:
                button.setText(R.string.show_ratings_en);
        }

        if (focusedView.getId() == R.id.showRatingsButton) {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }

    public static void setupLayoutTitle(Context ctx, TextView enterPasswordTitle, Language language, Theme theme) {
        if (enterPasswordTitle == null) return;

        switch (language) {
            case german:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.choose_rating_page_de));
                break;
            case croatian:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.choose_rating_page_hr));
                break;
            default:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.choose_rating_page_en));
        }

        if (theme == Theme.light) enterPasswordTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else enterPasswordTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }

    public static void setupLayoutBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.drawable.password_field_cream_background));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.drawable.password_field_purple_background));
    }
}
