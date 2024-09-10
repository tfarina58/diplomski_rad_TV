package com.example.diplomski_rad_tv;

import android.content.SharedPreferences;

import com.google.firebase.firestore.GeoPoint;

public class SharedPreferencesService {
    SharedPreferences sharedPreferences;
    public SharedPreferencesService(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getUserId() {
        return sharedPreferences.getString("userId", "");
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (userId == null) return;

        editor.putString("userId", userId);
        editor.apply();
    }

    public String getEstateId() {
        return sharedPreferences.getString("estateId", "");
    }

    public void setEstateId(String estateId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (estateId == null) return;

        editor.putString("estateId", estateId);
        editor.apply();
    }

    public String getCategoryId() {
        return sharedPreferences.getString("categoryId", "");
    }

    public void setCategoryId(String categoryId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (categoryId == null) return;

        editor.putString("categoryId", categoryId);
        editor.apply();
    }

    public String getElementId() {
        return sharedPreferences.getString("elementId", "");
    }

    public void setElementId(String elementId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (elementId == null) return;

        editor.putString("elementId", elementId);
        editor.apply();
    }

    public Language getLanguage() {
        String languageCode = sharedPreferences.getString("language", "");

        switch (languageCode) {
            case "de":
                return Language.german;
            case "hr":
                return Language.croatian;
            default:
                return Language.english;
        }
    }

    public void setLanguage(Language language) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (language) {
            case german:
                editor.putString("language", "de");
                break;
            case croatian:
                editor.putString("language", "hr");
                break;
            default:
                editor.putString("language", "en");
                break;
        }

        editor.apply();
    }
    public Theme getTheme() {
        String theme = sharedPreferences.getString("theme", "");

        if (theme.equals("light")) return Theme.light;
        return Theme.dark;
    }

    public void setTheme(Theme theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (theme == Theme.light) editor.putString("theme", "light");
        else editor.putString("theme", "dark");

        editor.apply();
    }

    public GridNavigation getGrid() {
        String grid = sharedPreferences.getString("grid", "");

        if (grid.equals("one")) return GridNavigation.one;
        else if (grid.equals("three")) return GridNavigation.three;
        return GridNavigation.six;
    }

    public void setGrid(GridNavigation grid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (grid == GridNavigation.one) editor.putString("grid", "one");
        else if (grid == GridNavigation.three) editor.putString("grid", "three");
        else editor.putString("grid", "six");

        editor.apply();
    }

    public Clock getClockFormat() {
        String clock = sharedPreferences.getString("clock", "");

        if (clock.equals("h12")) return Clock.h12;
        return Clock.h24;
    }

    public void setClockFormat(Clock clock) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (clock == Clock.h12) editor.putString("clock", "h12");
        else editor.putString("clock", "h24");

        editor.apply();
    }

    public String getRatingId() {
        return sharedPreferences.getString("ratingId", "");
    }

    public void setRatingId(String ratingId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (ratingId == null) return;

        editor.putString("ratingId", ratingId);
        editor.apply();
    }

    public String getGuestId() {
        return sharedPreferences.getString("guestId", "");
    }

    public void setGuestId(String ratingId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (ratingId == null) return;

        editor.putString("guestId", ratingId);
        editor.apply();
    }

    public GeoPoint getEstateCoordinates() {
        double latitude = Double.valueOf(sharedPreferences.getString("latitude", ""));
        double longitude = Double.valueOf(sharedPreferences.getString("longitude", ""));

        return new GeoPoint(latitude, longitude);
    }

    public void setEstateCoordinates(double latitude, double longitude) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("latitude", Double.toString(latitude));
        editor.putString("longitude", Double.toString(longitude));
        editor.apply();
    }

    public String getTemperatureUnit() {
        return sharedPreferences.getString("unit", "C");
    }

    public void setTemperatureUnit(String unit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (unit == null) return;

        editor.putString("unit", unit);
        editor.apply();
    }

    public void clearUserAndEstateInfo() {
        setUserId("");
        setEstateId("");
    }

    public void clearAllInfo() {
        setUserId("");
        setEstateId("");
        setCategoryId("");
        setElementId("");
        setLanguage(Language.english);
        setTheme(Theme.dark);
        setGrid(GridNavigation.six);
        setClockFormat(Clock.h24);
        setGuestId("");
        setEstateCoordinates(0, 0);
        setRatingId("");
        setTemperatureUnit("C");
    }
}
