package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class RatingCard {
    public static void setupRatingCard(Context ctx, Button ratingButton, TextView ratingUsername, RatingBar ratingBar, Button centerLine, TextView ratingComment, Rating rating, Language language, Theme theme) {
        if (ratingButton == null || ratingUsername == null || ratingBar == null || ratingComment == null) return;

        if (rating == null) {
            ratingButton.setVisibility(View.GONE);
            ratingUsername.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            centerLine.setVisibility(View.GONE);
            ratingComment.setVisibility(View.GONE);
            return;
        }

        ratingButton.setVisibility(View.VISIBLE);
        ratingUsername.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        centerLine.setVisibility(View.VISIBLE);
        ratingComment.setVisibility(View.VISIBLE);

        if (theme == Theme.light) {
            ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            ratingUsername.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            ratingComment.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            centerLine.setBackground(ContextCompat.getDrawable(ctx, R.color.cream_tertiary));
        } else {
            ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
            ratingUsername.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            ratingComment.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            centerLine.setBackground(ContextCompat.getDrawable(ctx, R.color.purple_secondary));
        }

        if (!rating.username.isEmpty()) ratingUsername.setText(rating.username);
        else {
            switch (language) {
                case german:
                    ratingUsername.setText(ContextCompat.getString(ctx, R.string.anonymous_de));
                    break;
                case croatian:
                    ratingUsername.setText(ContextCompat.getString(ctx, R.string.anonymous_hr));
                    break;
                default:
                    ratingUsername.setText(ContextCompat.getString(ctx, R.string.anonymous_en));
            }
        }

        ratingBar.setRating((float)rating.rating);
    }
}
