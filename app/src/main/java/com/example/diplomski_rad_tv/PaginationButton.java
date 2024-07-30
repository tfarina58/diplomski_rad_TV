package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class PaginationButton {
    public static void setupPaginationButton(Context ctx, Button paginationButton, EditText paginationCurrentPage, TextView paginationSlash, TextView paginationTotalPages, View focusedView, Theme theme, int listToShowSize, int currentPage, int totalPages) {
        if (paginationButton == null || paginationCurrentPage == null || paginationSlash == null || paginationTotalPages == null) return;

        if (listToShowSize != 0) paginationCurrentPage.setText(Integer.toString(currentPage + 1));
        else paginationCurrentPage.setText("0");
        paginationTotalPages.setText(Integer.toString(totalPages));

        if (focusedView.getId() == R.id.pagination) {
            if (theme == Theme.light) paginationButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else paginationButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        }
        else {
            if (theme == Theme.light) paginationButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else paginationButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) {
            paginationCurrentPage.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            paginationSlash.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
            paginationTotalPages.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        } else {
            paginationCurrentPage.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            paginationSlash.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
            paginationTotalPages.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
        }
    }
}
