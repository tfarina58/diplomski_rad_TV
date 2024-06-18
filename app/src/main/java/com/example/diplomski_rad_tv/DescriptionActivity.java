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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import android.text.SpannableString;

import androidx.core.content.ContextCompat;

public class DescriptionActivity extends Activity {
    Element element;
    Language language = Language.united_kingdom;
    Theme theme = Theme.dark;
    Clock format = Clock.h12;
    View focusedView;
    int overallImageIndex = 0;
    int overallLinkIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);

        Bundle bundle = getIntent().getExtras();
        String jsonElement = bundle.getString("element");

        Gson gson = new Gson();
        Element element = gson.fromJson(jsonElement, Element.class);

        this.element = element;

        this.setupTitle();
        this.setupDescription();
        this.setupLanguageButton();
        this.setupThemeButton();
        this.setupTextClock();
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
            if (newFocusedViewId == 0) return false;

            // Pressed up
            if (keyCode == 19) {
                if (oldFocusedViewId == R.id.link1) {
                    int imagesLength = element.images.size();

                    if (imagesLength >= 7) {
                        this.overallImageIndex = 6;
                    } else if (imagesLength >= 4) {
                        this.overallImageIndex = 3;
                        newFocusedViewId = R.id.image4;
                    } else if (imagesLength >= 1) {
                        this.overallImageIndex = 0;
                        newFocusedViewId = R.id.image1;
                    } else if (!element.description.isEmpty()) newFocusedViewId = R.id.descriptionText;
                    else newFocusedViewId = R.id.languageButton;
                } else if (DescriptionNavigation.isFirstRow(oldFocusedViewId)) {
                    if (!element.description.isEmpty()) {
                        // Do nothing
                    } else newFocusedViewId = R.id.languageButton;
                }
            }
            // Pressed down
            else if (keyCode == 20) {
                if (DescriptionNavigation.isUpperButtons(oldFocusedViewId)) {
                    if (!element.description.isEmpty()) {
                        // Do nothing
                    } else if (element.images.size() >= 1) {
                        this.overallImageIndex = 0;
                        newFocusedViewId = R.id.image1;
                    } else if (element.links.size() >= 1) {
                        this.overallLinkIndex = 0;
                        newFocusedViewId = R.id.link1;
                    } else newFocusedViewId = 0;
                } else if (oldFocusedViewId == R.id.descriptionText) {
                    if (element.images.size() >= 1) {
                        this.overallImageIndex = 0;
                    } else if (element.links.size() >= 1) {
                        this.overallLinkIndex = 0;
                        newFocusedViewId = R.id.link1;
                    } else newFocusedViewId = 0;
                } else if (DescriptionNavigation.isThirdRow(oldFocusedViewId)) {
                    if (element.links.size() >= 1) {
                        this.overallLinkIndex = 0;
                    } else newFocusedViewId = 0;
                }

            }
            // Pressed left
            else if (keyCode == 21) {
                if (DescriptionNavigation.isUpperButtons(oldFocusedViewId) || oldFocusedViewId == R.id.descriptionText || DescriptionNavigation.isLeftColumn(oldFocusedViewId) || DescriptionNavigation.isLinks(oldFocusedViewId)) {
                    // Do nothing
                } else if (DescriptionNavigation.isMiddleColumn(oldFocusedViewId) || DescriptionNavigation.isRightColumn(oldFocusedViewId)) {
                    this.overallImageIndex--;
                } else newFocusedViewId = 0;
            }
            // Pressed right
            else if (keyCode == 22) {
                if (DescriptionNavigation.isUpperButtons(oldFocusedViewId) || oldFocusedViewId == R.id.descriptionText || DescriptionNavigation.isRightColumn(oldFocusedViewId) || DescriptionNavigation.isLinks(oldFocusedViewId)) {
                    // Do nothing
                } else if (this.overallImageIndex + 1 < this.element.images.size() && (DescriptionNavigation.isLeftColumn(oldFocusedViewId) || DescriptionNavigation.isMiddleColumn(oldFocusedViewId))) {
                    this.overallImageIndex++;
                } else newFocusedViewId = 0;
            }

            if (newFocusedViewId != 0) {
                focusedView = findViewById(newFocusedViewId);

                // Remove focus from old View
                int row = DescriptionNavigation.getRowWithId(oldFocusedViewId);
                updateView(row);

                // Add focus to new View
                row = DescriptionNavigation.getRowWithId(newFocusedViewId);
                updateView(row);
            }

            this.focusedView = findViewById(newFocusedViewId);
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
            } else if (oldFocusedViewId == R.id.descriptionText) {
                TextView description = findViewById(oldFocusedViewId);
                if (description == null) return false;

                if (description.getMaxLines() == 16) description.setMaxLines(32);
                else if (description.getMaxLines() == 32)((TextView)findViewById(oldFocusedViewId)).setMaxLines(16);
            } else if (DescriptionNavigation.isImage(oldFocusedViewId)) {
                // Display image full-screen
            } else if (DescriptionNavigation.isLinks(oldFocusedViewId)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(element.links.get(this.overallLinkIndex).get("url")));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
        return true;
    }

    void setupTitle() {
        TextView title = findViewById(R.id.titleDescription);
        if (title == null) return;

        if (!this.element.title.isEmpty()) title.setText(this.element.title);
        else {
            switch(this.language) {
                case united_kingdom:
                    title.setText(getResources().getString(R.string.element_name_en));
                    break;
                case germany:
                    title.setText(getResources().getString(R.string.element_name_de));
                    break;
                case croatia:
                    title.setText(getResources().getString(R.string.element_name_hr));
                    break;
            }
        }
    }

    void setupDescription() {
        TextView description = findViewById(R.id.descriptionText);
        if (description == null) return;

        if (this.focusedView == null) {
            this.focusedView = description;
        }

        if (!this.element.description.isEmpty()) description.setText(this.element.description);
        else {
            description.setText("");
            description.setVisibility(View.INVISIBLE);
        }

        if (this.focusedView == description) {
           if (this.theme == Theme.dark) description.setBackground(getResources().getDrawable(R.drawable.main_border_dark));
           else if (this.theme == Theme.light) description.setBackground(getResources().getDrawable(R.drawable.main_border_light));
        } else description.setBackground(getResources().getDrawable(R.drawable.image_button));
    }

    void setupImages() {
        this.setupImage(0);
        this.setupImage(1);
        this.setupImage(2);
        this.setupImage(3);
        this.setupImage(4);
        this.setupImage(5);
        this.setupImage(6);
        this.setupImage(7);
        this.setupImage(8);
    }

    void setupImage(int index) {
        ImageView image = getImageByIndex(index);
        if (image == null) {
            image.setVisibility(View.GONE);
            return;
        }

        int imagesLength = this.element.images.size();

        if (this.focusedView.getId() == image.getId()) {
            if (this.theme == Theme.dark) image.setBackground(getResources().getDrawable(R.drawable.highlighted_image_button_dark));
            if (this.theme == Theme.light) image.setBackground(getResources().getDrawable(R.drawable.highlighted_image_button_light));
        } else image.setBackground(getResources().getDrawable(R.drawable.image_button));

        if (index < imagesLength && !this.element.images.get(index).isEmpty()) {
            try {
                Picasso.get()
                        .load(this.element.images.get(index))
                        .fit()
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
                image.setVisibility(View.GONE);
            }
        } else image.setVisibility(View.GONE);
    }

    void setupLinks() {
        this.setupLink(0);
        this.setupLink(1);
        this.setupLink(2);
    }

    void setupLink(int index) {
        Button link = getLinkByIndex(index);
        if (link == null) {
            link.setVisibility(View.GONE);
            return;
        }

        int linksLength = this.element.links.size();

        if (this.focusedView == link) {
            if (this.theme == Theme.dark) link.setBackground(getResources().getDrawable(R.drawable.highlighted_image_button_dark));
            if (this.theme == Theme.light) link.setBackground(getResources().getDrawable(R.drawable.highlighted_image_button_light));
        } else link.setBackground(getResources().getDrawable(R.color.transparent));

        if (index < linksLength && !this.element.links.get(index).get("title").isEmpty()) {
            SpannableString content = new SpannableString(this.element.links.get(index).get("title"));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

            link.setText(content);
            if (this.theme == Theme.dark) link.setTextColor(getResources().getColor(R.color.text_color_dark_mode));
            else if (this.theme == Theme.light) link.setTextColor(getResources().getColor(R.color.text_color_light_mode));

        } else link.setVisibility(View.GONE);
    }

    void updateView(int row) {
        if (row == 0) setupLanguageButton();
        else if (row == 1) setupThemeButton();
        else if (row == 2) setupTextClock();
        else if (row == 3) setupDescription();
        else if (row == 4) setupImage(0);
        else if (row == 5) setupImage(1);
        else if (row == 6) setupImage(2);
        else if (row == 7) setupImage(3);
        else if (row == 8) setupImage(4);
        else if (row == 9) setupImage(5);
        else if (row == 10) setupImage(6);
        else if (row == 11) setupImage(7);
        else if (row == 12) setupImage(8);
        else if (row == 13) setupLink(0);
        else if (row == 14) setupLink(1);
        else if (row == 15) setupLink(2);
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

    void setNewContentView() {
        this.setupTitle();
        this.setupDescription();
        this.setupLanguageButton();
        this.setupThemeButton();
        this.setupTextClock();
        this.setupImages();
        this.setupLinks();
    }

    ImageView getImageByIndex(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.image1);
            case 1:
                return findViewById(R.id.image2);
            case 2:
                return findViewById(R.id.image3);
            case 3:
                return findViewById(R.id.image4);
            case 4:
                return findViewById(R.id.image5);
            case 5:
                return findViewById(R.id.image6);
            case 6:
                return findViewById(R.id.image7);
            case 7:
                return findViewById(R.id.image8);
            case 8:
                return findViewById(R.id.image9);
        }
        return null;
    }

    Button getLinkByIndex(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.link1);
            case 1:
                return findViewById(R.id.link2);
            case 2:
                return findViewById(R.id.link3);
        }
        return null;
    }
}
