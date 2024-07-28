package com.example.diplomski_rad_tv;

public class RatingListNavigation {
    static int[][] navigationRatingList = {
            {0, 0, 0,                   R.id.languageButton}, // ratingButton
            {0, 0, R.id.ratingButton,   R.id.themeButton},    // languageButton
            {0, 0, R.id.languageButton, R.id.textClock},      // themeButton
            {0, 0, R.id.themeButton,    0},                   // textClock
            /*{R.id.languageButton, R.id.ratingButton2,  0,                   0},                 // ratingButton1
            {R.id.ratingButton1,  R.id.ratingButton3,  0,                   0},                 // ratingButton2
            {R.id.ratingButton2,  R.id.ratingButton4,  0,                   0},                 // ratingButton3
            {R.id.ratingButton3,  R.id.ratingButton5,  0,                   0},                 // ratingButton4
            {R.id.ratingButton4,  R.id.ratingButton6,  0,                   0},                 // ratingButton5
            {R.id.ratingButton5,  R.id.ratingButton7,  0,                   0},                 // ratingButton6
            {R.id.ratingButton6,  R.id.ratingButton8,  0,                   0},                 // ratingButton7
            {R.id.ratingButton7,  R.id.ratingButton9,  0,                   0},                 // ratingButton8
            {R.id.ratingButton8,  R.id.ratingButton10, 0,                   0},                 // ratingButton9
            {R.id.ratingButton9,  0,                   0,                   0},                 // ratingButton10*/
    };
    public static int navigateOverActivity(int currentViewId, int direction) {
        return navigationRatingList[getRowWithId(currentViewId)][direction];
    }

    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        /*if (currentViewId == R.id.ratingButton1) return 4;
        if (currentViewId == R.id.ratingButton2) return 5;
        if (currentViewId == R.id.ratingButton3) return 6;
        if (currentViewId == R.id.ratingButton4) return 7;
        if (currentViewId == R.id.ratingButton5) return 8;
        if (currentViewId == R.id.ratingButton6) return 9;
        if (currentViewId == R.id.ratingButton7) return 10;
        if (currentViewId == R.id.ratingButton8) return 11;
        if (currentViewId == R.id.ratingButton9) return 12;
        if (currentViewId == R.id.ratingButton10) return 13;*/
        return -1;
    }
}
