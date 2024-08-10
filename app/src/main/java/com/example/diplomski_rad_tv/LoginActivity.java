package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    FirebaseFirestore firestore;
    SharedPreferencesService sharedPreferencesService;
    View focusedView;
    Language language;
    Theme theme;
    Clock format = Clock.h12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));

        // this.sharedPreferencesService.clearAllInfo();

        String userId = sharedPreferencesService.getUserId();
        String estateId = sharedPreferencesService.getEstateId();

        if (!userId.isEmpty()) {
            if (!estateId.isEmpty()) startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
            else startActivity(new Intent(getApplicationContext(), EstateListActivity.class));
            return;
        }

        this.firestore = FirebaseFirestore.getInstance();

        this.language = sharedPreferencesService.getLanguage();
        // if (this.language == null) this.getDeviceLanguage(); // Default set to Language.english, so no need for this functionality (for now).
        this.theme = sharedPreferencesService.getTheme();

        ConstraintLayout background = findViewById(R.id.main);
        this.setupBackground(getApplicationContext(), background, this.theme);

        TextView loginTitle = findViewById(R.id.loginTitle);
        this.setupLoginTitle(getApplicationContext(), loginTitle, this.language, this.theme);

        EditText emailField = findViewById(R.id.emailAddress);
        if (emailField != null) this.focusedView = emailField;
        this.setupContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = LoginNavigation.navigateOverActivity(focusedView.getId(), keyCode - 19);
            if (newFocusedViewId == 0) return false;

            focusedView = findViewById(newFocusedViewId);
            focusedView.requestFocus();

            // Remove focus from old View
            int row = LoginNavigation.getRowWithId(oldFocusedViewId);
            this.updateView(row);

            // Add focus to new View
            row = LoginNavigation.getRowWithId(newFocusedViewId);
            this.updateView(row);
        }
        return true;
    }

    void setupBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
    }

    void setupLoginTitle(Context ctx, TextView loginTitle, Language language, Theme theme) {
        if (loginTitle == null) return;

        switch (language) {
            case german:
                loginTitle.setText(R.string.login_de);
                break;
            case croatian:
                loginTitle.setText(R.string.login_hr);
                break;
            default:
                loginTitle.setText(R.string.login_en);
                break;
        }

        if (theme == Theme.light) loginTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else loginTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    public void updateView(int row) {
        switch (row) {
            case 0:
                EditText emailField = findViewById(R.id.emailAddress);
                TextView emailFieldTitle = findViewById(R.id.emailAddressTitle);
                LoginNavigation.setupEmailField(getApplicationContext(), emailField, emailFieldTitle, this.focusedView, this.language, this.theme);
                break;
            case 1:
                EditText passwordField = findViewById(R.id.password);
                TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
                LoginNavigation.setupPasswordField(getApplicationContext(), passwordField, passwordFieldTitle, this.focusedView, this.language, this.theme);
                break;
            case 2:
                Button loginButton = findViewById(R.id.loginButton);
                LoginNavigation.setupLoginButton(getApplicationContext(), loginButton, this.focusedView, this.language, this.theme);

                if (!loginButton.hasOnClickListeners()) {
                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String email = ((EditText) findViewById(R.id.emailAddress)).getText().toString();
                            String password = ((EditText) findViewById(R.id.password)).getText().toString();

                            try {
                                // Create a MessageDigest instance for SHA-256
                                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

                                // Convert input string to bytes and update the digest
                                byte[] hashBytes = messageDigest.digest(password.getBytes());

                                // Convert the hash bytes to a hex string
                                StringBuilder hexString = new StringBuilder();
                                for (byte b : hashBytes) {
                                    String hex = Integer.toHexString(0xff & b);
                                    if (hex.length() == 1) {
                                        hexString.append('0');
                                    }
                                    hexString.append(hex);
                                }

                                password = hexString.toString();
                            } catch (NoSuchAlgorithmException e) {
                                return; // throw new RuntimeException("SHA-256 algorithm not found", e);
                            }

                            String emptyEmailFeedback, emptyPasswordFeedback, incorrectEmailOrPasswordFeedback, firebaseError, blockedStatus, bannedStatus;
                            switch (language) {
                                case german:
                                    emptyEmailFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_empty_de);
                                    emptyPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.password_empty_de);
                                    incorrectEmailOrPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_or_password_incorrect_de);
                                    firebaseError = ContextCompat.getString(getApplicationContext(), R.string.firebase_error_de);
                                    blockedStatus = ContextCompat.getString(getApplicationContext(), R.string.blocked_status_de);
                                    bannedStatus = ContextCompat.getString(getApplicationContext(), R.string.banned_status_de);
                                    break;
                                case croatian:
                                    emptyEmailFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_empty_hr);
                                    emptyPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.password_empty_hr);
                                    incorrectEmailOrPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_or_password_incorrect_hr);
                                    firebaseError = ContextCompat.getString(getApplicationContext(), R.string.firebase_error_hr);
                                    blockedStatus = ContextCompat.getString(getApplicationContext(), R.string.blocked_status_hr);
                                    bannedStatus = ContextCompat.getString(getApplicationContext(), R.string.banned_status_hr);
                                    break;
                                default:
                                    emptyEmailFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_empty_en);
                                    emptyPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.password_empty_en);
                                    incorrectEmailOrPasswordFeedback = ContextCompat.getString(getApplicationContext(), R.string.email_or_password_incorrect_en);
                                    firebaseError = ContextCompat.getString(getApplicationContext(), R.string.firebase_error_en);
                                    blockedStatus = ContextCompat.getString(getApplicationContext(), R.string.blocked_status_en);
                                    bannedStatus = ContextCompat.getString(getApplicationContext(), R.string.banned_status_en);
                                    break;
                            }

                            firestoreLogin(getApplicationContext(), email, password, emptyEmailFeedback, emptyPasswordFeedback, incorrectEmailOrPasswordFeedback, firebaseError, blockedStatus, bannedStatus, sharedPreferencesService.sharedPreferences);
                        }
                    });
                }
                break;
        }
    }

    public void firestoreLogin(Context ctx, String email, String password, String emptyEmailFeedback, String emptyPasswordFeedback, String incorrectEmailOrPasswordFeedback, String firebaseError, String blockedStatus, String bannedStatus, SharedPreferences sharedPreferences) {
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

                        boolean blocked = documents.get(0).getBoolean("blocked");
                        boolean banned = documents.get(0).getBoolean("banned");

                        if (blocked) {
                            Toast.makeText(ctx, blockedStatus, Toast.LENGTH_LONG).show();
                            return;
                        } else if (banned) {
                            Toast.makeText(ctx, bannedStatus, Toast.LENGTH_LONG).show();
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

    void setupContent() {
        for (int i = 0; i <= 2; ++i) this.updateView(i);
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
