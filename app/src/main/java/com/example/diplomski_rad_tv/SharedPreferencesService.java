package com.example.diplomski_rad_tv;

import android.content.SharedPreferences;

import java.util.Locale;

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

        editor.putString(userId, "");
        editor.apply();
    }

    public String getEstateId() {
        return sharedPreferences.getString("estateId", "");
    }

    public void setEstateId(String estateId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (estateId == null) return;

        editor.putString(estateId, "");
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
}
