package com.example.diplomski_rad_tv;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

public class ProgressBarLoader {

    public static void manageProgressBar(Context ctx, ProgressBar progressBar, Theme theme, boolean loadingInProgress) {
        if (progressBar == null) return;

        if (loadingInProgress) {
            if (theme == Theme.dark) {
                int color = ContextCompat.getColor(ctx, R.color.loader_color_dark_mode);
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
            } else if (theme == Theme.light) {
                int color = ContextCompat.getColor(ctx, R.color.loader_color_light_mode);
                progressBar.setProgressTintList(ColorStateList.valueOf(color));
                progressBar.setIndeterminateTintList(ColorStateList.valueOf(color));
            }
        } else progressBar.setVisibility(View.GONE);
    }

}