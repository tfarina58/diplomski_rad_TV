package com.example.diplomski_rad_tv;

public class RatingListNavigation {
    static int[][] navigationRatingList = {
            {0, 0, 0,                   R.id.languageButton}, // ratingButton
            {0, 0, R.id.ratingButton,   R.id.themeButton},    // languageButton
            {0, 0, R.id.languageButton, R.id.textClock},      // themeButton
            {0, 0, R.id.themeButton,    R.id.weatherButton},  // textClock
            {0, 0, R.id.textClock,      0},                   // weatherButton
    };

    static int[][] navigationRatingListWithoutWeather = {
            {0, 0, 0,                   R.id.languageButton}, // ratingButton
            {0, 0, R.id.ratingButton,   R.id.themeButton},    // languageButton
            {0, 0, R.id.languageButton, R.id.textClock},      // themeButton
            {0, 0, R.id.themeButton,    0},                   // textClock
    };
    public static int navigateOverActivity(boolean hasWeatherButton, int currentViewId, int direction) {
        if (!hasWeatherButton) return navigationRatingListWithoutWeather[getRowWithId(currentViewId)][direction];
        else return navigationRatingList[getRowWithId(currentViewId)][direction];
    }

    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.weatherButton) return 4;
        return -1;
    }
}
