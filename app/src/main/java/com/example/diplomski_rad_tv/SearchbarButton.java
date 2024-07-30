package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class SearchbarButton {
    public static void setupSearchBarButton(Context ctx, SearchView searchbarButton, String searchbarText, View focusedView, Language language, Theme theme) {
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

        if (focusedView.getId() == R.id.searchView) {
            if (theme == Theme.light) searchbarButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else searchbarButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        }
        else {
            if (theme == Theme.light) searchbarButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else searchbarButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        int id = searchbarButton.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchbarButton.findViewById(id);
        if (theme == Theme.light) {
            textView.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            textView.setHintTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        } else {
            textView.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            textView.setHintTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
        }
    }
}
