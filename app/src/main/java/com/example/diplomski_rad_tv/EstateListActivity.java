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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    SharedPreferencesService sharedPreferencesService;
    Estate[] estates;
    ArrayList<Integer> estatesToShow;
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
    String focusedLayout = ""; // "rating" or ""
    View layoutFocusedView;
    String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.userId = this.sharedPreferencesService.getUserId();
        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();
        this.grid = this.sharedPreferencesService.getGrid();
        this.format = this.sharedPreferencesService.getClockFormat();
        this.estatesToShow = new ArrayList<>();

        this.firestore = FirebaseFirestore.getInstance();

        String orderBy = "name";
        switch(this.language) {
            case german:
                orderBy += ".de";
                break;
            case croatian:
                orderBy += ".hr";
                break;
            default:
                orderBy += ".en";
                break;
        }

        Query query = firestore.collection("estates").whereEqualTo("ownerId", userId).orderBy(orderBy, Query.Direction.ASCENDING);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 0;
                        estates = new Estate[queryDocumentSnapshots.size()];
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String estateId = document.getId();
                            String ownerId = document.getString("ownerId");
                            HashMap<String, String> name = (HashMap<String, String>) document.get("name");
                            String image = document.getString("image");
                            GeoPoint coordinates = document.getGeoPoint("coordinates");
                            estates[i] = new Estate(estateId, ownerId, image, coordinates, name);
                            i++;
                        }
                        loadingInProgress = false;
                        setupEstatesToShow();

                        currentPage = 0;
                        totalPages = estatesToShow.size() == 0 ? 0 : (estatesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;
                        backgroundAlreadySet = false;

                        setNewContentView();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        estates = new Estate[0];
                        loadingInProgress = false;
                        setupEstatesToShow();
                        setNewContentView();
                    }
                });


        this.estates = new Estate[0];
        setupEstatesToShow();
        setNewContentView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = this.focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            if (specialCaseNavigation(oldFocusedViewId, keyCode - 19)) return true;

            int newFocusedViewId = GridNavigation.navigateOverActivity(true, false, this.grid, oldFocusedViewId, keyCode - 19);

            // If navigating up or down
            if (keyCode == 19 || keyCode == 20)
                while (!checkViewExistence(newFocusedViewId) && newFocusedViewId != 0)
                    newFocusedViewId = GridNavigation.navigateOverActivity(true, false, this.grid, newFocusedViewId, keyCode - 19);

            if (newFocusedViewId == 0 || !checkViewExistence(newFocusedViewId)) return false;

            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = GridNavigation.getEstateRowWithId(oldFocusedViewId);
            updateView(row);

            // Add focus to new View
            row = GridNavigation.getEstateRowWithId(newFocusedViewId);
            updateView(row);

            if (newFocusedViewId == R.id.searchView || newFocusedViewId == R.id.pagination) {
                // this.focusedView.requestFocus();
                this.focusedView.callOnClick();
            }
        }
        // Enter button
        else if (keyCode == 23) this.focusedView.callOnClick();
        else if (keyCode == 4) {
            this.sharedPreferencesService.setUserId("");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

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
            Estate[] tmpEstates = new Estate[3];
            for (int i = 0; i < 3; ++i)
                if (this.currentPage * gridType + i < this.estatesToShow.size())
                    tmpEstates[i] = this.estates[this.estatesToShow.get(this.currentPage * gridType + i)];

            this.setupGrid(getApplicationContext(), this.focusedView, this.language, this.theme, this.loadingInProgress, tmpEstates);
        } else {
            Estate[] tmpEstates = new Estate[6];
            for (int i = 0; i < 6; ++i)
                if (this.currentPage * gridType + i < this.estatesToShow.size())
                    tmpEstates[i] = this.estates[this.estatesToShow.get(this.currentPage * gridType + i)];

            this.setupGrid(getApplicationContext(), this.focusedView, this.language, this.theme, this.loadingInProgress, tmpEstates);
        }

        {
            Button button;
            ImageView icon;

            // Rating button
            button = findViewById(R.id.ratingButton);
            icon = findViewById(R.id.ratingIcon);

            RatingHeaderButton.setupRatingButton(getApplicationContext(), button, icon, false, this.focusedView, this.language, this.theme);

            // Language button
            button = findViewById(R.id.languageButton);
            icon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), button, icon, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    sharedPreferencesService.setLanguage(language);

                    setupEstatesToShow();
                    totalPages = estatesToShow.size() == 0 ? 0 : (estatesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;

                    if (grid == GridNavigation.one) {
                        TextView titleText = findViewById(R.id.gridTitle1);

                        if (currentPage < estatesToShow.size()) setMainTitle(getApplicationContext(), titleText, estates[estatesToShow.get(currentPage)].name, language, theme, loadingInProgress, estatesToShow.size(), currentPage);
                        else setMainTitle(getApplicationContext(), titleText, null, language, theme, loadingInProgress, estatesToShow.size(), currentPage);
                    } else if (grid == GridNavigation.three) {
                        Estate[] tmpEstates = new Estate[3];
                        for (int i = 0; i < 3; ++i)
                            if (currentPage * gridType + i < estatesToShow.size())
                                tmpEstates[i] = estates[estatesToShow.get(currentPage * gridType + i)];

                        setupGrid(getApplicationContext(), focusedView, language, theme, loadingInProgress, tmpEstates);
                    } else if (grid == GridNavigation.six) {
                        Estate[] tmpEstates = new Estate[6];
                        for (int i = 0; i < 6; ++i)
                            if (currentPage * gridType + i < estatesToShow.size())
                                tmpEstates[i] = estates[estatesToShow.get(currentPage * gridType + i)];

                        setupGrid(getApplicationContext(), focusedView, language, theme, loadingInProgress, tmpEstates);
                    }

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, estatesToShow.size(), "estates");

                    SearchView searchbarButton = findViewById(R.id.searchView);
                    SearchbarButton.setupSearchBarButton(getApplicationContext(), searchbarButton, searchbarText, focusedView, language, theme);

                    Button pagination = findViewById(R.id.pagination);
                    EditText pageNumber = findViewById(R.id.pageNumber);
                    TextView paginationSlash = findViewById(R.id.paginationSlash);
                    TextView totalPagesNumber = findViewById(R.id.totalPagesNumber);
                    PaginationButton.setupPaginationButton(getApplicationContext(), pagination, pageNumber, paginationSlash, totalPagesNumber, focusedView, theme, estatesToShow.size(), currentPage, totalPages);

                    updateView(0);
                    updateView(1);
                    updateView(2);
                    updateView(4);
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

                    // updateView(1);
                    setupHeaderButtons();

                    {
                        SearchView searchbarButton = findViewById(R.id.searchView);
                        SearchbarButton.setupSearchBarButton(getApplicationContext(), searchbarButton, searchbarText, focusedView, language, theme);
                    }

                    {
                        Button paginationButton = findViewById(R.id.pagination);
                        EditText paginationCurrentPage = findViewById(R.id.pageNumber);
                        TextView paginationSlash = findViewById(R.id.paginationSlash);
                        TextView paginationTotalPages = findViewById(R.id.totalPagesNumber);

                        PaginationButton.setupPaginationButton(getApplicationContext(), paginationButton, paginationCurrentPage, paginationSlash, paginationTotalPages, focusedView, theme, estatesToShow.size(), currentPage, totalPages);
                    }

                    TextView centerText = findViewById(R.id.centerText);
                    CenterText.setupCenterText(getApplicationContext(), centerText, language, theme, loadingInProgress, estatesToShow.size(), "estates");

                    if (grid == GridNavigation.one) updateView(6);
                    else if (grid == GridNavigation.three) {
                        {
                            ImageButton background = findViewById(R.id.backgroundGrid3);
                            setupGridBackground(getApplicationContext(), background, theme);
                        }

                        {
                            TextView gridTitle = findViewById(R.id.gridTitle3);
                            setupGridTitle(gridTitle);
                        }

                        ImageButton imageButton;
                        Button imageBackground;
                        TextView imageTitle;
                        int viewIndex;

                        imageButton = findViewById(R.id.gridButton1);
                        imageBackground = findViewById(R.id.gridButtonBackground1);
                        imageTitle = findViewById(R.id.gridButtonTitle1);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid);

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton2);
                        imageBackground = findViewById(R.id.gridButtonBackground2);
                        imageTitle = findViewById(R.id.gridButtonTitle2);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 1;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton3);
                        imageBackground = findViewById(R.id.gridButtonBackground3);
                        imageTitle = findViewById(R.id.gridButtonTitle3);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 2;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);
                    } else {
                        {
                            ImageButton background = findViewById(R.id.backgroundGrid6);
                            setupGridBackground(getApplicationContext(), background, theme);
                        }

                        {
                            TextView gridTitle = findViewById(R.id.gridTitle6);
                            setupGridTitle(gridTitle);
                        }

                        ImageButton imageButton;
                        Button imageBackground;
                        TextView imageTitle;
                        int viewIndex;

                        imageButton = findViewById(R.id.gridButton1);
                        imageBackground = findViewById(R.id.gridButtonBackground1);
                        imageTitle = findViewById(R.id.gridButtonTitle1);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid);

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton2);
                        imageBackground = findViewById(R.id.gridButtonBackground2);
                        imageTitle = findViewById(R.id.gridButtonTitle2);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 1;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton3);
                        imageBackground = findViewById(R.id.gridButtonBackground3);
                        imageTitle = findViewById(R.id.gridButtonTitle3);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 2;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton4);
                        imageBackground = findViewById(R.id.gridButtonBackground4);
                        imageTitle = findViewById(R.id.gridButtonTitle4);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 3;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton5);
                        imageBackground = findViewById(R.id.gridButtonBackground5);
                        imageTitle = findViewById(R.id.gridButtonTitle5);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 4;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

                        imageButton = findViewById(R.id.gridButton6);
                        imageBackground = findViewById(R.id.gridButtonBackground6);
                        imageTitle = findViewById(R.id.gridButtonTitle6);
                        viewIndex = currentPage * GridNavigation.getGridTypeAsInt(grid) + 5;

                        if (viewIndex < estatesToShow.size()) GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, estates[viewIndex]);
                        else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);
                    };
                }
            });

            // Grid button
            button = findViewById(R.id.gridButton);
            icon = findViewById(R.id.gridIcon);

            GridHeaderButton.setupGridButton(getApplicationContext(), button, icon, this.focusedView, this.language, this.theme);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grid = grid.next();
                    sharedPreferencesService.setGrid(grid);

                    currentPage = GridNavigation.getNewPageNumber(currentPage, grid);
                    totalPages = estatesToShow.size() == 0 ? 0 : (estatesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;
                    backgroundAlreadySet = false;

                    setNewContentView();
                }
            });

            button = findViewById(R.id.weatherButton);
            icon = findViewById(R.id.weatherIcon);

            WeatherHeaderButton.setupWeatherButton(getApplicationContext(), button, icon, focusedView, true, 0, -999, "C", theme);
        }

        {
            // Clock button
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);

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

            SearchbarButton.setupSearchBarButton(getApplicationContext(), searchbarButton, this.searchbarText, this.focusedView, this.language, this.theme);

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
                    setupEstatesToShow();

                    currentPage = 0;
                    totalPages = estatesToShow.size() == 0 ? 0 : (estatesToShow.size() - 1) / GridNavigation.getGridTypeAsInt(grid) + 1;

                    setNewContentView();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.isEmpty()) {
                        searchbarText = "";
                        setupEstatesToShow();
                    }
                    return true;
                }
            });
        }

        {
            // Pagination button
            Button paginationButton = findViewById(R.id.pagination);
            EditText paginationCurrentPage = findViewById(R.id.pageNumber);
            TextView paginationSlash = findViewById(R.id.paginationSlash);
            TextView paginationTotalPages = findViewById(R.id.totalPagesNumber);

            PaginationButton.setupPaginationButton(getApplicationContext(), paginationButton, paginationCurrentPage, paginationSlash, paginationTotalPages, this.focusedView, this.theme, this.estatesToShow.size(), this.currentPage, this.totalPages);

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

        if (this.currentPage < this.estatesToShow.size()) {
            this.setupMainBackground(ctx, main, this.focusedView, this.estates[this.estatesToShow.get(this.currentPage)].image, this.theme);
            this.setMainTitle(ctx, titleText, this.estates[this.estatesToShow.get(this.currentPage)].name, this.language, this.theme, this.loadingInProgress, this.estatesToShow.size(), this.currentPage);
        } else {
            this.setupMainBackground(ctx, main, this.focusedView, "", this.theme);
            this.setMainTitle(ctx, titleText, null, this.language, this.theme, this.loadingInProgress, this.estatesToShow.size(), this.currentPage);
        }

        ProgressBarLoader.manageProgressBar(ctx, progressBar, this.theme, this.loadingInProgress);
        CenterText.setupCenterText(ctx, centerText, this.language, this.theme, this.loadingInProgress, this.estatesToShow.size(), "estates");

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCategoryListActivity(estates[estatesToShow.get(currentPage)].id, estates[estatesToShow.get(currentPage)].coordinates);
            }
        });
    }


    void setMainTitle(Context ctx, TextView titleText, HashMap<String, String> title, Language language, Theme theme, boolean loadingInProgress, int estatesToShowSize, int overallIndex) {
        if (titleText == null) return;

        if (loadingInProgress || /*!loadingInProgress &&*/ estatesToShowSize == 0) {
            switch (language) {
                case german:
                    titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_de));
                    break;
                case croatian:
                    titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_hr));
                    break;
                default:
                    titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_en));
            }
        } else if (overallIndex < estatesToShowSize) {
            switch (language) {
                case german:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_de));
                        break;
                    }

                    String titleDe = title.get("de");

                    if (titleDe != null && !titleDe.isEmpty()) titleText.setText(titleDe);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_de));
                    break;
                case croatian:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_hr));
                        break;
                    }

                    String titleHr = title.get("hr");

                    if (titleHr != null && !titleHr.isEmpty()) titleText.setText(titleHr);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_hr));
                    break;
                default:
                    if (title == null) {
                        titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_en));
                        break;
                    }

                    String titleEn = title.get("en");

                    if (titleEn != null && !titleEn.isEmpty()) titleText.setText(titleEn);
                    else titleText.setText(ContextCompat.getString(ctx, R.string.real_estate_name_en));
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

    void updateView(int row) {
        if (row == 0) {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);
        } else if (row == 1) {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
        } else if (row == 2) {
            Button gridButton = findViewById(R.id.gridButton);
            ImageView gridIcon = findViewById(R.id.gridIcon);

            GridHeaderButton.setupGridButton(getApplicationContext(), gridButton, gridIcon, this.focusedView, this.language, this.theme);
        } else if (row == 3) {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
        } else if (row == 4) {
            SearchView searchbarButton = findViewById(R.id.searchView);

            SearchbarButton.setupSearchBarButton(getApplicationContext(), searchbarButton, this.searchbarText, this.focusedView, this.language, this.theme);
        } else if (row == 5) {
            Button paginationButton = findViewById(R.id.pagination);
            EditText paginationCurrentPage = findViewById(R.id.pageNumber);
            TextView paginationSlash = findViewById(R.id.paginationSlash);
            TextView paginationTotalPages = findViewById(R.id.totalPagesNumber);

            PaginationButton.setupPaginationButton(getApplicationContext(), paginationButton, paginationCurrentPage, paginationSlash, paginationTotalPages, this.focusedView, this.theme, this.estatesToShow.size(), this.currentPage, this.totalPages);
        } else if (row == 6 && this.grid == GridNavigation.one) {
            ImageButton main = findViewById(R.id.backgroundGrid1);
            TextView title = findViewById(R.id.gridTitle1);

            if (this.currentPage < this.estatesToShow.size()) {
                this.setupMainBackground(getApplicationContext(), main, this.focusedView, this.estates[this.estatesToShow.get(this.currentPage)].image, this.theme);
                this.setMainTitle(getApplicationContext(), title, this.estates[this.estatesToShow.get(this.currentPage)].name, this.language, this.theme, this.loadingInProgress, this.estatesToShow.size(), this.currentPage);
            } else {
                this.setupMainBackground(getApplicationContext(), main, this.focusedView, "", this.theme);
                this.setMainTitle(getApplicationContext(), title, null, this.language, this.theme, this.loadingInProgress, this.estatesToShow.size(), this.currentPage);
            }
        } else if (row == 6 && (this.grid == GridNavigation.three || this.grid == GridNavigation.six)) {
            ImageButton imageButton = findViewById(R.id.gridButton1);
            Button imageBackground = findViewById(R.id.gridButtonBackground1);
            TextView imageTitle = findViewById(R.id.gridButtonTitle1);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        } else if (row == 7) {
            ImageButton imageButton = findViewById(R.id.gridButton2);
            Button imageBackground = findViewById(R.id.gridButtonBackground2);
            TextView imageTitle = findViewById(R.id.gridButtonTitle2);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 1;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        } else if (row == 8) {
            ImageButton imageButton = findViewById(R.id.gridButton3);
            Button imageBackground = findViewById(R.id.gridButtonBackground3);
            TextView imageTitle = findViewById(R.id.gridButtonTitle3);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 2;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        } else if (row == 9) {
            ImageButton imageButton = findViewById(R.id.gridButton4);
            Button imageBackground = findViewById(R.id.gridButtonBackground4);
            TextView imageTitle = findViewById(R.id.gridButtonTitle4);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 3;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        } else if (row == 10) {
            ImageButton imageButton = findViewById(R.id.gridButton5);
            Button imageBackground = findViewById(R.id.gridButtonBackground5);
            TextView imageTitle = findViewById(R.id.gridButtonTitle5);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 4;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        } else if (row == 11) {
            ImageButton imageButton = findViewById(R.id.gridButton6);
            Button imageBackground = findViewById(R.id.gridButtonBackground6);
            TextView imageTitle = findViewById(R.id.gridButtonTitle6);
            int viewIndex = GridNavigation.getGridTypeAsInt(grid) * currentPage + 5;

            if (viewIndex < this.estatesToShow.size())
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, this.estates[this.estatesToShow.get(viewIndex)]);
            else
                GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, this.focusedView, this.language, this.theme, (Estate) null);
        }
    }

    void setupEstatesToShow() {
        this.estatesToShow.clear();
        if (searchbarText.trim().equals("")) {
            for (int i = 0; i < estates.length; ++i) estatesToShow.add(i);
            return;
        }
        for (int i = 0; i < estates.length; ++i)
            switch (language) {
                case german:
                    String titleDe = estates[i].name.get("de");
                    if (titleDe == null) break;

                    if (titleDe.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.estatesToShow.add(i);
                    break;
                case croatian:
                    String titleHr = estates[i].name.get("hr");
                    if (titleHr == null) break;

                    if (titleHr.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.estatesToShow.add(i);
                    break;
                default:
                    String titleEn = estates[i].name.get("en");
                    if (titleEn == null) break;

                    if (titleEn.toLowerCase().contains(this.searchbarText.toLowerCase()))
                        this.estatesToShow.add(i);
            }
    }

    void navigateToCategoryListActivity(String estateId, GeoPoint coordinates) {
        this.sharedPreferencesService.setEstateId(estateId);
        if (coordinates != null)
            this.sharedPreferencesService.setEstateCoordinates(coordinates.getLatitude(), coordinates.getLongitude());

        startActivity(new Intent(getApplicationContext(), CategoryListActivity.class));
    }

    boolean checkViewExistence(int focusedViewId) {
        switch (this.grid) {
            case three:
                if (focusedViewId == R.id.ratingButton) return false;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.gridButton1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton2) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 1;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton3) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 2;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                break;
            case six:
                if (focusedViewId == R.id.ratingButton) return false;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.gridButton1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton2) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 1;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton3) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 2;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton4) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 3;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton5) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 4;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                if (focusedViewId == R.id.gridButton6) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 5;
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
                }
                break;
            default:
                if (focusedViewId == R.id.ratingButton) return false;
                if (focusedViewId == R.id.languageButton) return true;
                if (focusedViewId == R.id.themeButton) return true;
                if (focusedViewId == R.id.gridButton) return true;
                if (focusedViewId == R.id.textClock) return true;
                if (focusedViewId == R.id.searchView) return true;
                if (focusedViewId == R.id.pagination) return true;
                if (focusedViewId == R.id.backgroundGrid1) {
                    int viewIndex = this.currentPage * GridNavigation.getGridTypeAsInt(this.grid);
                    return this.estatesToShow.size() > viewIndex && !this.estates[this.estatesToShow.get(viewIndex)].name.isEmpty();
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
                    } else if (this.grid == GridNavigation.three) {
                        this.focusedView = findViewById(R.id.gridButton3);
                    } else if (this.grid == GridNavigation.six) {
                        if (GridNavigation.isFirstRow(oldFocusedViewId))
                            this.focusedView = findViewById(R.id.gridButton3);
                        else if (GridNavigation.isSecondRow(oldFocusedViewId))
                            this.focusedView = findViewById(R.id.gridButton6);

                    }
                    this.focusedView.requestFocus();
                    this.setNewContentView();
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
                    } else if (this.grid == GridNavigation.three) {
                        this.focusedView = findViewById(R.id.gridButton1);
                    } else if (this.grid == GridNavigation.six) {
                        if (GridNavigation.isFirstRow(oldFocusedViewId))
                            this.focusedView = findViewById(R.id.gridButton1);
                        else if (GridNavigation.isSecondRow(oldFocusedViewId))
                            if (this.currentPage * GridNavigation.getGridTypeAsInt(this.grid) + 3 < this.estatesToShow.size())
                                this.focusedView = findViewById(R.id.gridButton4);
                            else
                                this.focusedView = findViewById(R.id.gridButton1);
                    }
                    this.focusedView.requestFocus();
                    this.setNewContentView();
                }
                return true;
            }
        }
        return false;
    }

    void setupGrid(Context ctx, View focusedView, Language language, Theme theme, boolean loadingInProgress, Estate[] shownEstates) {
        TextView gridTitle = null;
        if (this.grid == GridNavigation.three) gridTitle = findViewById(R.id.gridTitle3);
        else if (this.grid == GridNavigation.six) gridTitle = findViewById(R.id.gridTitle6);

        this.setupGridTitle(gridTitle);

        ImageButton imageButton;
        Button imageBackground;
        TextView imageTitle;

        // Image button 1
        imageButton = findViewById(R.id.gridButton1);
        imageBackground = findViewById(R.id.gridButtonBackground1);
        imageTitle = findViewById(R.id.gridButtonTitle1);

        if (0 < shownEstates.length && shownEstates[0] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[0]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType)].id, estates[estatesToShow.get(currentPage * gridType)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Image button 2
        imageButton = findViewById(R.id.gridButton2);
        imageBackground = findViewById(R.id.gridButtonBackground2);
        imageTitle = findViewById(R.id.gridButtonTitle2);

        if (1 < shownEstates.length && shownEstates[1] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[1]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType + 1)].id, estates[estatesToShow.get(currentPage * gridType + 1)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Image button 3
        imageButton = findViewById(R.id.gridButton3);
        imageBackground = findViewById(R.id.gridButtonBackground3);
        imageTitle = findViewById(R.id.gridButtonTitle3);

        if (2 < shownEstates.length && shownEstates[2] != null) {
            GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[2]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType + 2)].id, estates[estatesToShow.get(currentPage * gridType + 2)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(ctx, imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Image button 4
        imageButton = findViewById(R.id.gridButton4);
        imageBackground = findViewById(R.id.gridButtonBackground4);
        imageTitle = findViewById(R.id.gridButtonTitle4);

        if (3 < shownEstates.length && shownEstates[3] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[3]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType + 3)].id, estates[estatesToShow.get(currentPage * gridType + 3)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Image button 5
        imageButton = findViewById(R.id.gridButton5);
        imageBackground = findViewById(R.id.gridButtonBackground5);
        imageTitle = findViewById(R.id.gridButtonTitle5);

        if (4 < shownEstates.length && shownEstates[4] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[4]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType + 4)].id, estates[estatesToShow.get(currentPage * gridType + 4)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Image button 6
        imageButton = findViewById(R.id.gridButton6);
        imageBackground = findViewById(R.id.gridButtonBackground6);
        imageTitle = findViewById(R.id.gridButtonTitle6);

        if (5 < shownEstates.length && shownEstates[5] != null) {
            GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, shownEstates[5]);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int gridType = GridNavigation.getGridTypeAsInt(grid);
                    navigateToCategoryListActivity(estates[estatesToShow.get(currentPage * gridType + 5)].id, estates[estatesToShow.get(currentPage * gridType + 5)].coordinates);
                }
            });
        } else GridImageButton.setupImageButton(getApplicationContext(), imageButton, imageBackground, imageTitle, focusedView, language, theme, (Estate) null);

        // Center text
        TextView centerText = findViewById(R.id.centerText);

        CenterText.setupCenterText(ctx, centerText, language, theme, loadingInProgress, estatesToShow.size(), "estates");

        // Background
        ImageButton background = null;
        if (grid == GridNavigation.three) background = findViewById(R.id.backgroundGrid3);
        else if (grid == GridNavigation.six) background = findViewById(R.id.backgroundGrid6);

        this.setupGridBackground(ctx, background, theme);

        // Progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);

        ProgressBarLoader.manageProgressBar(ctx, progressBar, theme, loadingInProgress);
    }

    void setupGridTitle(TextView gridTitle) {
        if (gridTitle == null) return;
        gridTitle.setText("");
    }

    void setupHeaderButtons() {
        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language, this.theme);

        Button themeButton = findViewById(R.id.themeButton);
        ImageView themeIcon = findViewById(R.id.themeIcon);

        ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);

        Button gridButton = findViewById(R.id.gridButton);
        ImageView gridIcon = findViewById(R.id.gridIcon);

        GridHeaderButton.setupGridButton(getApplicationContext(), gridButton, gridIcon, this.focusedView, this.language, this.theme);

        TextClock textClock = findViewById(R.id.textClock);

        ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format, this.theme);
    }
}