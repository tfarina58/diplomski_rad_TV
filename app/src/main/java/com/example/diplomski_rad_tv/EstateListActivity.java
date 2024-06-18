package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class EstateListActivity extends Activity {
    String userId;
    FirebaseFirestore firestore;
    Estate[] estates;
    ArrayList<Integer> estatesToShow;
    Language language = Language.united_kingdom;
    Theme theme = Theme.dark;
    BasicGridNavigation grid = BasicGridNavigation.one;
    Clock format = Clock.h12;
    View focusedView;
    String searchbarText = "";
    int overallIndex = 0;
    int currentIndex = 0;
    int currentPage = 0;
    int totalPages;
    boolean loadingInProgress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        this.userId = userId;

        this.estatesToShow = new ArrayList<>();

        this.firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("estates").whereEqualTo("ownerId", userId);

        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int i = 0;
                    estates = new Estate[queryDocumentSnapshots.size()];
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String estateId = document.getId();
                        String ownerId = document.getString("ownerId");
                        String name = document.getString("name");
                        String image = document.getString("image");
                        GeoPoint coordinates = document.getGeoPoint("coordinates");
                        HashMap<String, Object> variables = (HashMap<String, Object>) document.get("variables");
                        estates[i] = new Estate(estateId, ownerId, image, coordinates.getLatitude(), coordinates.getLongitude(), name, variables);
                        i++;
                    }
                    loadingInProgress = false;
                    setupEstatesToShow();
                    setNewContentView();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    estates = new Estate[0];
                    setupEstatesToShow();
                    setNewContentView();
                }
            });


        this.estates = new Estate[0];
        setupEstatesToShow();
        setNewContentView();
        this.setProgressBar();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = BasicGridNavigation.navigateOverActivity(this.grid, focusedView.getId(), keyCode - 19);

            if (newFocusedViewId == 0) return false;

            // Pressed up
            if (keyCode == 19) {
                if (BasicGridNavigation.isUpperButtons(oldFocusedViewId) || BasicGridNavigation.isFirstRow(oldFocusedViewId) || oldFocusedViewId == R.id.main) {
                    // Do nothing
                } else if (BasicGridNavigation.isSecondRow(oldFocusedViewId)) {
                    this.overallIndex -= 3;
                    this.currentIndex -= 3;
                } else if (BasicGridNavigation.isLowerButtons(oldFocusedViewId)) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                    if (this.estatesToShow.size() == 0) {
                        newFocusedViewId = R.id.languageButton;
                    }
                } else newFocusedViewId = 0;
            }
            // Pressed down
            else if (keyCode == 20) {
                if (BasicGridNavigation.isUpperButtons(oldFocusedViewId)) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                    if (this.estatesToShow.size() == 0) {
                        newFocusedViewId = R.id.searchView;
                    }
                } else if (oldFocusedViewId == R.id.main || BasicGridNavigation.isFirstRow(oldFocusedViewId)) {
                    if (this.grid == BasicGridNavigation.six && this.overallIndex + 3 < this.estatesToShow.size()) {
                        this.overallIndex += 3;
                        this.currentIndex += 3;
                    } else newFocusedViewId = R.id.searchView;
                } else if (BasicGridNavigation.isSecondRow(oldFocusedViewId)) {
                    newFocusedViewId = R.id.searchView;
                } else newFocusedViewId = 0;

            }
            // Pressed left
            if (keyCode == 21) {
                if (BasicGridNavigation.isUpperButtons(oldFocusedViewId) || BasicGridNavigation.isLowerButtons(oldFocusedViewId)) {
                    // Do nothing
                } else if (oldFocusedViewId == R.id.main || (this.grid == BasicGridNavigation.three && BasicGridNavigation.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex - 1 >= 0) {
                        this.overallIndex--;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (this.grid == BasicGridNavigation.six) {
                    if (BasicGridNavigation.isLeftColumn(oldFocusedViewId)) {
                        if (this.overallIndex - 4 >= 0) {
                            this.overallIndex -= 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (BasicGridNavigation.isMiddleColumn(oldFocusedViewId) || BasicGridNavigation.isRightColumn(oldFocusedViewId)) {
                        this.overallIndex--;
                        this.currentPage = this.overallIndex / gridType;
                        this.currentIndex = this.overallIndex % gridType;
                    }
                }
            }
            // Pressed right
            else if (keyCode == 22) {
                if (BasicGridNavigation.isUpperButtons(oldFocusedViewId) || BasicGridNavigation.isLowerButtons(oldFocusedViewId)) {
                    // Do nothing
                } else if (oldFocusedViewId == R.id.main || (this.grid == BasicGridNavigation.three && BasicGridNavigation.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex + 1 < estatesToShow.size()) {
                        this.overallIndex++;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (this.grid == BasicGridNavigation.six) {
                    if (BasicGridNavigation.isRightColumn(oldFocusedViewId)) {
                        if (this.overallIndex + 4 < estatesToShow.size()) {
                            this.overallIndex += 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (BasicGridNavigation.isLeftColumn(oldFocusedViewId) || BasicGridNavigation.isMiddleColumn(oldFocusedViewId)) {
                        if (this.overallIndex + 1 < estatesToShow.size()) {
                            this.overallIndex++;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                        } else newFocusedViewId = 0;
                    }
                }
            }

            if (newFocusedViewId != 0) {
                focusedView = findViewById(newFocusedViewId);

                // Remove focus from old View
                int row = BasicGridNavigation.getRowWithId(oldFocusedViewId);
                updateView(row);

                // Add focus to new View
                row = BasicGridNavigation.getRowWithId(newFocusedViewId);
                updateView(row);
            }
        }
        // Enter button
        else if (keyCode == 23) {
            if (oldFocusedViewId == R.id.languageButton) {
                language = language.next();
                focusedView = findViewById(R.id.languageButton);
                setNewContentView();
            } else if (oldFocusedViewId == R.id.themeButton) {
                theme = theme.next();
                focusedView = findViewById(R.id.themeButton);
                setNewContentView();
            } else if (oldFocusedViewId == R.id.gridButton) {
                this.grid = this.grid.next();
                focusedView = findViewById(R.id.gridButton);
                setNewContentView();
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
            } else if (oldFocusedViewId == R.id.searchView) {
                focusedView = findViewById(R.id.searchView);

                // TODO: calculate all variables and content to show
                ((SearchView)focusedView).requestFocus();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(((SearchView)focusedView), InputMethodManager.SHOW_IMPLICIT);
                }

                ((SearchView)focusedView).setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchbarText = query;
                        setupEstatesToShow();
                        setNewContentView();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return true;
                    }
                });

                // setNewContentView();
            } else if (oldFocusedViewId == R.id.pageIndex) {
                focusedView = findViewById(R.id.pageIndex);

                EditText pageIndexIndex = findViewById(R.id.pageIndexIndex);
                pageIndexIndex.requestFocus();

                pageIndexIndex.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            try {
                                String input = pageIndexIndex.getText().toString();
                                int pageIndexNumber = Integer.parseInt(input) - 1;
                                if (pageIndexNumber < 0 || pageIndexNumber >= totalPages) throw new Exception();
                                if (pageIndexNumber != currentPage) {
                                    currentPage = pageIndexNumber;
                                    overallIndex = BasicGridNavigation.getGridTypeAsInt(grid) * currentPage;
                                    currentIndex = 0;
                                }
                            } catch (Exception ex) {
                                switch (language) {
                                    case united_kingdom:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_page_number_en) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                                        break;
                                    case germany:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_page_number_de) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                                        break;
                                    case croatia:
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_page_number_hr) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                                        break;
                                }
                                pageIndexIndex.clearFocus();
                                return false;
                            }
                            pageIndexIndex.clearFocus();
                            setNewContentView();
                            return true; // Return true to indicate that the event has been handled
                        }
                        pageIndexIndex.clearFocus();
                        return false; // Return false to indicate that the event has not been handled
                    }
                });

                // setNewContentView();
            } else if (oldFocusedViewId == R.id.main || BasicGridNavigation.isFirstRow(oldFocusedViewId) || BasicGridNavigation.isSecondRow(oldFocusedViewId)) {
                this.navigateToCategoryListActivity();
            }
        }
        // else return keyCode != 4;

        Toast.makeText(getApplicationContext(), Integer.toString(overallIndex) + ", " + Integer.toString(currentIndex) + ", " + Integer.toString(currentPage) + ", " + Integer.toString(totalPages), Toast.LENGTH_SHORT).show();
        return true;
    }

    void setNewContentView() {
        setContentView();

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);

        this.currentPage = this.overallIndex / gridType;
        this.currentIndex = this.overallIndex % gridType;
        this.totalPages = (this.estatesToShow.size() - 1) / gridType + 1;

        if (this.grid == BasicGridNavigation.one) setupMain();
        else if (this.grid == BasicGridNavigation.three) {
            setupImageButton(1);
            setupImageButton(2);
            setupImageButton(3);
        } else {
            setupImageButton(1);
            setupImageButton(2);
            setupImageButton(3);
            setupImageButton(4);
            setupImageButton(5);
            setupImageButton(6);
        }

        setupLanguageButton();
        setupThemeButton();
        setupGridButton();
        setupTextClock();
        setupSearchBarButton();
        setupPaginationButton();
    }

    void setContentView() {
        switch (this.grid) {
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

    void setupMain() {
        ImageView main = findViewById(R.id.main);

        if (main == null) return;
        if (this.focusedView == null) {
            this.focusedView = main;
        }

        if (this.focusedView.getId() == R.id.main) {
            if (this.theme == Theme.dark) main.setBackground(getResources().getDrawable(R.drawable.main_border_dark));
            else if (this.theme == Theme.light) main.setBackground(getResources().getDrawable(R.drawable.main_border_light));
        } else {
            if (this.theme == Theme.light) main.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.light_theme));
            else if (this.theme == Theme.dark) main.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dark_theme));
        }


        if (this.loadingInProgress) {
            this.setTitleText(View.VISIBLE, R.string.real_estate_name_en, R.color.text_color_dark_mode);
            this.setCenterText(View.INVISIBLE, R.string.empty_string, R.color.transparent);
        } else if (this.estatesToShow.size() == 0 /*&& !this.loadingInProgress*/) {
            this.hideProgressBar();

            switch (this.language) {
                case united_kingdom:
                    if (this.theme == Theme.dark) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_en, R.color.text_color_dark_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_en, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_en, R.color.text_color_light_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_en, R.color.text_color_light_mode);
                    }
                    break;
                case germany:
                    if (this.theme == Theme.dark) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_de, R.color.text_color_dark_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_de, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_de, R.color.text_color_light_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_de, R.color.text_color_light_mode);
                    }
                    break;
                case croatia:
                    if (this.theme == Theme.dark) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_hr, R.color.text_color_dark_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_hr, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setTitleText(View.VISIBLE, R.string.real_estate_name_hr, R.color.text_color_light_mode);
                        this.setCenterText(View.VISIBLE, R.string.no_estates_hr, R.color.text_color_light_mode);
                    }
                    break;
            }

            return;

        } else if (this.overallIndex < this.estatesToShow.size()) {
            this.hideProgressBar();

            this.setCenterText(View.INVISIBLE, R.string.empty_string, R.color.transparent);
            if (this.theme == Theme.dark) this.setTitleText(View.VISIBLE, this.estates[this.estatesToShow.get(this.overallIndex)].name, R.color.text_color_dark_mode);
            else if (this.theme == Theme.light) this.setTitleText(View.VISIBLE, this.estates[this.estatesToShow.get(this.overallIndex)].name, R.color.text_color_light_mode);

            this.setGridButtonImage(main);
        }
    }

    void setupImageButton(int index) {
        ImageButton background = findViewById(R.id.background);
        if (this.theme == Theme.light) background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.light_theme));
        else if (this.theme == Theme.dark) background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dark_theme));

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);
        int viewIndex = this.currentPage * gridType + index - 1;

        if (this.focusedView == null) {
            this.focusedView = findViewById(R.id.imageButton1);
        }

        if (this.estatesToShow.size() == 0) {
            switch (this.language) {
                case united_kingdom:
                    if (this.theme == Theme.dark) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_en, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_en, R.color.text_color_light_mode);
                    }
                    break;
                case germany:
                    if (this.theme == Theme.dark) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_de, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_de, R.color.text_color_light_mode);
                    }
                    break;
                case croatia:
                    if (this.theme == Theme.dark) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_hr, R.color.text_color_dark_mode);
                    } else if (this.theme == Theme.light) {
                        this.setCenterText(View.VISIBLE, R.string.no_estates_hr, R.color.text_color_light_mode);
                    }
                    break;
            }
            this.setGridButton(getViewsByIndex(index), R.drawable.image_button, viewIndex);
        } else {
            this.setCenterText(View.INVISIBLE, R.string.empty_string, R.color.transparent);

            View[] views = getViewsByIndex(index); // ImageButton, Button, TextView
            if (this.focusedView.getId() != views[0].getId()) this.setGridButton(views, R.drawable.image_button, viewIndex);
            else if (this.theme == Theme.dark) this.setGridButton(views, R.drawable.highlighted_image_button_dark, viewIndex);
            else if (this.theme == Theme.light) this.setGridButton(views, R.drawable.highlighted_image_button_light, viewIndex);

            this.setGridButtonImage(((ImageButton)views[0]), viewIndex);
        }
    }
    void setupLanguageButton() {
        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        if (languageButton == null) return;
        switch (language) {
            case united_kingdom:
                languageButton.setText(R.string.language_en);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.united_kingdom));
                break;
            case germany:
                languageButton.setText(R.string.language_de);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.germany));
                break;
            case croatia:
                languageButton.setText(R.string.language_hr);
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.croatia));
                break;
        }

        if (focusedView.getId() == R.id.languageButton) languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else languageButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupThemeButton() {
        Button themeButton = findViewById(R.id.themeButton);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        if (themeButton == null) return;
        switch (theme) {
            case light:
                if (this.language == Language.united_kingdom) themeButton.setText(R.string.light_theme_en);
                else if (this.language == Language.germany) themeButton.setText(R.string.light_theme_de);
                else if (this.language == Language.croatia) themeButton.setText(R.string.light_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sun));
                break;
            case dark:
                if (this.language == Language.united_kingdom) themeButton.setText(R.string.dark_theme_en);
                else if (this.language == Language.germany) themeButton.setText(R.string.dark_theme_de);
                else if (this.language == Language.croatia) themeButton.setText(R.string.dark_theme_hr);

                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                break;
        }

        if (focusedView.getId() == R.id.themeButton) themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupGridButton() {
        Button gridButton = findViewById(R.id.gridButton);
        ImageView gridIcon = findViewById(R.id.gridIcon);

        if (gridButton == null) return;
        if (this.language == Language.united_kingdom) gridButton.setText(R.string.grid_en);
        else if (this.language == Language.germany) gridButton.setText(R.string.grid_de);
        else if (this.language == Language.croatia) gridButton.setText(R.string.grid_hr);

        gridIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.grid));

        if (focusedView.getId() == R.id.gridButton) gridButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else gridButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupTextClock() {
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

        if (focusedView.getId() == R.id.textClock) clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else clockButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupSearchBarButton() {
        SearchView searchbarButton = findViewById(R.id.searchView);

        if (searchbarButton == null) return;
        searchbarButton.setQuery(searchbarText, false);

        if (focusedView.getId() == R.id.searchView) searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupPaginationButton() {
        Button paginationButton = findViewById(R.id.pageIndex);
        EditText paginationCurrentPage = findViewById(R.id.pageIndexIndex);
        TextView paginationTotalPages = findViewById(R.id.pageIndexTotal);

        if (paginationButton == null || paginationCurrentPage == null || paginationTotalPages == null) return;
        if (this.estatesToShow.size() != 0) paginationCurrentPage.setText(Integer.toString(this.currentPage + 1));
        else paginationCurrentPage.setText("0");
        paginationTotalPages.setText(Integer.toString(this.totalPages));

        if (focusedView.getId() == R.id.pageIndex) paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void updateView(int row) {
        if (row == 0) setupLanguageButton();
        else if (row == 1) setupThemeButton();
        else if (row == 2) setupGridButton();
        else if (row == 3) setupTextClock();
        else if (row == 4) setupSearchBarButton();
        else if (row == 5) setupPaginationButton();
        else if (row == 6 && this.grid == BasicGridNavigation.one) setupMain();
        else if (row == 6 && (this.grid == BasicGridNavigation.three || this.grid == BasicGridNavigation.six)) setupImageButton(1);
        else if (row == 7) setupImageButton(2);
        else if (row == 8) setupImageButton(3);
        else if (row == 9) setupImageButton(4);
        else if (row == 10) setupImageButton(5);
        else if (row == 11) setupImageButton(6);
    }

    void setupEstatesToShow() {
        this.estatesToShow.clear();
        for (int i = 0; i < estates.length; ++i) {
            if ((estates[i].name.toLowerCase()).contains(this.searchbarText.toLowerCase())) this.estatesToShow.add(i);
        }
    }

    void setCenterText(int visibility, int textId, int colorId) {
        TextView centerText = findViewById(R.id.centerText);

        centerText.setVisibility(visibility);
        centerText.setText(getResources().getString(textId));
        centerText.setTextColor(getResources().getColor(colorId));
    }

    void setTitleText(int visibility, int textId, int colorId) {
        TextView titleText = findViewById(R.id.title1);

        titleText.setVisibility(visibility);
        titleText.setText(getResources().getString(textId));
        titleText.setTextColor(getResources().getColor(colorId));
    }

    void setTitleText(int visibility, String text, int colorId) {
        TextView titleText = findViewById(R.id.title1);

        titleText.setVisibility(visibility);
        titleText.setText(text);
        titleText.setTextColor(getResources().getColor(colorId));
    }

    void setGridButton(View[] views, int drawableId, int viewIndex) {
        ImageButton imageButton = ((ImageButton)views[0]);
        Button imageBackground = ((Button) views[1]);
        TextView title = ((TextView) views[2]);

        int visibility = viewIndex >= this.estatesToShow.size() ? View.INVISIBLE: View.VISIBLE;
        String titleText = viewIndex < this.estatesToShow.size() ? this.estates[this.estatesToShow.get(viewIndex)].name : "";

        imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), drawableId));
        title.setText(titleText);

        imageButton.setVisibility(visibility);
        title.setVisibility(visibility);
    }
    View[] getViewsByIndex(int index) {
        View[] views = new View[3];
        switch (index) {
            case 1:
                views[0] = findViewById(R.id.imageButton1);
                views[1] = findViewById(R.id.imageButtonBackground1);
                views[2] = findViewById(R.id.title1);
                break;
            case 2:
                views[0] = findViewById(R.id.imageButton2);
                views[1] = findViewById(R.id.imageButtonBackground2);
                views[2] = findViewById(R.id.title2);
                break;
            case 3:
                views[0] = findViewById(R.id.imageButton3);
                views[1] = findViewById(R.id.imageButtonBackground3);
                views[2] = findViewById(R.id.title3);
                break;
            case 4:
                views[0] = findViewById(R.id.imageButton4);
                views[1] = findViewById(R.id.imageButtonBackground4);
                views[2] = findViewById(R.id.title4);
                break;
            case 5:
                views[0] = findViewById(R.id.imageButton5);
                views[1] = findViewById(R.id.imageButtonBackground5);
                views[2] = findViewById(R.id.title5);
                break;
            case 6:
                views[0] = findViewById(R.id.imageButton6);
                views[1] = findViewById(R.id.imageButtonBackground6);
                views[2] = findViewById(R.id.title6);
                break;
        }
        return views;
    }

    void setGridButtonImage(ImageButton imageButton, int viewIndex) {
        if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && !this.estates[this.estatesToShow.get(viewIndex)].image.isEmpty()) {
            try {
                Picasso.get()
                        .load(this.estates[this.estatesToShow.get(viewIndex)].image)
                        .fit()
                        .into(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void setGridButtonImage(ImageView imageButton) {
        if (this.estates[this.estatesToShow.get(this.overallIndex)].image != null && !this.estates[this.estatesToShow.get(this.overallIndex)].image.isEmpty()) {
            try {
                Picasso.get()
                        .load(this.estates[this.estatesToShow.get(this.overallIndex)].image)
                        .fit()
                        .into(imageButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void hideProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    void setProgressBar() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (progressBar == null) return;

        if (this.theme == Theme.dark) {
            int color = ContextCompat.getColor(this, R.color.loader_color_dark_mode);
            progressBar.setProgressTintList(ColorStateList.valueOf(color));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        } else if (this.theme == Theme.light) {
            int color = ContextCompat.getColor(this, R.color.loader_color_light_mode);
            progressBar.setProgressTintList(ColorStateList.valueOf(color));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
        }
    }

    void navigateToCategoryListActivity() {
        String estateId = this.estates[this.estatesToShow.get(this.overallIndex)].id;
        if (estateId == null || estateId.isEmpty()) return;

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("estateId", estateId);
        editor.apply();

        Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
        startActivity(i);
    }
}
