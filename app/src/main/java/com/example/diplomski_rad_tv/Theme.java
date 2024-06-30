package com.example.diplomski_rad_tv;

import android.content.SharedPreferences;

import java.util.Locale;

public enum Theme {
    light, dark;
    private static final Theme[] values = values();
    public Theme next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
