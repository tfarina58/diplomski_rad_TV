package com.example.diplomski_rad_tv;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;

public class EstateListActivity extends Activity {
    Estate[] estates;
    Language language = Language.german;
    Theme theme = Theme.light;
    Grid grid = BasicGridNavigation.one;
    Clock format = Clock.h12;
    View focusedView;
    String searchbarText = "";
    int overallIndex = 0;
    int currentIndex = 0;
    int currentPage = 0;
    int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.estates = new Estate[6];
        this.estates[0] = new Estate(1);
        this.estates[1] = new Estate(2);
        this.estates[2] = new Estate(1);
        this.estates[3] = new Estate(1);
        this.estates[4] = new Estate(2);
        this.estates[5] = new Estate(1);
        // this.estates[6] = new Estate(2);

        setNewContentView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int oldFocusedViewId = focusedView.getId();

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = BasicGridNavigation.navigateOverActivity(grid, focusedView.getId(), keyCode - 19);

            // Pressed up
            if (keyCode == 19) {
                if (oldFocusedViewId == R.id.imageButton4 || oldFocusedViewId == R.id.imageButton5 || oldFocusedViewId == R.id.imageButton6) {
                    this.overallIndex -= 3;
                    this.currentIndex -= 3;
                } else if (oldFocusedViewId == R.id.searchView || oldFocusedViewId == R.id.pageIndex) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                }
            }
            // Pressed down
            else if (keyCode == 20) {
                if ((oldFocusedViewId == R.id.imageButton1 || oldFocusedViewId == R.id.imageButton2 || oldFocusedViewId == R.id.imageButton3) && grid == BasicGridNavigation.six && this.overallIndex + 3 < estates.length) {
                    this.overallIndex += 3;
                    this.currentIndex += 3;
                } else if (BasicGridNavigation.isUpperButtons(oldFocusedViewId)) {
                    this.currentIndex = 0;
                    this.overallIndex = this.currentPage * gridType;
                }

            }
            // Pressed left
            if (keyCode == 21) {
                if (oldFocusedViewId == R.id.main || (grid == BasicGridNavigation.three && BasicGridNavigation.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex - 1 >= 0) {
                        this.overallIndex--;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (grid == BasicGridNavigation.six) {
                    if (oldFocusedViewId == R.id.imageButton1 || oldFocusedViewId == R.id.imageButton4) {
                        if (this.overallIndex - 4 >= 0) {
                            this.overallIndex -= 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (oldFocusedViewId == R.id.imageButton2 || oldFocusedViewId == R.id.imageButton3 || oldFocusedViewId == R.id.imageButton5 || oldFocusedViewId == R.id.imageButton6) {
                        this.overallIndex--;
                        this.currentPage = this.overallIndex / gridType;
                        this.currentIndex = this.overallIndex % gridType;
                    }
                }
            }
            // Pressed right
            else if (keyCode == 22) {
                if (oldFocusedViewId == R.id.main || (grid == BasicGridNavigation.three && BasicGridNavigation.isFirstRow(oldFocusedViewId))) {
                    if (this.overallIndex + 1 < estates.length) {
                        this.overallIndex++;
                        this.currentIndex = this.overallIndex % gridType;

                        int oldPage = this.currentPage;
                        this.currentPage = this.overallIndex / gridType;
                        if (oldPage != this.currentPage) setNewContentView();
                    } else newFocusedViewId = 0; // TODO: return false;
                } else if (grid == BasicGridNavigation.six) {
                    if (oldFocusedViewId == R.id.imageButton3 || oldFocusedViewId == R.id.imageButton6) {
                        if (this.overallIndex + 4 < estates.length) {
                            this.overallIndex += 4;
                            this.currentPage = this.overallIndex / gridType;
                            this.currentIndex = this.overallIndex % gridType;
                            setNewContentView();
                        } else newFocusedViewId = 0; // TODO: return false;
                    } else if (oldFocusedViewId == R.id.imageButton1 || oldFocusedViewId == R.id.imageButton2 || oldFocusedViewId == R.id.imageButton4 || oldFocusedViewId == R.id.imageButton5) {
                        this.overallIndex++;
                        this.currentPage = this.overallIndex / gridType;
                        this.currentIndex = this.overallIndex % gridType;
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
                setupLanguageButton();
            } else if (oldFocusedViewId == R.id.themeButton) {
                theme = theme.next();
                focusedView = findViewById(R.id.themeButton);
                setupThemeButton();
            } else if (oldFocusedViewId == R.id.gridButton) {
                grid = grid.next();
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
                setNewContentView();

                // TODO: calculate all variables and content to show
            } else if (oldFocusedViewId == R.id.pageIndex) {
                /*
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SelectPageFragment fragment = new SelectPageFragment();
                fragmentTransaction.add(R.id.fragmentContainerView, fragment);
                fragmentTransaction.commit();
                */

                focusedView = findViewById(R.id.pageIndex);
                setNewContentView();
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

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);

        this.currentPage = this.overallIndex / gridType;
        this.currentIndex = this.overallIndex % gridType;
        this.totalPages = (this.estates.length - 1) / gridType + 1;

        if (grid == BasicGridNavigation.one) setupMain();
        else if (grid == BasicGridNavigation.three) {
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
        switch (grid) {
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
        ImageButton main = findViewById(R.id.main);

        if (main == null) return;
        if (focusedView == null) {
            focusedView = main;
        }

        if (this.overallIndex < estates.length && this.estates[this.overallIndex].image != null && this.estates[this.overallIndex].image.length() > 0) {
            try {
                ImageLoader imageLoader = new ImageLoader(main);
                imageLoader.execute(this.estates[this.overallIndex].image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (focusedView.getId() == R.id.main) {
            // Add shadow on borders and image
        }

        // Setting estate's name as title
        TextView buttonTitle = findViewById(R.id.title1);
        buttonTitle.setText(estates[overallIndex].name);
    }

    void setupImageButton(int index) {
        Button imageBackground = null;
        ImageButton imageButton = null;
        int viewIndex = 0;

        if (focusedView == null) {
            focusedView = findViewById(R.id.imageButton1);
        }

        int gridType = BasicGridNavigation.getGridTypeAsInt(this.grid);

        if (index == 1) {
            imageButton = findViewById(R.id.imageButton1);
            imageBackground = findViewById(R.id.imageButtonBackground1);
            viewIndex = this.currentPage * gridType; // currentIndex?

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton1) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title1 = findViewById(R.id.title1);
            if (viewIndex < estates.length) title1.setText(estates[viewIndex].name); // This if statement has to always be true!
        } else if (index == 2) {
            imageButton = findViewById(R.id.imageButton2);
            imageBackground = findViewById(R.id.imageButtonBackground2);
            viewIndex = this.currentPage * gridType + 1;

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton2) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title2 = findViewById(R.id.title2);
            if (viewIndex < estates.length) title2.setText(estates[viewIndex].name);
        }
        else if (index == 3) {
            imageButton = findViewById(R.id.imageButton3);
            imageBackground = findViewById(R.id.imageButtonBackground3);
            viewIndex = this.currentPage * gridType + 2;

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton3) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title3 = findViewById(R.id.title3);
            if (viewIndex < estates.length) title3.setText(estates[viewIndex].name);
        }
        else if (index == 4) {
            imageButton = findViewById(R.id.imageButton4);
            imageBackground = findViewById(R.id.imageButtonBackground4);
            viewIndex = this.currentPage * gridType + 3;

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton4) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title4 = findViewById(R.id.title4);
            if (viewIndex < estates.length) title4.setText(estates[viewIndex].name);
        }
        else if (index == 5) {
            imageButton = findViewById(R.id.imageButton5);
            imageBackground = findViewById(R.id.imageButtonBackground5);
            viewIndex = this.currentPage * gridType + 4;

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton5) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title5 = findViewById(R.id.title5);
            if (viewIndex < estates.length) title5.setText(estates[viewIndex].name);
        }
        else if (index == 6) {
            imageButton = findViewById(R.id.imageButton6);
            imageBackground = findViewById(R.id.imageButtonBackground6);
            viewIndex = this.currentPage * gridType + 5;

            if (viewIndex < estates.length && this.estates[viewIndex].image != null && this.estates[viewIndex].image.length() > 0) {
                try {
                    ImageLoader imageLoader = new ImageLoader(imageButton);
                    imageLoader.execute(this.estates[viewIndex].image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (focusedView.getId() == R.id.imageButton6) imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_image_button));
            else imageBackground.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.image_button));

            TextView title6 = findViewById(R.id.title6);
            if (viewIndex < estates.length) title6.setText(estates[viewIndex].name);
        }
    }
    void setupLanguageButton() {
        Button languageButton = findViewById(R.id.languageButton);
        ImageView languageIcon = findViewById(R.id.languageIcon);

        if (languageButton == null) return;
        switch (language) {
            case german:
                languageButton.setText("Deutsche");
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.german));
                break;
            case croatiann:
                languageButton.setText("Hrvatski");
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.croatiann));
                break;
            default:
                languageButton.setText("English");
                languageIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.english));
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
                themeButton.setText("Light");
                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sun));
                break;
            case dark:
                themeButton.setText("Dark");
                themeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.moon));
                break;
        }

        if (focusedView.getId() == R.id.themeButton) themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.highlighted_header_button));
        else themeButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.header_button));
    }

    void setupGridButton() {
        Button gridButton = findViewById(R.id.gridButton);

        if (gridButton == null) return;
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

        if (paginationButton == null) return;
        paginationButton.setText((this.currentPage + 1) + "/" + this.totalPages);

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
        else if (row == 6 && grid == BasicGridNavigation.one) setupMain();
        else if (row == 6 && (grid == BasicGridNavigation.three || grid == BasicGridNavigation.six)) setupImageButton(1);
        else if (row == 7) setupImageButton(2);
        else if (row == 8) setupImageButton(3);
        else if (row == 9) setupImageButton(4);
        else if (row == 10) setupImageButton(5);
        else if (row == 11) setupImageButton(6);
    }

    int getIntFromView(int viewId) {
        if (viewId == R.id.main || viewId == R.id.imageButton1) return 0;
        if (viewId == R.id.imageButton2) return 1;
        if (viewId == R.id.imageButton3) return 2;
        if (viewId == R.id.imageButton4) return 3;
        if (viewId == R.id.imageButton5) return 4;
        if (viewId == R.id.imageButton6) return 5;
        return 0;
    }
}
