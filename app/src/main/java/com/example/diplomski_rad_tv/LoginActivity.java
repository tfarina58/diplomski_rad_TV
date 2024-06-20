package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

public class LoginActivity extends Activity {
    FirebaseFirestore firestore;
    SharedPreferencesService service;
    View focusedView;
    Language language;
    Theme theme;
    Clock format = Clock.h12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        SharedPreferencesService sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));

        String userId = sharedPreferencesService.getUserId();
        String estateId = sharedPreferencesService.getEstateId();
        if (!userId.isEmpty() && !estateId.isEmpty()) {
            Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(i);
            return;
        } // TODO: remove comment

        this.firestore = FirebaseFirestore.getInstance();

        this.language = sharedPreferencesService.getLanguage();
        // if (this.language == null) this.getDeviceLanguage(); // Default set to Language.english, so no need for this functionality (for now).

        TextView loginTitle = findViewById(R.id.loginTitle);
        this.setupLoginTitle(loginTitle);

        EditText emailField = findViewById(R.id.emailAddress);
        TextView emailFieldTitle = findViewById(R.id.emailAddressTitle);
        focusedView = emailField;
        LoginNavigation.setupEmailField(getApplicationContext(), emailField, emailFieldTitle, this.focusedView, this.language);

        EditText passwordField = findViewById(R.id.password);
        TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
        LoginNavigation.setupPasswordField(getApplicationContext(), passwordField, passwordFieldTitle, this.focusedView, this.language);

        Button loginButton = findViewById(R.id.loginButton);
        LoginNavigation.setupLoginButton(getApplicationContext(), loginButton, this.focusedView, this.language);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "tfarina58@gmail.com"; // ((EditText) findViewById(R.id.emailAddress)).getText().toString();
                String password = "password"; // ((EditText) findViewById(R.id.password)).getText().toString();

                String emptyEmailFeedback, emptyPasswordFeedback, incorrectEmailOrPasswordFeedback, firebaseError;
                switch (language) {
                    case german:
                        emptyEmailFeedback = getResources().getString(R.string.email_empty_de);
                        emptyPasswordFeedback = getResources().getString(R.string.password_empty_de);
                        incorrectEmailOrPasswordFeedback = getResources().getString(R.string.email_or_password_incorrect_de);
                        firebaseError = getResources().getString(R.string.firebase_error_de);
                        break;
                    case croatian:
                        emptyEmailFeedback = getResources().getString(R.string.email_empty_hr);
                        emptyPasswordFeedback = getResources().getString(R.string.password_empty_hr);
                        incorrectEmailOrPasswordFeedback = getResources().getString(R.string.email_or_password_incorrect_hr);
                        firebaseError = getResources().getString(R.string.firebase_error_hr);
                        break;
                    default:
                        emptyEmailFeedback = getResources().getString(R.string.email_empty_en);
                        emptyPasswordFeedback = getResources().getString(R.string.password_empty_en);
                        incorrectEmailOrPasswordFeedback = getResources().getString(R.string.email_or_password_incorrect_en);
                        firebaseError = getResources().getString(R.string.firebase_error_en);
                        break;
                }

                firestoreLogin(getApplicationContext(), email, password, emptyEmailFeedback, emptyPasswordFeedback, incorrectEmailOrPasswordFeedback, firebaseError, sharedPreferencesService.sharedPreferences);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode == 4) {
            this.focusedView.clearFocus();
            this.focusedView.setFocusable(false);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(this.focusedView.getWindowToken(), 0);
            }
            // TODO

        } else if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = LoginNavigation.navigateOverActivity(focusedView.getId(), keyCode - 19);
            if (newFocusedViewId == 0) return false;

            focusedView = findViewById(newFocusedViewId);

            // Remove focus from old View
            int row = LoginNavigation.getRowWithId(oldFocusedViewId);
            this.updateView(row);

            // Add focus to new View
            row = LoginNavigation.getRowWithId(newFocusedViewId);
            this.updateView(row);
        }
        // Enter button
        else if (keyCode == 23) {
            if (oldFocusedViewId == R.id.languageButton) {
                language = language.next();
                focusedView = findViewById(R.id.languageButton);
                // refreshContentView();
            } else if (oldFocusedViewId == R.id.themeButton) {
                theme = theme.next();
                focusedView = findViewById(R.id.themeButton);
                // refreshContentView();
            } else if (oldFocusedViewId == R.id.textClock) {
                format = format.next();
                focusedView = findViewById(R.id.textClock);
                switch (format) {
                    case h24:
                        ((TextClock)focusedView).setFormat12Hour("HH:mm:ss");
                        break;
                    case h12:
                        ((TextClock)focusedView).setFormat12Hour("hh:mm:ss a");
                        break;
                }
            } else if (oldFocusedViewId == R.id.emailAddress) {
                EditText email = findViewById(R.id.emailAddress);
                email.setFocusable(true);
                email.requestFocus();
                // TODO

            } else if (oldFocusedViewId == R.id.password) {
                EditText email = findViewById(R.id.emailAddress);
                email.setFocusable(true);
                email.requestFocus();
                // TODO

            } else if (oldFocusedViewId == R.id.loginButton) {
                // TODO: nothing should happen
            }
        }
        return true;
    }

    void setupLoginTitle(TextView loginTitle) {
        if (loginTitle == null) return;

        switch (language) {
            case english:
                loginTitle.setText(R.string.login_en);
                break;
            case german:
                loginTitle.setText(R.string.login_de);
                break;
            case croatian:
                loginTitle.setText(R.string.login_hr);
                break;
        }
    }

    public void updateView(int row) {
        switch (row) {
            case 0:
                EditText emailField = findViewById(R.id.emailAddress);
                TextView emailFieldTitle = findViewById(R.id.emailAddressTitle);
                LoginNavigation.setupEmailField(getApplicationContext(), emailField, emailFieldTitle, this.focusedView, this.language);
                break;
            case 1:
                EditText passwordField = findViewById(R.id.password);
                TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
                LoginNavigation.setupPasswordField(getApplicationContext(), passwordField, passwordFieldTitle, this.focusedView, this.language);
                break;
            case 2:
                Button loginButton = findViewById(R.id.loginButton);
                LoginNavigation.setupLoginButton(getApplicationContext(), loginButton, this.focusedView, this.language);
                break;
        }
    }

    public void firestoreLogin(Context ctx, String email, String password, String emptyEmailFeedback, String emptyPasswordFeedback, String incorrectEmailOrPasswordFeedback, String firebaseError, SharedPreferences sharedPreferences) {
        if (email.isEmpty()) Toast.makeText(ctx, emptyEmailFeedback, Toast.LENGTH_LONG).show();
        if (password.isEmpty()) Toast.makeText(ctx, emptyPasswordFeedback, Toast.LENGTH_LONG).show();

        Query query = this.firestore.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        if (documents.size() != 1) {
                            Toast.makeText(ctx, incorrectEmailOrPasswordFeedback, Toast.LENGTH_LONG).show();
                            return;
                        }

                        String documentId = documents.get(0).getId();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId", documentId);
                        editor.apply();

                        String userId = sharedPreferences.getString("userId", "");
                        if (userId.isEmpty()) return;

                        Intent i = new Intent(getApplicationContext(), EstateListActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ctx, firebaseError, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public Language getDeviceLanguage() {
        String languageCode = Locale.getDefault().getLanguage();
        switch (languageCode) {
            case "de":
                return Language.german;
            case "hr":
                return Language.croatian;
            default:
                return Language.english;
        }
    }
}
