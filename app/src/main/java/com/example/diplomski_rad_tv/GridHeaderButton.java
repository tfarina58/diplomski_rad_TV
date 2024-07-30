package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class GridHeaderButton {
    public static void setupGridButton(Context ctx, Button gridButton, ImageView gridIcon, View focusedView, Language language, Theme theme) {
        if (gridButton == null || gridIcon == null) return;

        if (language == Language.english) gridButton.setText(R.string.grid_en);
        else if (language == Language.german) gridButton.setText(R.string.grid_de);
        else if (language == Language.croatian) gridButton.setText(R.string.grid_hr);

        gridIcon.setBackground(ContextCompat.getDrawable(ctx, R.drawable.grid));

        if (focusedView.getId() == R.id.gridButton) {
            if (theme == Theme.light) gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) gridButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else gridButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
