package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.TextClock;

import androidx.core.content.ContextCompat;

public class ClockHeaderButton {
    public static void setupClockButton(Context ctx, TextClock textClock, View focusedView, Clock format, Theme theme) {
        if (textClock == null) return;

        if (format == Clock.h12) textClock.setFormat12Hour("hh:mm:ss a");
        else textClock.setFormat12Hour("HH:mm:ss");

        if (focusedView.getId() == R.id.textClock) {
            if (theme == Theme.light) textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) textClock.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else textClock.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }
}
