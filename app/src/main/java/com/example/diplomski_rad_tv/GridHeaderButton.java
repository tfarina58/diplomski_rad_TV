package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class GridHeaderButton {
    public static void setupGridButton(Context ctx, Button gridButton, ImageView gridIcon, View focusedView, Language language) {
        if (gridButton == null || gridIcon == null) return;

        if (language == Language.english) gridButton.setText(R.string.grid_en);
        else if (language == Language.german) gridButton.setText(R.string.grid_de);
        else if (language == Language.croatian) gridButton.setText(R.string.grid_hr);

        gridIcon.setBackground(ContextCompat.getDrawable(ctx, R.drawable.grid));

        if (focusedView.getId() == R.id.gridButton) gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else gridButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }
}
