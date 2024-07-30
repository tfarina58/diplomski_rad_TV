package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class ThemeHeaderButton {
    public static void setupThemeButton(Context ctx, Button themeButton, ImageView themeIcon, View focusedView, Language language, Theme theme) {
        if (themeButton == null || themeIcon == null) return;

        if (theme == Theme.light) {
            switch (language) {
                case german:
                    themeButton.setText(R.string.light_theme_de);
                    break;
                case croatian:
                    themeButton.setText(R.string.light_theme_hr);
                    break;
                default:
                    themeButton.setText(R.string.light_theme_en);
            }

            themeIcon.setBackground(ContextCompat.getDrawable(ctx, R.drawable.sun));
        } else {
            switch (language) {
                case german:
                    themeButton.setText(R.string.dark_theme_de);
                    break;
                case croatian:
                    themeButton.setText(R.string.dark_theme_hr);
                    break;
                default:
                    themeButton.setText(R.string.dark_theme_en);
            }

            themeIcon.setBackground(ContextCompat.getDrawable(ctx, R.drawable.moon));
        }

        if (focusedView.getId() == R.id.themeButton) {
            if (theme == Theme.light) themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else themeButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) themeButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else themeButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}