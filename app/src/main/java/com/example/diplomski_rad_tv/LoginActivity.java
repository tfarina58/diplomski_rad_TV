package com.example.diplomski_rad_tv;

import android.app.Activity;
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
import androidx.core.content.ContextCompat;

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
    View focusedView;
    Language language;
    Theme theme;
    Clock format = Clock.h12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);

        String userId = sharedPreferences.getString("userId", "");
        String estateId = sharedPreferences.getString("estateId", "");
        /*if (!userId.isEmpty() && !estateId.isEmpty()) {
            Intent i = new Intent(getApplicationContext(), Welcome.class);
            startActivity(i);
            return;
        }*/ // TODO: remove comment

        this.firestore = FirebaseFirestore.getInstance();
        Button loginButton = findViewById(R.id.loginButton);

        String languageCode = sharedPreferences.getString("language", "");
        if (languageCode.isEmpty())  languageCode = Locale.getDefault().getLanguage();

        switch (languageCode) {
            case "en":
                this.language = Language.united_kingdom;
                break;
            case "de":
                this.language = Language.germany;
                break;
            case "hr":
                this.language = Language.croatia;
                break;
        }

        this.setupLoginTitle();
        this.setupEmailField();
        this.setupPasswordField();
        this.setupLoginButton();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "tfarina58@gmail.com"; // ((EditText) findViewById(R.id.emailAddress)).getText().toString();
                String password = "password"; // ((EditText) findViewById(R.id.password)).getText().toString();

                if (email.isEmpty()) Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_empty_en), Toast.LENGTH_LONG);
                if (password.isEmpty()) Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_empty_en), Toast.LENGTH_LONG);

                Query query = firestore.collection("users").whereEqualTo("email", email).whereEqualTo("password", password);

                query.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            if (documents.size() == 1) {
                                String documentId = documents.get(0).getId();

                                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", documentId);
                                editor.apply();

                                Intent i = new Intent(getApplicationContext(), EstateListActivity.class);
                                startActivity(i);
                            } else
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.email_or_password_incorrect_en), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.firebase_error_en), Toast.LENGTH_LONG).show();
                        }
                    });
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
            updateView(row);

            // Add focus to new View
            row = LoginNavigation.getRowWithId(newFocusedViewId);
            updateView(row);
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
                EditText email = findViewById(R.id.emailAddress);
                email.setFocusable(true);
                email.requestFocus();
                // TODO

            }
        }
        return true;
    }

    void updateView(int row) {
        if (row == 0) setupEmailField();
        else if (row == 1) setupPasswordField();
        else if (row == 2) setupLoginButton();
    }

    void setupEmailField() {
        EditText emailField = findViewById(R.id.emailAddress);
        TextView emailFieldTitle = findViewById(R.id.emailAddressTitle);

        if (this.focusedView == null) {
            this.focusedView = emailField;
        }

        if (emailField == null || emailFieldTitle == null) return;
        emailField.clearFocus();

        switch (language) {
            case united_kingdom:
                emailFieldTitle.setText(R.string.email_en);
                break;
            case germany:
                emailFieldTitle.setText(R.string.email_de);
                break;
            case croatia:
                emailFieldTitle.setText(R.string.email_hr);
                break;
        }

        if (this.focusedView.getId() == R.id.emailAddress) emailField.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_design_focused));
        else emailField.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_design));
    }

    void setupPasswordField() {
        EditText passwordField = findViewById(R.id.password);
        TextView passwordFieldTitle = findViewById(R.id.passwordTitle);

        if (passwordField == null || passwordFieldTitle == null) return;
        switch (language) {
            case united_kingdom:
                passwordFieldTitle.setText(R.string.password_en);
                break;
            case germany:
                passwordFieldTitle.setText(R.string.password_de);
                break;
            case croatia:
                passwordFieldTitle.setText(R.string.password_hr);
                break;
        }

        if (this.focusedView.getId() == R.id.password) passwordField.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_design_focused));
        else passwordField.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_design));
    }

    void setupLoginTitle() {
        TextView loginTitle = findViewById(R.id.loginTitle);
        if (loginTitle == null) return;

        switch (language) {
            case united_kingdom:
                loginTitle.setText(R.string.login_en);
                break;
            case germany:
                loginTitle.setText(R.string.login_de);
                break;
            case croatia:
                loginTitle.setText(R.string.login_hr);
                break;
        }
    }

    void setupLoginButton() {
        Button loginButton = findViewById(R.id.loginButton);

        if (loginButton == null) return;
        switch (language) {
            case united_kingdom:
                loginButton.setText(R.string.login_en);
                break;
            case germany:
                loginButton.setText(R.string.login_de);
                break;
            case croatia:
                loginButton.setText(R.string.login_hr);
                break;
        }

        if (this.focusedView.getId() == R.id.loginButton) loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_design_focused));
        else loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_design));
    }
}
