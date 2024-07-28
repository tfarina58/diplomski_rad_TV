package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class EnterPasswordLayout {
    public static void setupPasswordField(Context ctx, EditText passwordField, TextView passwordFieldTitle, View focusedView, Language language, Theme theme) {
        if (passwordField == null || passwordFieldTitle == null) return;

        switch (language) {
            case german:
                passwordFieldTitle.setText(R.string.password_de);
                break;
            case croatian:
                passwordFieldTitle.setText(R.string.password_hr);
                break;
            default:
                passwordFieldTitle.setText(R.string.password_en);
        }

        if (focusedView.getId() == R.id.password) {
            if (theme == Theme.light) {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design_focused_light));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            } else {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design_focused_dark));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            }
        } else {
            if (theme == Theme.light) {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            } else {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            }
        }
    }

    public static void setupCancelButton(Context ctx, Button button, View focusedView, Language language) {
        if (button == null) return;

        button.setAllCaps(false);
        switch (language) {
            case german:
                button.setText(R.string.cancel_de);
                break;
            case croatian:
                button.setText(R.string.cancel_hr);
                break;
            default:
                button.setText(R.string.cancel_en);
        }

        if (focusedView.getId() == R.id.cancelButtonPassword) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }

    public static void setupSubmitButton(Context ctx, Button button, View focusedView, Language language) {
        if (button == null) return;

        button.setAllCaps(false);
        switch (language) {
            case german:
                button.setText(R.string.submit_password_de);
                break;
            case croatian:
                button.setText(R.string.submit_password_hr);
                break;
            default:
                button.setText(R.string.submit_password_en);
        }

        if (focusedView.getId() == R.id.submitButton) button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }

    public static void setupLayoutTitle(Context ctx, TextView enterPasswordTitle, Language language, Theme theme) {
        if (enterPasswordTitle == null) return;

        switch (language) {
            case german:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.enter_password_de));
                break;
            case croatian:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.enter_password_hr));
                break;
            default:
                enterPasswordTitle.setText(ContextCompat.getString(ctx, R.string.enter_password_en));
        }

        if (theme == Theme.light) enterPasswordTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else enterPasswordTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    public static void setupLayoutBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
    }
}
