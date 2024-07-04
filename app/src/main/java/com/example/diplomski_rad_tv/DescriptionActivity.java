package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.text.SpannableString;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

public class DescriptionActivity extends Activity {
    SharedPreferencesService sharedPreferencesService;
    Element element;
    Language language;
    Theme theme;
    Clock format;
    View focusedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);

        this.sharedPreferencesService = new SharedPreferencesService(getSharedPreferences("MyPreferences", MODE_PRIVATE));
        this.language = this.sharedPreferencesService.getLanguage();
        this.theme = this.sharedPreferencesService.getTheme();
        this.format = this.sharedPreferencesService.getClockFormat();

        Bundle bundle = getIntent().getExtras();
        String jsonElement = bundle.getString("element");

        Gson gson = new Gson();
        Element element = gson.fromJson(jsonElement, Element.class);

        this.element = element;

        {
            TextView title = findViewById(R.id.descriptionTitle);

            this.setupTitle(getApplicationContext(), title, this.element.title, this.language, this.theme);
        }

        {
            ConstraintLayout background = findViewById(R.id.background);
            setupBackground(getApplicationContext(), background, theme);
        }

        {
            TextView description = findViewById(R.id.descriptionContent);
            this.focusedView = description;

            this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.theme);

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Change amount of lines displayed on descriptionText
                    if (description.getMaxLines() == 16) description.setMaxLines(32);
                    else if (description.getMaxLines() == 32) description.setMaxLines(16);
                }
            });
        }

        {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language);

            languageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    language = language.next();
                    sharedPreferencesService.setLanguage(language);

                    updateView(0);
                    updateView(1);
                    updateView(2);
                }
            });
        }

        {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);

            themeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    theme = theme.next();
                    sharedPreferencesService.setTheme(theme);

                    updateView(1);
                    updateView(3);

                    ConstraintLayout background = findViewById(R.id.background);
                    setupBackground(getApplicationContext(), background, theme);

                    TextView titleDescription = findViewById(R.id.descriptionTitle);
                    setupTitle(getApplicationContext(), titleDescription, element.title, language, theme);

                    setupLinks();
                }
            });
        }

        {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);

            textClock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    format = format.next();
                    sharedPreferencesService.setClockFormat(format);

                    switch (format) {
                        case h24:
                            ((TextClock)focusedView).setFormat12Hour("HH:mm:ss");
                            break;
                        case h12:
                            ((TextClock)focusedView).setFormat12Hour("hh:mm:ss a");
                            break;
                    }
                }
            });
        }

        this.setupImages();
        this.setupLinks();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        int oldFocusedViewId = focusedView.getId();

        // Up, down, left, right navigation button
        if (keyCode >= 19 && keyCode <= 22) {
            int newFocusedViewId = DescriptionNavigation.navigateOverActivity(oldFocusedViewId, keyCode - 19);
            while (!checkViewExistence(newFocusedViewId) && newFocusedViewId != 0)
                newFocusedViewId = DescriptionNavigation.navigateOverActivity(newFocusedViewId, keyCode - 19);

            // If == 0, focusedView will stay the same
            if (newFocusedViewId == 0) return false;

            // If != 0, focusedView will change its value
            this.focusedView = findViewById(newFocusedViewId);
            this.focusedView.requestFocus();

            // Remove focus from old View
            int row = DescriptionNavigation.getRowWithId(oldFocusedViewId);
            updateView(row);

            // Add focus to new View
            row = DescriptionNavigation.getRowWithId(newFocusedViewId);
            updateView(row);

            CustomScrollView scrollView = findViewById(R.id.scrollView);

            this.scrollToCenterView(scrollView, this.focusedView);
        } else if (keyCode == 4) {
            startActivity(new Intent(getApplicationContext(), ElementListActivity.class));
        }
        return true;
    }

    boolean checkViewExistence(int focusedViewId) {
        if (focusedViewId == R.id.languageButton) return true;
        if (focusedViewId == R.id.themeButton) return true;
        if (focusedViewId == R.id.textClock) return true;
        if (focusedViewId == R.id.descriptionContent) return this.element.description != null && !this.element.description.isEmpty();
        if (focusedViewId == R.id.descriptionImage1) return this.element.images.size() > 0 && !this.element.images.get(0).isEmpty();
        if (focusedViewId == R.id.descriptionImage2) return this.element.images.size() > 1 && !this.element.images.get(1).isEmpty();
        if (focusedViewId == R.id.descriptionImage3) return this.element.images.size() > 2 && !this.element.images.get(2).isEmpty();
        if (focusedViewId == R.id.descriptionImage4) return this.element.images.size() > 3 && !this.element.images.get(3).isEmpty();
        if (focusedViewId == R.id.descriptionImage5) return this.element.images.size() > 4 && !this.element.images.get(4).isEmpty();
        if (focusedViewId == R.id.descriptionImage6) return this.element.images.size() > 5 && !this.element.images.get(5).isEmpty();
        if (focusedViewId == R.id.descriptionImage7) return this.element.images.size() > 6 && !this.element.images.get(6).isEmpty();
        if (focusedViewId == R.id.descriptionImage8) return this.element.images.size() > 7 && !this.element.images.get(7).isEmpty();
        if (focusedViewId == R.id.descriptionImage9) return this.element.images.size() > 8 && !this.element.images.get(8).isEmpty();
        if (focusedViewId == R.id.descriptionLink1) return this.element.links.size() > 0 && !this.element.links.get(0).get("url").isEmpty() && !this.element.links.get(0).get("title").isEmpty();
        if (focusedViewId == R.id.descriptionLink2) return this.element.links.size() > 1 && !this.element.links.get(1).get("url").isEmpty() && !this.element.links.get(1).get("title").isEmpty();
        if (focusedViewId == R.id.descriptionLink3) return this.element.links.size() > 2 && !this.element.links.get(2).get("url").isEmpty() && !this.element.links.get(2).get("title").isEmpty();
        return false;
    }

    void setupBackground(Context ctx, ConstraintLayout background, Theme theme) {
        if (background == null) return;

        if (theme == Theme.light) background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
        else background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
    }

    void setupTitle(Context ctx, TextView titleText, String title, Language language, Theme theme) {
        if (titleText == null) return;

        if (!title.isEmpty()) titleText.setText(title);
        else {
            switch(language) {
                case german:
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_de));
                    break;
                case croatian:
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_hr));
                    break;
                default:
                    titleText.setText(ContextCompat.getString(ctx, R.string.element_name_en));
            }
        }

        if (theme == Theme.light) titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else titleText.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    void setupDescription(Context ctx, TextView descriptionView, String description, View focusedView, Theme theme) {
        if (descriptionView == null) return;

        if (!description.isEmpty()) descriptionView.setText(description);
        else {
            descriptionView.setText("");
            descriptionView.setVisibility(View.INVISIBLE);
        }

        if (focusedView.getId() == descriptionView.getId()) {
           if (theme == Theme.light) {
               descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_light));
               descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
           } else {
               descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.main_border_dark));
               descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
           }
        } else {
            descriptionView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));
            if (theme == Theme.light) descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else descriptionView.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }

    void setupImages() {
        int imagesLength = this.element.images.size();
        ImageView image;

        image = findViewById(R.id.descriptionImage1);
        if (0 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "0", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage2);
        if (1 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(1), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage3);
        if (2 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(2), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage4);
        if (3 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(3), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage5);
        if (4 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(4), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage6);
        if (5 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(5), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "5", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage7);
        if (6 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(6), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "6", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage8);
        if (7 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(7), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "7", Toast.LENGTH_LONG).show();
            }
        });

        image = findViewById(R.id.descriptionImage9);
        if (8 < imagesLength) this.setupImage(getApplicationContext(), image, this.element.images.get(8), this.focusedView, this.theme);
        else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "8", Toast.LENGTH_LONG).show();
            }
        });
    }

    void setupImage(Context ctx, ImageView image, String imageUrl, View focusedView, Theme theme) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            image.setVisibility(View.GONE);
            return;
        }

        if (focusedView.getId() == image.getId()) {
            if (theme == Theme.light) image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
            else image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
        } else image.setBackground(ContextCompat.getDrawable(ctx, R.drawable.image_button));

        try {
            Picasso.get()
                    .load(imageUrl)
                    .fit()
                    .into(image);
        } catch (Exception e) {
            e.printStackTrace();
            image.setVisibility(View.GONE);
        }
    }

    void setupLinks() {
        int linksLength = this.element.links.size();
        Button button;

        button = findViewById(R.id.descriptionLink1);
        if (0 < linksLength) this.setupLink(getApplicationContext(), button, this.element.links.get(0).get("title"), this.element.links.get(0).get("url"), this.focusedView, this.theme);
        else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 0; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        button = findViewById(R.id.descriptionLink2);
        if (1 < linksLength) this.setupLink(getApplicationContext(), button, this.element.links.get(1).get("title"), this.element.links.get(1).get("url"), this.focusedView, this.theme);
        else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 1; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });

        button = findViewById(R.id.descriptionLink3);
        if (2 < linksLength) this.setupLink(getApplicationContext(), button, this.element.links.get(2).get("title"), this.element.links.get(2).get("url"), this.focusedView, this.theme);
        else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int linkIndex = 2; // getLinkIndexByViewId(focusedView.getId());
                // if (linkIndex == -1) return;

                if (linkIndex >= element.links.size()) return;

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.links.get(linkIndex).get("url")));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }

    void setupLink(Context ctx, Button button, String linkTitle, String linkUrl, View focusedView, Theme theme) {
        if (linkTitle == null || linkTitle.isEmpty() || linkUrl == null || linkUrl.isEmpty()) {
            button.setVisibility(View.GONE);
            return;
        }

        if (focusedView.getId() == button.getId()) {
            if (theme == Theme.light) {
                button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_light));
                button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            } else {
                button.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_image_button_dark));
                button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            }
        } else {
            button.setBackground(ContextCompat.getDrawable(ctx, R.color.transparent));
            if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            else button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }

        SpannableString content = new SpannableString(linkTitle);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        button.setText(content);
        if (theme == Theme.light) button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        else button.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
    }

    void updateView(int row) {
        if (row == 0) {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language);
        }
        else if (row == 1) {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
        }
        else if (row == 2) {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);
        }
        else if (row == 3) {
            TextView description = findViewById(R.id.descriptionContent);

            this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.theme);
        }
        else if (row == 4) {
            ImageView image = findViewById(R.id.descriptionImage1);

            if (0 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(0), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 5) {
            ImageView image = findViewById(R.id.descriptionImage2);

            if (1 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(1), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 6) {
            ImageView image = findViewById(R.id.descriptionImage3);

            if (2 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(2), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 7) {
            ImageView image = findViewById(R.id.descriptionImage4);

            if (3 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(3), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 8) {
            ImageView image = findViewById(R.id.descriptionImage5);

            if (4 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(4), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 9) {
            ImageView image = findViewById(R.id.descriptionImage6);

            if (5 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(5), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 10) {
            ImageView image = findViewById(R.id.descriptionImage7);

            if (6 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(6), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 11) {
            ImageView image = findViewById(R.id.descriptionImage8);

            if (7 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(7), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 12) {
            ImageView image = findViewById(R.id.descriptionImage9);

            if (8 < this.element.images.size()) this.setupImage(getApplicationContext(), image, this.element.images.get(8), this.focusedView, this.theme);
            else this.setupImage(getApplicationContext(), image, "", this.focusedView, this.theme);
        }
        else if (row == 13) {
            Button button = findViewById(R.id.descriptionLink1);

            if (0 < this.element.links.size()) this.setupLink(getApplicationContext(), button, this.element.links.get(0).get("title"), this.element.links.get(0).get("url"), this.focusedView, this.theme);
            else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);
        }
        else if (row == 14) {
            Button button = findViewById(R.id.descriptionLink2);

            if (1 < this.element.links.size()) this.setupLink(getApplicationContext(), button, this.element.links.get(1).get("title"), this.element.links.get(1).get("url"), this.focusedView, this.theme);
            else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);
        }
        else if (row == 15) {
            Button button = findViewById(R.id.descriptionLink3);

            if (2 < this.element.links.size()) this.setupLink(getApplicationContext(), button, this.element.links.get(2).get("title"), this.element.links.get(2).get("url"), this.focusedView, this.theme);
            else this.setupLink(getApplicationContext(), button, "", "", this.focusedView, this.theme);
        }
    }

    void setNewContentView() {
        {
            TextView titleDescription = findViewById(R.id.descriptionTitle);

            this.setupTitle(getApplicationContext(), titleDescription, this.element.title, this.language, this.theme);
        }

        {
            TextView description = findViewById(R.id.descriptionContent);

            this.setupDescription(getApplicationContext(), description, this.element.description, this.focusedView, this.theme);
        }

        {
            Button languageButton = findViewById(R.id.languageButton);
            ImageView languageIcon = findViewById(R.id.languageIcon);

            LanguageHeaderButton.setupLanguageButton(getApplicationContext(), languageButton, languageIcon, this.focusedView, this.language);
        }

        {
            Button themeButton = findViewById(R.id.themeButton);
            ImageView themeIcon = findViewById(R.id.themeIcon);

            ThemeHeaderButton.setupThemeButton(getApplicationContext(), themeButton, themeIcon, this.focusedView, this.language, this.theme);
        }

        {
            TextClock textClock = findViewById(R.id.textClock);

            ClockHeaderButton.setupClockButton(getApplicationContext(), textClock, this.focusedView, this.format);
        }

        this.setupImages();
        this.setupLinks();
    }

    void scrollToCenterView(CustomScrollView scrollView, View focusedView) {
        if (scrollView == null || focusedView == null) return;

        int scrollViewHeight = scrollView.getHeight();
        int viewTop = focusedView.getTop();
        int viewHeight = focusedView.getHeight();
        int scrollToY = viewTop - (scrollViewHeight / 2) + (viewHeight / 2);

        scrollView.smoothScrollTo(0, scrollToY);
    }
}
