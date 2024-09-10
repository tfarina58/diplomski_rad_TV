package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class RatingHeaderButton {
    public static void setupRatingButton(Context ctx, Button ratingButton, ImageView ratingIcon, boolean visible, View focusedView, Language language, Theme theme) {
        if (ratingButton == null || ratingIcon == null) return;

        if (!visible) {
            ratingButton.setVisibility(View.INVISIBLE);
            ratingIcon.setVisibility(View.INVISIBLE);
            return;
        }

        switch (language) {
            case german:
                ratingButton.setText(R.string.ratings_de);
                break;
            case croatian:
                ratingButton.setText(R.string.ratings_hr);
                break;
            default:
                ratingButton.setText(R.string.ratings_en);
        }

        ratingIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.rating));

        if (focusedView.getId() == R.id.ratingButton) {
            if (theme == Theme.light) ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) ratingButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else ratingButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
