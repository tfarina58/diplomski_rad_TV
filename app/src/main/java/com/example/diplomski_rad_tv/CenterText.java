package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class CenterText {
    public static void setupCenterText(Context ctx, TextView centerText, Language language, Theme theme, boolean loadingInProgress, int estatesToShowSize) {
        if (centerText == null) return;

        if (loadingInProgress || estatesToShowSize > 0) {
            centerText.setVisibility(View.INVISIBLE);
            centerText.setText(ContextCompat.getString(ctx, R.string.empty_string));
            centerText.setTextColor(ContextCompat.getColor(ctx, R.color.transparent));
        } else if (estatesToShowSize == 0) {
            centerText.setVisibility(View.INVISIBLE);

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

            if (theme == Theme.light) centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else centerText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }
}
