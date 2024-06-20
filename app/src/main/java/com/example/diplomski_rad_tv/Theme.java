package com.example.diplomski_rad_tv;

import android.content.SharedPreferences;

import java.util.Locale;

public enum Theme {
    light, dark;
    private static final Theme[] values = values();
    public Theme next() {
        return values[(this.ordinal() + 1) % values.length];
    }

    public Theme getThemeFromSharedPreferences(SharedPreferences sharedPreferences) {
        String themeCode = sharedPreferences.getString("theme", "");

        if (themeCode.equals("light")) return Theme.light;
        return Theme.dark;
    }

    public void setThemeOnSharedPreferences(SharedPreferences sharedPreferences, Theme theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (theme == Theme.light) editor.putString("theme", "light");
        else editor.putString("theme", "dark");

        editor.apply();
    }
}
