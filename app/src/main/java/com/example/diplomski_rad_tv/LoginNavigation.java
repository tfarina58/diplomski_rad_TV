package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class LoginNavigation {
    private static final int[][] navigationLogin = {
            {0,                 R.id.password,    0, 0},  // emailAddress
            {R.id.emailAddress, R.id.loginButton, 0, 0},  // password
            {R.id.password,     0,                0, 0},  // loginButton
    };
    public static int navigateOverActivity(int currentViewId, int direction) {
        return navigationLogin[getRowWithId(currentViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.emailAddress) return 0;
        if (currentViewId == R.id.password) return 1;
        if (currentViewId == R.id.loginButton) return 2;
        return -1;
    }

    public static void setupEmailField(Context ctx, EditText emailField, TextView emailFieldTitle, View focusedView, Language language, Theme theme) {
        if (emailField == null || emailFieldTitle == null) return;

        switch (language) {
            case german:
                emailFieldTitle.setText(R.string.email_de);
                break;
            case croatian:
                emailFieldTitle.setText(R.string.email_hr);
                break;
            default:
                emailFieldTitle.setText(R.string.email_en);
        }

        if (focusedView.getId() == R.id.emailAddress) {
            if (theme == Theme.light) {
                emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
                emailField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
                emailFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                emailFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            } else {
                emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
                emailField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
                emailFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                emailFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            }
        } else {
            if (theme == Theme.light) {
                emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
                emailField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
                emailFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                emailFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            } else {
                emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
                emailField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
                emailFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                emailFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            }
        }
    }

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
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            } else {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            }
        } else {
            if (theme == Theme.light) {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            } else {
                passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
                passwordField.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
                passwordFieldTitle.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
                passwordFieldTitle.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            }
        }
    }

    public static void setupLoginButton(Context ctx, Button loginButton, View focusedView, Language language, Theme theme) {
        if (loginButton == null) return;

        switch (language) {
            case german:
                loginButton.setText(R.string.login_de);
                break;
            case croatian:
                loginButton.setText(R.string.login_hr);
                break;
            default:
                loginButton.setText(R.string.login_en);
        }

        if (focusedView.getId() == R.id.loginButton) {
            if (theme == Theme.light) loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) loginButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else loginButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
