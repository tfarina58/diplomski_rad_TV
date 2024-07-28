package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class CenterText {
    public static void setupCenterText(Context ctx, TextView centerText, Language language, Theme theme, boolean loadingInProgress, int estatesToShowSize, String activity) {
        if (centerText == null) return;

        if (loadingInProgress || estatesToShowSize > 0) {
            centerText.setVisibility(View.INVISIBLE);
            centerText.setText(ContextCompat.getString(ctx, R.string.empty_string));
            centerText.setTextColor(ContextCompat.getColor(ctx, R.color.transparent));
        } else if (estatesToShowSize == 0) {
            centerText.setVisibility(View.VISIBLE);

            switch (activity) {
                case "estates":
                    switch (language) {
                        case german:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_estates_de));
                            break;
                        case croatian:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_estates_hr));
                            break;
                        default:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_estates_en));
                    }
                    break;
                case "categories":
                    switch (language) {
                        case german:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_categories_de));
                            break;
                        case croatian:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_categories_hr));
                            break;
                        default:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_categories_en));
                    }
                    break;
                case "elements":
                    switch (language) {
                        case german:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_elements_de));
                            break;
                        case croatian:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_elements_hr));
                            break;
                        default:
                            centerText.setText(ContextCompat.getString(ctx, R.string.no_elements_en));
                    }
                    break;
            }

            if (theme == Theme.light) centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }

    public static void setupCenterText(Context ctx, TextView centerText, Language language, Theme theme, boolean loadingInProgress, int ratingsToShowSize) {
        if (centerText == null) return;

        if (loadingInProgress || ratingsToShowSize > 0) {
            centerText.setVisibility(View.INVISIBLE);
            centerText.setText(ContextCompat.getString(ctx, R.string.empty_string));
            centerText.setTextColor(ContextCompat.getColor(ctx, R.color.transparent));
        } else if (ratingsToShowSize == 0) {
            centerText.setVisibility(View.VISIBLE);

            switch (language) {
                case german:
                    centerText.setText(ContextCompat.getString(ctx, R.string.no_ratings_de));
                    break;
                case croatian:
                    centerText.setText(ContextCompat.getString(ctx, R.string.no_ratings_hr));
                    break;
                default:
                    centerText.setText(ContextCompat.getString(ctx, R.string.no_ratings_en));
            }

            if (theme == Theme.light) centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }
}
