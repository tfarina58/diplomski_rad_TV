package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class RatingHeaderButton {
    public static void setupRatingButton(Context ctx, Button ratingButton, ImageView ratingIcon, View focusedView, Language language) {
        if (ratingButton == null || ratingIcon == null) return;

        switch (language) {
            case german:
                ratingButton.setText(R.string.rating_de);
                break;
            case croatian:
                ratingButton.setText(R.string.rating_hr);
                break;
            default:
                ratingButton.setText(R.string.rating_en);
        }

        ratingIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.rating));

        if (focusedView.getId() == R.id.ratingButton) ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else ratingButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }
}
