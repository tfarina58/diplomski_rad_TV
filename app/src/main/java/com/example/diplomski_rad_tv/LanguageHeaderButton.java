package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class LanguageHeaderButton {
    public static void setupLanguageButton(Context ctx, Button languageButton, ImageView languageIcon, View focusedView, Language language, Theme theme) {
        if (languageButton == null || languageIcon == null) return;

        switch (language) {
            case german:
                languageButton.setText(R.string.language_de);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.german));
                break;
            case croatian:
                languageButton.setText(R.string.language_hr);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.croatian));
                break;
            default:
                languageButton.setText(R.string.language_en);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.english));
        }

        if (focusedView.getId() == R.id.languageButton) {
            if (theme == Theme.light) languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else languageButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) languageButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else languageButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
