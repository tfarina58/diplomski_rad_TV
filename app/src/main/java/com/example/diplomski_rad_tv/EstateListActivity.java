package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class EstateListActivity extends Activity {
    String userId = "TUXPrSDT4VHw1hfYUn5z";
    FirebaseFirestore firestore;
    Estate[] estates;
    ArrayList<Integer> estatesToShow;
    Language language = Language.germany;
    Theme theme = Theme.light;
    Grid grid = Grid.one;
    Clock format = Clock.h12;
    View focusedView;
    String searchbarText = "e";
    int overallIndex = 0;
    int currentIndex = 0;
    int currentPage = 0;
    int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.firestore = FirebaseFirestore.getInstance();
        Query query = firestore.collection("estates").whereEqualTo("ownerId", userId);

        estatesToShow = new ArrayList<>();

        query.get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    int i = 0;
                    estates = new Estate[queryDocumentSnapshots.size()];
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String estateId = document.getId();
                        String name = document.getString("name");
                        String image = document.getString("image");
                        GeoPoint coordinates = document.getGeoPoint("coordinates");
                        HashMap<String, Object> variables = (HashMap<String, Object>) document.get("variables");
                        estates[i] = new Estate(estateId, image, coordinates.getLatitude(), coordinates.getLongitude(), name, userId, variables);
                        i++;
                    }
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
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        int gridType = Grid.getGridTypeAsInt(this.grid);

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = Grid.navigateOverActivity(this.grid, focusedView.getId(), keyCode - 19);

            if (newFocusedViewId == 0) return false;

            // Pressed up
            if (keyCode == 19) {
                if (Grid.isUpperButtons(oldFocusedViewId) || Grid.isFirstRow(oldFocusedViewId) || oldFocusedViewId == R.id.main) {
                    // Do nothing
                } else if (Grid.isSecondRow(oldFocusedViewId)) {
                    this.overallIndex -= 3;
                    this.currentIndex -= 3;
                } else if (Grid.isLowerButtons(oldFocusedViewId)) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                    if (this.estatesToShow.size() == 0) {
                        newFocusedViewId = R.id.languageButton;
                    }
                } else newFocusedViewId = 0;
            }
            // Pressed down
            else if (keyCode == 20) {
                if (Grid.isUpperButtons(oldFocusedViewId)) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                    if (this.estatesToShow.size() == 0) {
                        newFocusedViewId = R.id.searchView;
                    }
                } else if (oldFocusedViewId == R.id.main || Grid.isFirstRow(oldFocusedViewId)) {
                    if (this.grid == Grid.six && this.overallIndex + 3 < this.estatesToShow.size()) {
                        this.overallIndex += 3;
                        this.currentIndex += 3;
                    } else newFocusedViewId = R.id.searchView;
                } else if (Grid.isSecondRow(oldFocusedViewId)) {
                    newFocusedViewId = R.id.searchView;
                } else newFocusedViewId = 0;

            }
            // Pressed left
            if (keyCode == 21) {
                if (Grid.isUpperButtons(oldFocusedViewId) || Grid.isLowerButtons(oldFocusedViewId)) {
                    // Do nothing
                } else if (oldFocusedViewId == R.id.main || (this.grid == Grid.three && Grid.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex - 1 >= 0) {
                        this.overallIndex--;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (this.grid == Grid.six) {
                    if (Grid.isLeftColumn(oldFocusedViewId)) {
                        if (this.overallIndex - 4 >= 0) {
                            this.overallIndex -= 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (Grid.isMiddleColumn(oldFocusedViewId) || Grid.isRightColumn(oldFocusedViewId)) {
                        this.overallIndex--;
                        this.currentPage = this.overallIndex / gridType;
                        this.currentIndex = this.overallIndex % gridType;
                    }
                }
            }
            // Pressed right
            else if (keyCode == 22) {
                if (Grid.isUpperButtons(oldFocusedViewId) || Grid.isLowerButtons(oldFocusedViewId)) {
                    // Do nothing
                } else if (oldFocusedViewId == R.id.main || (this.grid == Grid.three && Grid.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex + 1 < estatesToShow.size()) {
                        this.overallIndex++;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (this.grid == Grid.six) {
                    if (Grid.isRightColumn(oldFocusedViewId)) {
                        if (this.overallIndex + 4 < estatesToShow.size()) {
                            this.overallIndex += 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (Grid.isLeftColumn(oldFocusedViewId) || Grid.isMiddleColumn(oldFocusedViewId)) {
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
                int row = Grid.getRowWithId(oldFocusedViewId);
                updateView(row);

                // Add focus to new View
                row = Grid.getRowWithId(newFocusedViewId);
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
                                    overallIndex = Grid.getGridTypeAsInt(grid) * currentPage;
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
            } else if (oldFocusedViewId == R.id.main) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton1) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton2) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton3) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton4) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton5) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            } else if (oldFocusedViewId == R.id.imageButton6) {
                Toast.makeText(EstateListActivity.this, "Navigate to next page", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                startActivity(i);
            }
        }
        // else return keyCode != 4;

        Toast.makeText(getApplicationContext(), Integer.toString(overallIndex) + ", " + Integer.toString(currentIndex) + ", " + Integer.toString(currentPage) + ", " + Integer.toString(totalPages), Toast.LENGTH_SHORT).show();

        return true;
    }

    void setNewContentView() {
        pickContentView();

        int gridType = Grid.getGridTypeAsInt(this.grid);

        this.currentPage = this.overallIndex / gridType;
        this.currentIndex = this.overallIndex % gridType;
        this.totalPages = (this.estatesToShow.size() - 1) / gridType + 1;

        if (this.grid == Grid.one) setupMain();
        else if (this.grid == Grid.three) {
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

    void pickContentView() {
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

        if (this.theme == Theme.light) main.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.light_theme));
        else if (this.theme == Theme.dark) main.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dark_theme));

        if (this.estatesToShow.size() == 0) {
            TextView centerText = findViewById(R.id.centerText);
            TextView titleText = findViewById(R.id.title1);

            switch (this.language) {
                case united_kingdom:
                    titleText.setText(R.string.real_estate_name_en);
                    centerText.setText(R.string.no_estates_en);
                    if (this.theme == Theme.dark) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                    } else if (this.theme == Theme.light) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                    }
                    break;
                case germany:
                    titleText.setText(R.string.real_estate_name_de);
                    centerText.setText(R.string.no_estates_de);
                    if (this.theme == Theme.dark) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                    } else if (this.theme == Theme.light) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                    }
                    break;
                case croatia:
                    titleText.setText(R.string.real_estate_name_hr);
                    centerText.setText(R.string.no_estates_hr);
                    if (this.theme == Theme.dark) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
                    } else if (this.theme == Theme.light) {
                        titleText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                        centerText.setTextColor(getResources().getColor(R.color.text_color_light_mode));
                    }
                    break;
            }
            return;

        } else if (this.overallIndex < this.estatesToShow.size()) {
            TextView centerText = findViewById(R.id.centerText);
            TextView titleText = findViewById(R.id.title1);

            centerText.setText("");
            centerText.setVisibility(View.INVISIBLE);
            if (this.theme == Theme.dark) titleText.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
            else if (this.theme == Theme.light) titleText.setTextColor(getResources().getColor(R.color.text_color_light_mode));

            if (this.estates[this.estatesToShow.get(this.overallIndex)].image != null && !this.estates[this.estatesToShow.get(this.overallIndex)].image.isEmpty()) {
                try {
                    Picasso.get()
                            .load(this.estates[this.estatesToShow.get(this.overallIndex)].image)
                            .fit()
                            .into(main);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        if (this.focusedView.getId() == R.id.main) {
            // Add shadow on borders and image
        }

        // Setting estate's name as title
        TextView buttonTitle = findViewById(R.id.title1);
        buttonTitle.setText(this.estates[this.estatesToShow.get(this.overallIndex)].name);
    }

    void setupImageButton(int index) {
        Button imageBackground = null;
        ImageButton imageButton = null;
        ImageButton background = findViewById(R.id.background);
        int viewIndex = 0;

        if (this.focusedView == null) {
            this.focusedView = findViewById(R.id.imageButton1);
        }

        if (this.theme == Theme.light) background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.light_theme));
        else if (this.theme == Theme.dark) background.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.dark_theme));

        int gridType = Grid.getGridTypeAsInt(this.grid);

        if (index == 1) {
            imageButton = findViewById(R.id.imageButton1);
            imageBackground = findViewById(R.id.imageButtonBackground1);
            viewIndex = this.currentPage * gridType; // currentIndex?

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[this.estatesToShow.get(viewIndex)].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton1) {
                if (this.theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (this.theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title1 = findViewById(R.id.title1);
            if (viewIndex < this.estatesToShow.size()) title1.setText(this.estates[this.estatesToShow.get(viewIndex)].name); // This if statement has to always be true!

            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title1.setVisibility(View.INVISIBLE);
            }
        } else if (index == 2) {
            imageButton = findViewById(R.id.imageButton2);
            imageBackground = findViewById(R.id.imageButtonBackground2);
            viewIndex = this.currentPage * gridType + 1;

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[this.estatesToShow.get(viewIndex)].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton2) {
                if (this.theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (this.theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title2 = findViewById(R.id.title2);
            if (viewIndex < this.estatesToShow.size()) title2.setText(this.estates[this.estatesToShow.get(viewIndex)].name);

            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title2.setVisibility(View.INVISIBLE);
            }
        }
        else if (index == 3) {
            imageButton = findViewById(R.id.imageButton3);
            imageBackground = findViewById(R.id.imageButtonBackground3);
            viewIndex = this.currentPage * gridType + 2;

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[this.estatesToShow.get(viewIndex)].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton3) {
                if (theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title3 = findViewById(R.id.title3);
            if (viewIndex < this.estatesToShow.size()) title3.setText(this.estates[this.estatesToShow.get(viewIndex)].name);


            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title3.setVisibility(View.INVISIBLE);
            }
        }
        else if (index == 4) {
            imageButton = findViewById(R.id.imageButton4);
            imageBackground = findViewById(R.id.imageButtonBackground4);
            viewIndex = this.currentPage * gridType + 3;

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton4) {
                if (this.theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (this.theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title4 = findViewById(R.id.title4);
            if (viewIndex < this.estatesToShow.size()) title4.setText(this.estates[this.estatesToShow.get(viewIndex)].name);


            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title4.setVisibility(View.INVISIBLE);
            }
        }
        else if (index == 5) {
            imageButton = findViewById(R.id.imageButton5);
            imageBackground = findViewById(R.id.imageButtonBackground5);
            viewIndex = this.currentPage * gridType + 4;

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[this.estatesToShow.get(viewIndex)].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton5) {
                if (this.theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (this.theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title5 = findViewById(R.id.title5);
            if (viewIndex < this.estatesToShow.size()) title5.setText(this.estates[this.estatesToShow.get(viewIndex)].name);


            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title5.setVisibility(View.INVISIBLE);
            }
        }
        else if (index == 6) {
            imageButton = findViewById(R.id.imageButton6);
            imageBackground = findViewById(R.id.imageButtonBackground6);
            viewIndex = this.currentPage * gridType + 5;

            if (viewIndex < this.estatesToShow.size() && this.estates[this.estatesToShow.get(viewIndex)].image != null && this.estates[this.estatesToShow.get(viewIndex)].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[this.estatesToShow.get(viewIndex)].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (this.focusedView.getId() == R.id.imageButton6) {
                if (this.theme == Theme.light) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_light));
                else if (this.theme == Theme.dark) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button_dark));
            } else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title6 = findViewById(R.id.title6);
            if (viewIndex < this.estatesToShow.size()) title6.setText(this.estates[this.estatesToShow.get(viewIndex)].name);

            if (viewIndex >= this.estatesToShow.size()) {
                imageButton.setVisibility(View.INVISIBLE);
                title6.setVisibility(View.INVISIBLE);
            }
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
        // searchbarButton.setIconified(false);

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
        else if (row == 6 && this.grid == Grid.one) setupMain();
        else if (row == 6 && (this.grid == Grid.three || this.grid == Grid.six)) setupImageButton(1);
        else if (row == 7) setupImageButton(2);
        else if (row == 8) setupImageButton(3);
        else if (row == 9) setupImageButton(4);
        else if (row == 10) setupImageButton(5);
        else if (row == 11) setupImageButton(6);
    }

    void setupEstatesToShow() {
        this.estatesToShow.clear();
        for (int i = 0; i < estates.length; ++i) {
            if (estates[i].name.contains(this.searchbarText)) this.estatesToShow.add(i);
        }
    }
}
