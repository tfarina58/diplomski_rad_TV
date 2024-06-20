package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class LoginNavigation {
    private static int[][] navigationLogin = {
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

    public static void setupEmailField(Context ctx, EditText emailField, TextView emailFieldTitle, View focusedView, Language language) {
        if (emailField == null || emailFieldTitle == null) return;

        emailField.clearFocus();

        switch (language) {
            case english:
                emailFieldTitle.setText(R.string.email_en);
                break;
            case german:
                emailFieldTitle.setText(R.string.email_de);
                break;
            case croatian:
                emailFieldTitle.setText(R.string.email_hr);
                break;
        }

        if (focusedView.getId() == R.id.emailAddress) emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design_focused));
        else emailField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design));
    }

    public static void setupPasswordField(Context ctx, EditText passwordField, TextView passwordFieldTitle, View focusedView, Language language) {
        if (passwordField == null || passwordFieldTitle == null) return;

        switch (language) {
            case english:
                passwordFieldTitle.setText(R.string.password_en);
                break;
            case german:
                passwordFieldTitle.setText(R.string.password_de);
                break;
            case croatian:
                passwordFieldTitle.setText(R.string.password_hr);
                break;
        }

        if (focusedView.getId() == R.id.password) passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design_focused));
        else passwordField.setBackground(ContextCompat.getDrawable(ctx, R.drawable.edittext_design));
    }

    public static void setupLoginButton(Context ctx, Button loginButton, View focusedView, Language language) {
        if (loginButton == null) return;

        switch (language) {
            case english:
                loginButton.setText(R.string.login_en);
                break;
            case german:
                loginButton.setText(R.string.login_de);
                break;
            case croatian:
                loginButton.setText(R.string.login_hr);
                break;
        }

        if (focusedView.getId() == R.id.loginButton) loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design_focused));
        else loginButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.button_design));
    }
}
