package com.example.diplomski_rad_tv;

import android.content.SharedPreferences;

public enum Clock {
    h24, h12;
    private static final Clock[] values = values();
    public Clock next() {
        return values[(this.ordinal() + 1) % values.length];
    }
    public Clock getClockFromSharedPreferences(SharedPreferences sharedPreferences) {
        String clockCode = sharedPreferences.getString("clock", "");

        if (clockCode.equals("h12")) return Clock.h12;
        return Clock.h24;
    }

    public void setClockOnSharedPreferences(SharedPreferences sharedPreferences, Clock clock) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (clock == Clock.h12) editor.putString("clock", "h12");
        else editor.putString("clock", "h24");

        editor.apply();
    }
}
