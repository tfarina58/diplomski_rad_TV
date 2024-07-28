package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class CategoryListActivity extends Activity {
    String estateId;
    FirebaseFirestore firestore;
    SharedPreferencesService sharedPreferencesService;
    Category[] categories;
    ArrayList<Integer> categoriesToShow;
    Language language;
    Theme theme;
    GridNavigation grid;
    Clock format;
    View focusedView;
    String searchbarText = "";
    int currentPage = 0;
    int totalPages;
    boolean loadingInProgress = true;
    boolean backgroundAlreadySet = false;
    String focusedLayout = ""; // "rating", "password" or ""
    View layoutFocusedView;
    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.estateId = this.sharedPreferencesService.getEstateId();
        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();
        this.grid = this.sharedPreferencesService.getGrid();
        this.format = this.sharedPreferencesService.getClockFormat();
        this.categoriesToShow = new ArrayList<>();

        this.firestore = FirebaseFirestore.getInstance();

        getCurrentUserName();

        Query query = firestore.collection("categories").whereEqualTo("estateId", estateId);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 0;
                        categories = new Category[queryDocumentSnapshots.size()];
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String categoryId = document.getId();
                            String estateId = document.getString("estateId");
                            HashMap<String, String> title = (HashMap<String, String>) document.get("title");
                            String image = document.getString("image");
                            categories[i] = new Category(categoryId, estateId, title, image);
                            i++;
                        }
                        loadingInProgress = false;
                        setupCategoriesToShow();

                        currentPage = 0;
                        totalPages = categoriesToShow.size() == 0 ? 0 : (categoriesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;
                        backgroundAlreadySet = false;

                        setNewContentView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        categories = new Category[0];
                        loadingInProgress = false;
                        setupCategoriesToShow();
                        setNewContentView();
                    }
                });


        this.categories = new Category[0];
        setupCategoriesToShow();
        setNewContentView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);

        // Up, down, left, right navigation button
        if (this.focusedLayout.equals("rating")) {
            int oldFocusedViewId = this.layoutFocusedView.getId();

            if (keyCode >= 19 && keyCode <= 22) {
                int newFocusedViewId = ChooseRatingLayoutNavigation.navigateOverLayout(oldFocusedViewId, keyCode - 19);

                if (newFocusedViewId == 0) return true;

                this.layoutFocusedView = findViewById(newFocusedViewId);
                this.layoutFocusedView.requestFocus();

                // Remove focus from old View
                int row = ChooseRatingLayoutNavigation.getRowWithId(oldFocusedViewId);
                updateLayoutView(this.focusedLayout, row);

                // Add focus to new View
                row = ChooseRatingLayoutNavigation.getRowWithId(newFocusedViewId);
                updateLayoutView(this.focusedLayout, row);

            } else if (keyCode == 4) {
                FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                ConstraintLayout background = findViewById(R.id.ratingMain);
                TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                Button showRatingsButton = findViewById(R.id.showRatingsButton);
                Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                Button submitRatingButton = findViewById(R.id.submitRatingButton);

                this.focusedLayout = "";
                this.layoutFocusedView = null;
                this.focusedView.requestFocus();

                this.setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, false, this.layoutFocusedView, this.language, this.theme);
            }
            // Enter button
            else if (keyCode == 23) this.layoutFocusedView.callOnClick();

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        else if (this.focusedLayout.equals("password")) {
            int oldFocusedViewId = this.layoutFocusedView.getId();

            if (keyCode >= 19 && keyCode <= 22) {
                int newFocusedViewId = EnterPasswordLayoutNavigation.navigateOverLayout(oldFocusedViewId, keyCode - 19);

                if (newFocusedViewId == 0) return true;

                this.layoutFocusedView = findViewById(newFocusedViewId);
                this.layoutFocusedView.requestFocus();

                // Remove focus from old View
                int row = EnterPasswordLayoutNavigation.getRowWithId(oldFocusedViewId);
                updateLayoutView(this.focusedLayout, row);

                // Add focus to new View
                row = EnterPasswordLayoutNavigation.getRowWithId(newFocusedViewId);
                updateLayoutView(this.focusedLayout, row);

            } else if (keyCode == 4) {
                FrameLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
                ConstraintLayout background = findViewById(R.id.passwordMain);
                TextView enterPasswordTitle = findViewById(R.id.enterPasswordTitle);
                EditText passwordField = findViewById(R.id.password);
                TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
                Button cancelButton = findViewById(R.id.cancelButtonPassword);
                Button submitButton = findViewById(R.id.submitButton);

                this.focusedLayout = "";
                this.layoutFocusedView = null;
                this.focusedView.requestFocus();

                this.setupPasswordLayout(getApplicationContext(), enterPasswordLayout, background, enterPasswordTitle, passwordField, passwordFieldTitle, cancelButton, submitButton, false, this.layoutFocusedView, this.language, this.theme);
            }
            // Enter button
            else if (keyCode == 23) this.layoutFocusedView.callOnClick();

            return true;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        int oldFocusedViewId = this.focusedView.getId();

        if (keyCode >= 19 && keyCode <= 22) {
            if (specialCaseNavigation(oldFocusedViewId, keyCode - 19)) return true;

            int newFocusedViewId = GridNavigation.navigateOverActivity(false, this.grid, oldFocusedViewId, keyCode - 19);

            // If navigating up or down
            if (keyCode == 19 || keyCode == 20)
                while (!checkViewExistence(newFocusedViewId) && newFocusedViewId != 0)
                    newFocusedViewId = GridNavigation.navigateOverActivity(false, this.grid, newFocusedViewId, keyCode - 19);

            if (newFocusedViewId == 0 || !checkViewExistence(newFocusedViewId)) return false;

            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = GridNavigation.getNormalRowWithId(oldFocusedViewId);
            updateView(row);

            // Add focus to new View
            row = GridNavigation.getNormalRowWithId(newFocusedViewId);
            updateView(row);

            if (newFocusedViewId == R.id.searchView || newFocusedViewId == R.id.pagination) {
                this.focusedView.callOnClick();
            }
        } else if (keyCode == 4) {
            FrameLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
            ConstraintLayout background = findViewById(R.id.passwordMain);
            TextView enterPasswordTitle = findViewById(R.id.enterPasswordTitle);
            EditText passwordField = findViewById(R.id.password);
            TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
            Button cancelButton = findViewById(R.id.cancelButtonPassword);
            Button submitButton = findViewById(R.id.submitButton);

            this.focusedLayout = "password";
            this.layoutFocusedView = findViewById(R.id.password);
            this.layoutFocusedView.requestFocus();

            this.setupPasswordLayout(getApplicationContext(), enterPasswordLayout, background, enterPasswordTitle, passwordField, passwordFieldTitle, cancelButton, submitButton, true, this.layoutFocusedView, this.language, this.theme);
        }
        // Enter button
        else if (keyCode == 23) this.focusedView.callOnClick();

        return true;
    }

    void setNewContentView() {
        setContentView();

        int gridType = GridNavigation.getGridTypeAsInt(this.grid);

        if (this.focusedView == null) {
            if (this.grid == GridNavigation.three || this.grid == GridNavigation.six) this.focusedView = findViewById(R.id.gridButton1);
            else this.focusedView = findViewById(R.id.backgroundGrid1);
            this.focusedView.requestFocus();
        }

        if (this.grid == GridNavigation.one) {
            ImageButton main = findViewById(R.id.backgroundGrid1);
            TextView title = findViewById(R.id.gridTitle1);
            ProgressBar progressBar = findViewById(R.id.progressBar);
            TextView centerText = findViewById(R.id.centerText);

            this.setupMain(getApplicationContext(), main, title, progressBar, centerText);
        } else if (this.grid == GridNavigation.three) {
            Category[] tmpCategories = new Category[3];
            for (int i = 0; i < 3; ++i)
                if (this.currentPage * gridType + i < this.categoriesToShow.size())
                    tmpCategories[i] = this.categories[this.categoriesToShow.get(this.currentPage * gridType + i)];

            this.setupGrid(getApplicationContext(), this.focusedView, this.language, this.theme, this.loadingInProgress, tmpCategories);
        } else {
            Category[] tmpCategories = new Category[6];
            for (int i = 0; i < 6; ++i)
                if (this.currentPage * gridType + i < this.categoriesToShow.size())
                    tmpCategories[i] = this.categories[this.categoriesToShow.get(this.currentPage * gridType + i)];

            this.setupGrid(getApplicationContext(), this.focusedView, this.language, this.theme, this.loadingInProgress, tmpCategories);
        }

        {
            Button button;
            ImageView icon;

            // Rating button
            button = findViewById(R.id.ratingButton);
            icon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), button, icon, true, this.focusedView, this.language);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    focusedLayout = "rating";

                    FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                    ConstraintLayout background = findViewById(R.id.ratingMain);
                    TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                    Button showRatingsButton = findViewById(R.id.showRatingsButton);
                    Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                    Button submitRatingButton = findViewById(R.id.submitRatingButton);

                    layoutFocusedView = showRatingsButton;

                    setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, true, layoutFocusedView, language, theme);
                }
            });

            // Language button
            button = findViewById(R.id.languageButton);
            icon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), button, icon, this.focusedView, this.language);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    sharedPreferencesService.setLanguage(language);

                    setupCategoriesToShow();
                    totalPages = categoriesToShow.size() == 0 ? 0 : (categoriesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;

                    if (grid == GridNavigation.one) {
                        TextView titleText = findViewById(R.id.gridTitle1);
                        if (currentPage < categoriesToShow.size()) setMainTitle(getApplicationContext(), titleText, categories[categoriesToShow.get(currentPage)].title, language, theme, loadingInProgress, categoriesToShow.size(), currentPage);
                        else setMainTitle(getApplicationContext(), titleText, null, language, theme, loadingInProgress, categoriesToShow.size(), currentPage);
                    } else if (grid == GridNavigation.three) {
                        Category[] tmpCategories = new Category[3];
                        for (int i = 0; i < 3; ++i)
                            if (currentPage * gridType + i < categoriesToShow.size())
                                tmpCategories[i] = categories[categoriesToShow.get(currentPage * gridType + i)];

                        setupGrid(getApplicationContext(), focusedView, language, theme, loadingInProgress, tmpCategories);
                    } else if (grid == GridNavigation.six) {
                        Category[] tmpCategories = new Category[6];
                        for (int i = 0; i < 6; ++i)
                            if (currentPage * gridType + i < categoriesToShow.size())
                                tmpCategories[i] = categories[categoriesToShow.get(currentPage * gridType + i)];

                        setupGrid(getApplicationContext(), focusedView, language, theme, loadingInProgress, tmpCategories);
                    }

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, categoriesToShow.size(), "categories");

                    SearchView searchbarButton = findViewById(R.id.searchView);
                    setupSearchBarButton(getApplicationContext(), searchbarButton, searchbarText, language);

                    Button pagination = findViewById(R.id.pagination);
                    EditText pageNumber = findViewById(R.id.pageNumber);
                    TextView totalPagesNumber = findViewById(R.id.totalPagesNumber);
                    setupPaginationButton(pagination, pageNumber, totalPagesNumber);

                    updateView(0);
                    updateView(1);
                    updateView(2);
                    updateView(3);
                    updateView(5);
                }
            });

            // Theme button
            button = findViewById(R.id.themeButton);
            icon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), button, icon, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme = theme.next();
                    sharedPreferencesService.setTheme(theme);

                    updateView(2);

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, categoriesToShow.size(), "categories");

                    if (grid == GridNavigation.one) updateView(7);
                    else if (grid == GridNavigation.three) {
                        {
                            ImageButton background = findViewById(R.id.backgroundGrid3);
                            setupGridBackground(getApplicationContext(), background, theme);
                        }

                        {
                            TextView gridTitle = findViewById(R.id.gridTitle3);
                            setupGridTitle(getApplicationContext(), gridTitle, userName, language, theme);
                        }

                        ImageButton imageButton;
                        Button imageBackground;
                        TextView imageTitle;
                        int viewIndex;

                        imageButton = findViewById(R.id.gridButton1);
                        imageBackground = findViewById(R.id.gridButtonBackground1);
                        imageTitle = findViewById(R.id.gridButtonTitle1);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid);

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton2);
                        imageBackground = findViewById(R.id.gridButtonBackground2);
                        imageTitle = findViewById(R.id.gridButtonTitle2);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 1;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton3);
                        imageBackground = findViewById(R.id.gridButtonBackground3);
                        imageTitle = findViewById(R.id.gridButtonTitle3);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 2;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);
                    } else {
                        {
                            ImageButton background = findViewById(R.id.backgroundGrid6);
                            setupGridBackground(getApplicationContext(), background, theme);
                        }

                        {
                            TextView gridTitle = findViewById(R.id.gridTitle6);
                            setupGridTitle(getApplicationContext(), gridTitle, userName, language, theme);
                        }

                        ImageButton imageButton;
                        Button imageBackground;
                        TextView imageTitle;
                        int viewIndex;

                        imageButton = findViewById(R.id.gridButton1);
                        imageBackground = findViewById(R.id.gridButtonBackground1);
                        imageTitle = findViewById(R.id.gridButtonTitle1);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid);

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton2);
                        imageBackground = findViewById(R.id.gridButtonBackground2);
                        imageTitle = findViewById(R.id.gridButtonTitle2);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 1;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton3);
                        imageBackground = findViewById(R.id.gridButtonBackground3);
                        imageTitle = findViewById(R.id.gridButtonTitle3);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 2;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton4);
                        imageBackground = findViewById(R.id.gridButtonBackground4);
                        imageTitle = findViewById(R.id.gridButtonTitle4);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 3;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton5);
                        imageBackground = findViewById(R.id.gridButtonBackground5);
                        imageTitle = findViewById(R.id.gridButtonTitle5);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 4;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

                        imageButton = findViewById(R.id.gridButton6);
                        imageBackground = findViewById(R.id.gridButtonBackground6);
                        imageTitle = findViewById(R.id.gridButtonTitle6);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 5;

                        if (viewIndex < categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);
                    }
                }
            });

            // Grid button
            button = findViewById(R.id.gridButton);
            icon = findViewById(R.id.gridIcon);

            GridHeaderButton.setupGridButton(getApplicationContext(), button, icon, this.focusedView, this.language);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grid = grid.next();
                    sharedPreferencesService.setGrid(grid);

                    currentPage = GridNavigation.getNewPageNumber(currentPage, grid);
                    totalPages = categoriesToShow.size() == 0 ? 0 :(categoriesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;
                    backgroundAlreadySet = false;

                    setNewContentView();
                }
            });
        }

        {
            // Clock button
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);

            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    format = format.next();
                    sharedPreferencesService.setClockFormat(format);

                    switch (format) {
                        case h24:
                            ((TextClock) focusedView).setFormat12Hour("HH:mm:ss");
                            break;
                        case h12:
                            ((TextClock) focusedView).setFormat12Hour("hh:mm:ss a");
                            break;
                    }
                }
            });
        }

        {
            // Searchbar button
            SearchView searchbarButton = findViewById(R.id.searchView);

            this.setupSearchBarButton(getApplicationContext(), searchbarButton, this.searchbarText, this.language);

            searchbarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(((SearchView) focusedView), InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });

            searchbarButton.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchbarText = query;
                    setupCategoriesToShow();

                    currentPage = 0;
                    totalPages = categoriesToShow.size() == 0 ? 0 : (categoriesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;


                    setNewContentView();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return true;
                }
            });
        }

        {
            // Pagination button
            Button paginationButton = findViewById(R.id.pagination);
            EditText paginationCurrentPage = findViewById(R.id.pageNumber);
            TextView paginationTotalPages = findViewById(R.id.totalPagesNumber);

            setupPaginationButton(paginationButton, paginationCurrentPage, paginationTotalPages);

            paginationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paginationCurrentPage.setEnabled(true);
                    paginationCurrentPage.setFocusable(true);
                    paginationCurrentPage.setFocusableInTouchMode(true);
                    paginationCurrentPage.requestFocus();
                    // paginationCurrentPage.callOnClick();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(((EditText) paginationCurrentPage), InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });

            paginationCurrentPage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        try {
                            String input = paginationCurrentPage.getText().toString();
                            int pageIndexNumber = Integer.parseInt(input) - 1;
                            if (pageIndexNumber < 0 || pageIndexNumber >= totalPages)
                                throw new Exception();
                            if (pageIndexNumber != currentPage) {
                                currentPage = pageIndexNumber;
                            }
                        } catch (Exception ex) {
                            switch (language) {
                                case german:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.invalid_page_number_de) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                                    break;
                                case croatian:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.invalid_page_number_hr) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.invalid_page_number_en) + " " + totalPages + ".", Toast.LENGTH_LONG).show();
                            }
                            InputMethodManager imm = (InputMethodManager) paginationCurrentPage.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(paginationCurrentPage.getWindowToken(), 0);
                            }

                            paginationCurrentPage.clearFocus();
                            paginationButton.requestFocus();

                            return false;
                        }
                        paginationCurrentPage.clearFocus();
                        paginationButton.requestFocus();

                        backgroundAlreadySet = false;
                        setNewContentView();
                        return true; // Return true to indicate that the event has been handled
                    }
                    InputMethodManager imm = (InputMethodManager) paginationCurrentPage.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(paginationCurrentPage.getWindowToken(), 0);
                    }

                    paginationCurrentPage.clearFocus();
                    paginationButton.requestFocus();
                    return false; // Return false to indicate that the event has not been handled
                }
            });
        }

        FrameLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
        ConstraintLayout background = findViewById(R.id.passwordMain);
        TextView enterPasswordTitle = findViewById(R.id.enterPasswordTitle);
        EditText passwordField = findViewById(R.id.password);
        TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
        Button cancelButton = findViewById(R.id.cancelButtonPassword);
        Button submitButton = findViewById(R.id.submitButton);

        this.setupPasswordLayout(getApplicationContext(), enterPasswordLayout, background, enterPasswordTitle, passwordField, passwordFieldTitle, cancelButton, submitButton, false, this.layoutFocusedView, this.language, this.theme); // TODO: CHECK
    }

    void setContentView() {
        switch (this.grid) {
            case one:
                setContentView(R.layout.activity_basic_grid_1);
                break;
            case three:
                setContentView(R.layout.activity_basic_grid_3);
                break;
            default:
                setContentView(R.layout.activity_basic_grid_6);
        }
    }

    void setupMain(Context ctx, ImageButton main, TextView titleText, ProgressBar progressBar, TextView centerText) {
        if (main == null || titleText == null) return;

        if (this.currentPage < this.categoriesToShow.size()) {
            this.setupMainBackground(ctx, main, this.focusedView, this.categories[this.categoriesToShow.get(this.currentPage)].image, this.theme);
            this.setMainTitle(ctx, titleText, this.categories[this.categoriesToShow.get(this.currentPage)].title, this.language, this.theme, this.loadingInProgress, this.categoriesToShow.size(), this.currentPage);
        } else {
            this.setupMainBackground(ctx, main, this.focusedView, "", this.theme);
            this.setMainTitle(ctx, titleText, null, this.language, this.theme, this.loadingInProgress, this.categoriesToShow.size(), this.currentPage);
        }

        ProgressBarLoader.manageProgressBar(ctx, progressBar, this.theme, this.loadingInProgress);
        CenterText.setupCenterText(ctx, centerText, this.language, this.theme, this.loadingInProgress, this.categoriesToShow.size(), "categories");

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToElementListActivity();
            }
        });
    }


    void setMainTitle(Context ctx, TextView titleText, HashMap<String, String> title, Language language, Theme theme, boolean loadingInProgress, int categoriesToShowSize, int overallIndex) {
        if (titleText == null) return;

        if (loadingInProgress || /*!loadingInProgress &&*/ categoriesToShowSize == 0) {
            switch (language) {
                case german:
                    titleText.setText(ContextCompat.getString(ctx, R.string.category_name_de));
                    break;
                case croatian:
                    titleText.setText(ContextCompat.getString(ctx, R.string.category_name_hr));
                    break;
                default:
                    titleText.setText(ContextCompat.getString(ctx, R.string.category_name_en));
            }
        } else if (overallIndex < categoriesToShowSize) {
            switch (language) {
                case german:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.category_name_de));
                        break;
                    }

                    String titleDe = title.get("de");

                    if (titleDe != null && !titleDe.isEmpty()) titleText.setText(titleDe);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.category_name_de));
                    break;
                case croatian:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.category_name_hr));
                        break;
                    }

                    String titleHr = title.get("hr");

                    if (titleHr != null && !titleHr.isEmpty()) titleText.setText(titleHr);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.category_name_hr));
                    break;
                default:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.category_name_en));
                        break;
                    }

                    String titleEn = title.get("en");

                    if (titleEn != null && !titleEn.isEmpty()) titleText.setText(titleEn);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.category_name_en));
                    break;
            }
        }

        if (theme == Theme.light)
            titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    void setupMainBackground(Context ctx, ImageButton main, View focusedView, String imageUrl, Theme theme) {
        if (main == null) return;

        if (focusedView.getId() == main.getId()) {
            if (theme == Theme.light) main.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
            else main.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
        } else {
            if (theme == Theme.light) main.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
            else main.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
        }

        // TODO: fix focus border
        if (!this.backgroundAlreadySet) {
            if (imageUrl == null || imageUrl.isEmpty()) main.setImageDrawable(null);
            else {
                try {
                    Picasso.get()
                            .load(imageUrl)
                            .fit()
                            .into(main);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.backgroundAlreadySet = true;
        }
    }

    void setupGridBackground(Context ctx, ImageButton background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
    }

    void setupSearchBarButton(Context ctx, SearchView searchbarButton, String searchbarText, Language language) {
        if (searchbarButton == null) return;

        if (!searchbarText.isEmpty()) searchbarButton.setQuery(searchbarText, false);
        else {
            switch (language) {
                case german:
                    searchbarButton.setQueryHint(ContextCompat.getString(ctx, R.string.search_de));
                    break;
                case croatian:
                    searchbarButton.setQueryHint(ContextCompat.getString(ctx, R.string.search_hr));
                    break;
                default:
                    searchbarButton.setQueryHint(ContextCompat.getString(ctx, R.string.search_en));
            }
        }

        if (focusedView.getId() == R.id.searchView) searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else searchbarButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupPaginationButton(Button paginationButton, EditText paginationCurrentPage, TextView paginationTotalPages) {
        if (paginationButton == null || paginationCurrentPage == null || paginationTotalPages == null)
            return;

        if (this.categoriesToShow.size() != 0)
            paginationCurrentPage.setText(Integer.toString(this.currentPage + 1));
        else paginationCurrentPage.setText("0");
        paginationTotalPages.setText(Integer.toString(this.totalPages));

        if (focusedView.getId() == R.id.pagination)
            paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else
            paginationButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void updateView(int row) {
        if (row == 0) {
            Button ratingButton = findViewById(R.id.ratingButton);
            ImageView ratingIcon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), ratingButton, ratingIcon, true, this.focusedView, this.language);
        } else if (row == 1) {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language);
        } else if (row == 2) {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
        } else if (row == 3) {
            Button gridButton = findViewById(R.id.gridButton);
            ImageView gridIcon = findViewById(R.id.gridIcon);

            GridHeaderButton.setupGridButton(getApplicationContext(), gridButton, gridIcon, this.focusedView, this.language);
        } else if (row == 4) {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);
        } else if (row == 5) {
            SearchView searchbarButton = findViewById(R.id.searchView);

            this.setupSearchBarButton(getApplicationContext(), searchbarButton, this.searchbarText, this.language);
        } else if (row == 6) {
            Button paginationButton = findViewById(R.id.pagination);
            EditText paginationCurrentPage = findViewById(R.id.pageNumber);
            TextView paginationTotalPages = findViewById(R.id.totalPagesNumber);

            this.setupPaginationButton(paginationButton, paginationCurrentPage, paginationTotalPages);
        } else if (row == 7 && this.grid == GridNavigation.one) {
            ImageButton main = findViewById(R.id.backgroundGrid1);
            TextView title = findViewById(R.id.gridTitle1);

            if (this.currentPage < this.categoriesToShow.size()) {
                this.setupMainBackground(getApplicationContext(), main, this.focusedView, this.categories[this.categoriesToShow.get(this.currentPage)].image, this.theme);
                this.setMainTitle(getApplicationContext(), title, this.categories[this.categoriesToShow.get(this.currentPage)].title, this.language, this.theme, this.loadingInProgress, this.categoriesToShow.size(), this.currentPage);
            } else {
                this.setupMainBackground(getApplicationContext(), main, this.focusedView, "", this.theme);
                this.setMainTitle(getApplicationContext(), title, null, this.language, this.theme, this.loadingInProgress, this.categoriesToShow.size(), this.currentPage);
            }
        } else if (row == 7 && (this.grid == GridNavigation.three || this.grid == GridNavigation.six)) {
            ImageButton imageButton = findViewById(R.id.gridButton1);
            Button imageBackground = findViewById(R.id.gridButtonBackground1);
            TextView imageTitle = findViewById(R.id.gridButtonTitle1);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage;

            if (viewIndex < this.categoriesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        } else if (row == 8) {
            ImageButton imageButton = findViewById(R.id.gridButton2);
            Button imageBackground = findViewById(R.id.gridButtonBackground2);
            TextView imageTitle = findViewById(R.id.gridButtonTitle2);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 1;

            if (viewIndex < this.categoriesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        } else if (row == 9) {
            ImageButton imageButton = findViewById(R.id.gridButton3);
            Button imageBackground = findViewById(R.id.gridButtonBackground3);
            TextView imageTitle = findViewById(R.id.gridButtonTitle3);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 2;

            if (viewIndex < this.categoriesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        } else if (row == 10) {
            ImageButton imageButton = findViewById(R.id.gridButton4);
            Button imageBackground = findViewById(R.id.gridButtonBackground4);
            TextView imageTitle = findViewById(R.id.gridButtonTitle4);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 3;

            if (viewIndex < this.categoriesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        } else if (row == 11) {
            ImageButton imageButton = findViewById(R.id.gridButton5);
            Button imageBackground = findViewById(R.id.gridButtonBackground5);
            TextView imageTitle = findViewById(R.id.gridButtonTitle5);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 4;

            if (viewIndex < this.categoriesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        } else if (row == 12) {
            ImageButton imageButton = findViewById(R.id.gridButton6);
            Button imageBackground = findViewById(R.id.gridButtonBackground6);
            TextView imageTitle = findViewById(R.id.gridButtonTitle6);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 5;

            if (viewIndex < this.categoriesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.categories[this.categoriesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Category) null);
        }
    }

    void setupCategoriesToShow() {
        this.categoriesToShow.clear();
        for (int i = 0; i < categories.length; ++i)
            switch (language) {
                case german:
                    String titleDe = categories[i].title.get("de");
                    if (titleDe == null) break;

                    if (titleDe.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.categoriesToShow.add(i);
                    break;
                case croatian:
                    String titleHr = categories[i].title.get("hr");
                    if (titleHr == null) break;

                    if (titleHr.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.categoriesToShow.add(i);
                    break;
                default:
                    String titleEn = categories[i].title.get("en");
                    if (titleEn == null) break;

                    if (titleEn.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.categoriesToShow.add(i);
            }
    }

    void navigateToElementListActivity() {
        int viewIndex = GridNavigation.getViewIndexAsInt(this.focusedView.getId());
        int overallIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + viewIndex;

        // Happens only when array length == 0
        if (overallIndex >= this.categoriesToShow.size()) return;

        String categoryId = this.categories[this.categoriesToShow.get(overallIndex)].id;
        if (categoryId == null || categoryId.isEmpty()) return;

        this.sharedPreferencesService.setCategoryId(categoryId);
        startActivity(new Intent(getApplicationContext(), ElementListActivity.class));
    }

    boolean checkViewExistence(int focusedViewId) {
        switch (this.grid) {
            case three:
                if (focusedViewId == R.id.ratingButton) return true;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.gridButton1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton2) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 1;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton3) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 2;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                break;
            case six:
                if (focusedViewId == R.id.ratingButton) return true;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.gridButton1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton2) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 1;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton3) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 2;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton4) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 3;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton5) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 4;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                if (focusedViewId == R.id.gridButton6) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 5;
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
                break;
            default:
                if (focusedViewId == R.id.ratingButton) return true;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.backgroundGrid1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.categoriesToShow.size() > viewIndex && !this.categories[this.categoriesToShow.get(viewIndex)].title.isEmpty();
                }
        }
        return false;
    }

    boolean specialCaseNavigation(int oldFocusedViewId, int direction) {
        // Direction left
        if (direction == 2) {
            if (oldFocusedViewId == R.id.backgroundGrid1 || oldFocusedViewId == R.id.gridButton1 || oldFocusedViewId == R.id.gridButton4) {
                if (this.currentPage > 0) {
                    this.currentPage--;
                    if (this.grid == GridNavigation.one) {
                        backgroundAlreadySet = false;
                        this.updateView(7);
                    } else if (this.grid == GridNavigation.three) {
                        this.focusedView = findViewById(R.id.gridButton3);
                        this.updateView(7);
                        this.updateView(8);
                        this.updateView(9);
                    } else if (this.grid == GridNavigation.six) {
                        if (GridNavigation.isFirstRow(this.focusedView.getId())) {
                            this.focusedView = findViewById(R.id.gridButton3);
                            this.updateView(7);
                            this.updateView(8);
                            this.updateView(9);
                        } else if (GridNavigation.isSecondRow(this.focusedView.getId())) {
                            this.focusedView = findViewById(R.id.gridButton6);
                            this.updateView(10);
                            this.updateView(11);
                            this.updateView(12);
                        }
                    }
                    this.updateView(6);
                }
                return true;
            }
        }
        // Direction right
        else if (direction == 3) {
            if (oldFocusedViewId == R.id.backgroundGrid1 || oldFocusedViewId == R.id.gridButton3 || oldFocusedViewId == R.id.gridButton6) {
                if (this.currentPage < this.totalPages - 1) {
                    this.currentPage++;
                    if (this.grid == GridNavigation.one) {
                        backgroundAlreadySet = false;
                        this.updateView(7);
                    } else if (this.grid == GridNavigation.three) {
                        this.focusedView = findViewById(R.id.gridButton1);
                        this.updateView(7);
                        this.updateView(8);
                        this.updateView(9);
                    } else if (this.grid == GridNavigation.six) {
                        if (GridNavigation.isFirstRow(this.focusedView.getId()))
                            this.focusedView = findViewById(R.id.gridButton1);
                        else if (GridNavigation.isSecondRow(this.focusedView.getId())) {
                            this.focusedView = findViewById(R.id.gridButton4);

                            this.updateView(7);
                            this.updateView(8);
                            this.updateView(9);

                            this.updateView(10);
                            this.updateView(11);
                            this.updateView(12);
                        }
                    }
                    this.updateView(6);
                }
                return true;
            }
        }
        return false;
    }

    void setupGrid(Context ctx, View focusedView, Language language, Theme theme, boolean loadingInProgress, Category[] categories) {

        TextView gridTitle = null;
        if (this.grid == GridNavigation.three) gridTitle = findViewById(R.id.gridTitle3);
        else if (this.grid == GridNavigation.six) gridTitle = findViewById(R.id.gridTitle6);

        this.setupGridTitle(ctx, gridTitle, this.userName, this.language, this.theme);

        ImageButton imageButton;
        Button imageBackground;
        TextView imageTitle;

        // Image button 1
        imageButton = findViewById(R.id.gridButton1);
        imageBackground = findViewById(R.id.gridButtonBackground1);
        imageTitle = findViewById(R.id.gridButtonTitle1);

        if (0 < categories.length && categories[0] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[0]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Image button 2
        imageButton = findViewById(R.id.gridButton2);
        imageBackground = findViewById(R.id.gridButtonBackground2);
        imageTitle = findViewById(R.id.gridButtonTitle2);

        if (1 < categories.length && categories[1] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[1]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Image button 3
        imageButton = findViewById(R.id.gridButton3);
        imageBackground = findViewById(R.id.gridButtonBackground3);
        imageTitle = findViewById(R.id.gridButtonTitle3);

        if (2 < categories.length && categories[2] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[2]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Image button 4
        imageButton = findViewById(R.id.gridButton4);
        imageBackground = findViewById(R.id.gridButtonBackground4);
        imageTitle = findViewById(R.id.gridButtonTitle4);

        if (3 < categories.length && categories[3] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[3]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Image button 5
        imageButton = findViewById(R.id.gridButton5);
        imageBackground = findViewById(R.id.gridButtonBackground5);
        imageTitle = findViewById(R.id.gridButtonTitle5);

        if (4 < categories.length && categories[4] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[4]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Image button 6
        imageButton = findViewById(R.id.gridButton6);
        imageBackground = findViewById(R.id.gridButtonBackground6);
        imageTitle = findViewById(R.id.gridButtonTitle6);

        if (5 < categories.length && categories[5] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, categories[5]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToElementListActivity();
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Category) null);

        // Center text
        TextView centerText = findViewById(R.id.centerText);

        CenterText.setupCenterText(ctx, centerText, language, theme, loadingInProgress, categoriesToShow.size(), "categories");

        // Background
        ImageButton background = null;
        if (grid == GridNavigation.three) background = findViewById(R.id.backgroundGrid3);
        else if (grid == GridNavigation.six) background = findViewById(R.id.backgroundGrid6);

        this.setupGridBackground(ctx, background, theme);

        // Progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);

        ProgressBarLoader.manageProgressBar(ctx, progressBar, theme, loadingInProgress);
    }

    void checkPasswordMatching(String password) {
        if (password == null || password.isEmpty()) return;

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

        DocumentReference query = firestore.collection("users").document(sharedPreferencesService.getUserId());

        String finalPassword = password;

        query.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                        String firestorePassword = queryDocumentSnapshots.getString("password");
                        if (!finalPassword.equals(firestorePassword)) {
                            switch (language) {
                                case german:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.incorrect_password_de), Toast.LENGTH_LONG).show();
                                    break;
                                case croatian:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.incorrect_password_hr), Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getApplicationContext(), ContextCompat.getString(getApplicationContext(), R.string.incorrect_password_en), Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        FrameLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
                        enterPasswordLayout.setVisibility(View.INVISIBLE);
                        focusedLayout = "";

                        sharedPreferencesService.setEstateId("");
                        startActivity(new Intent(getApplicationContext(), EstateListActivity.class));
                    }
                });
    }

    void setupPasswordLayout(Context ctx, FrameLayout enterPasswordLayout, ConstraintLayout background, TextView enterPasswordTitle, EditText passwordField, TextView passwordFieldTitle, Button cancelButton, Button submitButton, boolean visible, View layoutFocusedView, Language language, Theme theme) {
        if (enterPasswordLayout == null || background == null || enterPasswordTitle == null || passwordField == null || passwordFieldTitle == null || cancelButton == null || submitButton == null) return;

        if (!visible) {
            enterPasswordLayout.setVisibility(View.INVISIBLE);
            return;
        }
        enterPasswordLayout.setVisibility(View.VISIBLE);

        EnterPasswordLayout.setupLayoutTitle(ctx, enterPasswordTitle, language, theme);
        EnterPasswordLayout.setupLayoutBackground(ctx, background, theme);
        EnterPasswordLayout.setupPasswordField(ctx, passwordField, passwordFieldTitle, layoutFocusedView, language, theme);
        EnterPasswordLayout.setupCancelButton(ctx, cancelButton, layoutFocusedView, language);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout enterPasswordLayout = findViewById(R.id.enterPasswordLayout);
                ConstraintLayout background = findViewById(R.id.passwordMain);
                TextView enterPasswordTitle = findViewById(R.id.enterPasswordTitle);
                EditText passwordField = findViewById(R.id.password);
                TextView passwordFieldTitle = findViewById(R.id.passwordTitle);
                Button cancelButton = findViewById(R.id.cancelButtonPassword);
                Button submitButton = findViewById(R.id.submitButton);

                focusedLayout = "";
                focusedView.requestFocus();

                setupPasswordLayout(getApplicationContext(), enterPasswordLayout, background, enterPasswordTitle, passwordField, passwordFieldTitle, cancelButton, submitButton, false, layoutFocusedView, language, theme);
            }
        });

        EnterPasswordLayout.setupSubmitButton(getApplicationContext(), submitButton, layoutFocusedView, language);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText passwordField = findViewById(R.id.password);
                if (passwordField == null) return;

                String password = passwordField.getText().toString();
                checkPasswordMatching(password);
            }
        });
    }


    void setupChooseRatingLayout(Context ctx, FrameLayout chooseRatingLayout, ConstraintLayout background, TextView chooseRatingTitle, Button showRatingsButton, Button cancelButtonRating, Button submitRatingButton, boolean visible, View layoutFocusedView, Language language, Theme theme) {
        if (chooseRatingLayout == null || background == null || chooseRatingTitle == null || showRatingsButton == null || cancelButtonRating == null || submitRatingButton == null) return;

        if (!visible) {
            chooseRatingLayout.setVisibility(View.INVISIBLE);
            return;
        }
        chooseRatingLayout.setVisibility(View.VISIBLE);

        ChooseRatingLayout.setupLayoutTitle(ctx, chooseRatingTitle, language, theme);
        ChooseRatingLayout.setupLayoutBackground(ctx, background, theme);
        ChooseRatingLayout.setupShowRatingButton(ctx, showRatingsButton, layoutFocusedView, language);
        showRatingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RatingListActivity.class));
            }
        });

        ChooseRatingLayout.setupCancelButton(ctx, cancelButtonRating, layoutFocusedView, language);
        cancelButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                focusedLayout = "";

                FrameLayout chooseRatingLayout = findViewById(R.id.chooseRatingLayout);
                ConstraintLayout background = findViewById(R.id.ratingMain);
                TextView chooseRatingTitle = findViewById(R.id.chooseRatingTitle);
                Button showRatingsButton = findViewById(R.id.showRatingsButton);
                Button cancelButtonRating = findViewById(R.id.cancelButtonRating);
                Button submitRatingButton = findViewById(R.id.submitRatingButton);

                // layoutFocusedView = null;

                setupChooseRatingLayout(getApplicationContext(), chooseRatingLayout, background, chooseRatingTitle, showRatingsButton, cancelButtonRating, submitRatingButton, false, layoutFocusedView, language, theme);
            }
        });

        ChooseRatingLayout.setupMyRatingButton(getApplicationContext(), submitRatingButton, layoutFocusedView, language);
        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MyRatingActivity.class));
            }
        });
    }

    public void updateLayoutView(String layout, int row) {
        if (layout.equals("rating")) {
            switch (row) {
                case 0:
                    Button showRatingsButton = findViewById(R.id.showRatingsButton);

                    ChooseRatingLayout.setupShowRatingButton(getApplicationContext(), showRatingsButton, this.layoutFocusedView, this.language);
                    break;
                case 1:
                    Button cancelButtonRating = findViewById(R.id.cancelButtonRating);

                    ChooseRatingLayout.setupCancelButton(getApplicationContext(), cancelButtonRating, this.layoutFocusedView, this.language);
                    break;
                case 2:
                    Button submitRatingButton = findViewById(R.id.submitRatingButton);

                    ChooseRatingLayout.setupMyRatingButton(getApplicationContext(), submitRatingButton, this.layoutFocusedView, this.language);
                    break;
            }
        } else if (layout.equals("password")) {
            switch (row) {
                case 0:
                    EditText passwordField = findViewById(R.id.password);
                    TextView passwordFieldTitle = findViewById(R.id.passwordTitle);

                    EnterPasswordLayout.setupPasswordField(getApplicationContext(), passwordField, passwordFieldTitle, this.layoutFocusedView, this.language, this.theme);
                    break;
                case 1:
                    Button cancelButton = findViewById(R.id.cancelButtonPassword);

                    EnterPasswordLayout.setupCancelButton(getApplicationContext(), cancelButton, this.layoutFocusedView, this.language);
                    break;
                case 2:
                    Button submitButton = findViewById(R.id.submitButton);

                    EnterPasswordLayout.setupSubmitButton(getApplicationContext(), submitButton, this.layoutFocusedView, this.language);
                    break;
            }
        }
    }

    void getCurrentUserName() {
        DocumentReference ref = firestore.collection("estates").document(this.sharedPreferencesService.getEstateId());

        ref.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot queryDocumentSnapshots) {
                    ArrayList<HashMap<String, Object>> guests = (ArrayList<HashMap<String, Object>>)queryDocumentSnapshots.get("guests");
                    if (guests == null || guests.size() == 0) return;

                    int guestsLength = guests.size();
                    for (int i = 0; i < guestsLength; ++i) {
                        Timestamp fromStamp = (Timestamp)(guests.get(i).getOrDefault("from", null));
                        Timestamp toStamp = (Timestamp)(guests.get(i).getOrDefault("to", null));
                        if (fromStamp == null || toStamp == null) continue;

                        long epochFrom = fromStamp.getSeconds();
                        long epochNow = System.currentTimeMillis() / 1000;
                        long epochTo = toStamp.getSeconds();

                        if (epochNow - epochFrom >= 0 && epochTo - epochNow > 0) {
                            userName = (String)(guests.get(i).getOrDefault("name", ""));
                            break;
                        }
                    }
                }
            });
    }

    void setupGridTitle(Context ctx, TextView gridTitle, String userName, Language language, Theme theme) {
        if (gridTitle == null) return;

        switch (language) {
            case german:
                gridTitle.setText(ContextCompat.getString(ctx, R.string.welcome_de) + " " + userName);
                break;
            case croatian:
                gridTitle.setText(ContextCompat.getString(ctx, R.string.welcome_hr) + " " + userName);
                break;
            default:
                gridTitle.setText(ContextCompat.getString(ctx, R.string.welcome_en) + " " + userName);
        }

        if (theme == Theme.light) gridTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else gridTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }
}