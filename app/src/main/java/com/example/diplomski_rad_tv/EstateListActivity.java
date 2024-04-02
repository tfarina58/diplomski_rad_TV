package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class EstateListActivity extends Activity {
    Estate estate;
    Language language = Language.germany;
    Theme theme = Theme.light;
    Grid grid = Grid.one;
    Clock format = Clock.h12;
    BasicPageButton focusedButton;
    String searchbarText = "";
    int currentPage = 7;
    int totalPages = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setNewContentView(savedInstanceState);
    }

    void setNewContentView(Bundle savedInstanceState) {
        pickContentView();

        setupMain(savedInstanceState);
        setupLanguageButton(savedInstanceState);
        setupThemeButton(savedInstanceState);
        setupGridButton(savedInstanceState);
        setupTextClock(savedInstanceState);
        setupSearchBarButton(savedInstanceState);
        setupPaginationButton(savedInstanceState);
    }

    void pickContentView() {
        switch(grid) {
            case one:
                setContentView(R.layout.activity_basic_grid_1);
                break;
            case three:
                setContentView(R.layout.activity_basic_grid_3);
                break;
            case six:
                setContentView(R.layout.activity_basic_grid_6);
                break;
        }
    }

    void setupMain(Bundle savedInstanceState) {
        View main = findViewById(R.id.main);

        if (main == null) return;
        if (focusedButton == null) focusedButton = BasicPageButton.main;

        if (focusedButton == BasicPageButton.main) {}

        if (!main.hasOnClickListeners()) {
            /*main.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focused = main;
                    Toast.makeText(getApplicationContext(), "MAIN", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });*/

            main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    void setupLanguageButton(Bundle savedInstanceState) {
        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        switch (language) {
            case united_kingdom:
                languageButton.setText("English");
                languageIcon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.united_kingdom));
                break;
            case germany:
                languageButton.setText("Deutsche");
                languageIcon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.germany));
                break;
            case croatia:
                languageButton.setText("Hrvatski");
                languageIcon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.croatia));
                break;
        }
        if (focusedButton == BasicPageButton.language) languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!languageButton.hasOnClickListeners()) {
            languageButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.language;
                    Toast.makeText(getApplicationContext(), "LANGUAGE", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            languageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    focusedButton = BasicPageButton.language;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }

    void setupThemeButton(Bundle savedInstanceState) {
        Button themeButton = findViewById(R.id.themeButton);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        if (themeButton == null) return;
        switch (theme) {
            case light:
                themeButton.setText("Light");
                themeIcon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sun));
                break;
            case dark:
                themeButton.setText("Dark");
                themeIcon.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                break;
        }
        if (focusedButton == BasicPageButton.theme) themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!themeButton.hasOnClickListeners()) {
            themeButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.theme;
                    Toast.makeText(getApplicationContext(), "THEME", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            themeButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    theme = theme.next();
                    focusedButton = BasicPageButton.theme;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }

    void setupGridButton(Bundle savedInstanceState) {
        Button gridButton = findViewById(R.id.gridButton);

        if (gridButton == null) return;

        if (focusedButton == BasicPageButton.grid) gridButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else gridButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!gridButton.hasOnClickListeners()) {
            gridButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.grid;
                    Toast.makeText(getApplicationContext(), "GRID", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            gridButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    grid = grid.next();
                    focusedButton = BasicPageButton.grid;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }

    void setupTextClock(Bundle savedInstanceState) {
        TextClock clockButton = findViewById(R.id.textClock);

        if (clockButton == null) return;
        switch (format) {
            case h24:
                clockButton.setFormat12Hour("HH:mm:ss");
                break;
            case h12:
                clockButton.setFormat12Hour("hh:mm:ss a");
                break;
            default:
                return;
        }
        if (focusedButton == BasicPageButton.clock) clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!clockButton.hasOnClickListeners()) {
            clockButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.clock;
                    Toast.makeText(getApplicationContext(), "CLOCK", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            clockButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    format = format.next();
                    focusedButton = BasicPageButton.clock;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }


    void setupSearchBarButton(Bundle savedInstanceState) {
        SearchView searchbarButton = findViewById(R.id.searchView);

        searchbarButton.setQuery(searchbarText, false);
        if (focusedButton == BasicPageButton.searchbar) searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!searchbarButton.hasOnClickListeners()) {
            searchbarButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.searchbar;
                    Toast.makeText(getApplicationContext(), "SEARCHBAR", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            searchbarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusedButton = BasicPageButton.searchbar;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }
    void setupPaginationButton(Bundle savedInstanceState) {
        Button paginationButton = findViewById(R.id.pageIndex);

        paginationButton.setText(currentPage + "/" + totalPages);
        if (focusedButton == BasicPageButton.pagination) paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));

        if (!paginationButton.hasOnClickListeners()) {
            paginationButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) focusedButton = BasicPageButton.pagination;
                    Toast.makeText(getApplicationContext(), "PAGINATION", Toast.LENGTH_SHORT).show();
                    setNewContentView(savedInstanceState);
                }
            });

            paginationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: open pagination modal or something
                    focusedButton = BasicPageButton.pagination;
                    setNewContentView(savedInstanceState);
                }
            });
        }
    }
}
