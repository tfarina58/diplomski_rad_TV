package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.TextClock;

import androidx.core.content.ContextCompat;

public class ClockHeaderButton {
    public static void setupClockButton(Context ctx, TextClock textClock, View focusedView, Clock format) {
        if (textClock == null) return;

        if (format == Clock.h12) textClock.setFormat12Hour("hh:mm:ss a");
        else textClock.setFormat12Hour("HH:mm:ss");

        if (focusedView.getId() == R.id.textClock) textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.highlighted_header_button));
        else textClock.setBackground(ContextCompat.getDrawable(ctx, R.drawable.header_button));
    }
}
