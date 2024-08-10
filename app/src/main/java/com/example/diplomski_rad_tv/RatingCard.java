package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import java.util.Date;

public class RatingCard {
    public static void setupRatingCard(Context ctx, Button ratingButton, TextView ratingUsername, RatingBar ratingBar, Button centerLine, TextView ratingComment, TextView ratingTimestamp, Rating rating, Language language, Theme theme) {
        if (ratingButton == null || ratingUsername == null || ratingBar == null || ratingComment == null || ratingTimestamp == null) return;

        if (rating == null) {
            ratingButton.setVisibility(View.GONE);
            ratingUsername.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
            centerLine.setVisibility(View.GONE);
            ratingComment.setVisibility(View.GONE);
            ratingTimestamp.setVisibility(View.GONE);
            return;
        }

        ratingButton.setVisibility(View.VISIBLE);
        ratingUsername.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        centerLine.setVisibility(View.VISIBLE);
        ratingComment.setVisibility(View.VISIBLE);
        ratingTimestamp.setVisibility(View.VISIBLE);

        if (theme == Theme.light) {
            ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            ratingUsername.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            ratingComment.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            centerLine.setBackground(ContextCompat.getDrawable(ctx, R.color.cream_tertiary));
            ratingTimestamp.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        } else {
            ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
            ratingUsername.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            ratingComment.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            centerLine.setBackground(ContextCompat.getDrawable(ctx, R.color.purple_secondary));
            ratingTimestamp.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
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
        ratingComment.setText(rating.comment);

        Date createdDate = rating.created.toDate();
        ratingTimestamp.setText(addZeroPrefix(createdDate.getDate()) + "." + addZeroPrefix(createdDate.getMonth()) + "." + (2000 + createdDate.getYear() % 100) + ".");
    }

    static String addZeroPrefix(int timeValue) {
        String timeValueString = "0" + timeValue;
        timeValueString = timeValueString.substring(timeValueString.length() - 2);
        return timeValueString;
    }
}
