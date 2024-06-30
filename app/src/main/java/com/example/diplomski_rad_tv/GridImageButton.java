package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class GridImageButton {
    public static void setupImageButton(Context ctx, ImageButton imageButton, Button imageBackground, TextView imageTitle, View focusedView, Theme theme, Estate estate) {
        if (imageButton == null || imageBackground == null || imageTitle == null) return;

        int visibility = estate == null ? View.INVISIBLE: View.VISIBLE;
        String titleText = estate == null ? "" : estate.name;

        if (focusedView.getId() == imageButton.getId()) {
            if (theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
            else imageBackground.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
        } else imageBackground.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));

        imageTitle.setText(titleText);
        if (theme == Theme.light) imageTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else imageTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));

        imageButton.setVisibility(visibility);
        imageTitle.setVisibility(visibility);
        imageBackground.setVisibility(visibility);

        if (visibility == View.VISIBLE && estate.image != null && !estate.image.isEmpty()) {
            try {
                Picasso.get()
                        .load(estate.image)
                        .fit()
                        .into(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } // TODO: else set color when no image can be displayed
    }
}
