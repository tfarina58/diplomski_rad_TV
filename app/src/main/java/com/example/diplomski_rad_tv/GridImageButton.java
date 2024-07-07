package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

public class GridImageButton {
    public static void setupImageButton(Context ctx, ImageButton imageButton, Button imageBackground, TextView imageTitle, View focusedView, Language language, Theme theme, Estate estate) {
        if (imageButton == null || imageBackground == null || imageTitle == null) return;

        int visibility = estate == null ? View.INVISIBLE: View.VISIBLE;
        String titleText = null;
        switch (language) {
            case german:
                if (estate != null && estate.name != null && estate.name.get("de") != null) titleText = estate.name.get("de");
                else titleText = "";
                break;
            case croatian:
                if (estate != null && estate.name != null && estate.name.get("hr") != null) titleText = estate.name.get("hr");
                else titleText = "";
                break;
            default:
                if (estate != null && estate.name != null && estate.name.get("en") != null) titleText = estate.name.get("en");
                else titleText = "";
        }

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
        }
    }

    public static void setupImageButton(Context ctx, ImageButton imageButton, Button imageBackground, TextView imageTitle, View focusedView, Language language, Theme theme, Category category) {
        if (imageButton == null || imageBackground == null || imageTitle == null) return;

        int visibility = category == null ? View.INVISIBLE: View.VISIBLE;
        String titleText = null;
        switch (language) {
            case german:
                if (category != null && category.title != null && category.title.get("de") != null) titleText = category.title.get("de");
                else titleText = "";
                break;
            case croatian:
                if (category != null && category.title != null && category.title.get("hr") != null) titleText = category.title.get("hr");
                else titleText = "";
                break;
            default:
                if (category != null && category.title != null && category.title.get("en") != null) titleText = category.title.get("en");
                else titleText = "";
        }

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

        if (visibility == View.VISIBLE && category.image != null && !category.image.isEmpty()) {
            try {
                Picasso.get()
                        .load(category.image)
                        .fit()
                        .into(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setupImageButton(Context ctx, ImageButton imageButton, Button imageBackground, TextView imageTitle, View focusedView, Language language, Theme theme, Element element) {
        if (imageButton == null || imageBackground == null || imageTitle == null) return;

        int visibility = element == null ? View.INVISIBLE: View.VISIBLE;
        String titleText = null;
        switch (language) {
            case german:
                if (element != null && element.title != null && element.title.get("de") != null) titleText = element.title.get("de");
                else titleText = "";
                break;
            case croatian:
                if (element != null && element.title != null && element.title.get("hr") != null) titleText = element.title.get("hr");
                else titleText = "";
                break;
            default:
                if (element != null && element.title != null && element.title.get("en") != null) titleText = element.title.get("en");
                else titleText = "";
        }

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

        if (visibility == View.VISIBLE && element.background != null && !element.background.isEmpty()) {
            try {
                Picasso.get()
                        .load(element.background)
                        .fit()
                        .into(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
