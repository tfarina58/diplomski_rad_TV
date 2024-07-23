package com.example.diplomski_rad_tv;

public class ChooseRatingLayoutNavigation {
    private static final int[][] navigationChooseRating = {
        {0, 0, 0,                         R.id.cancelButtonRating}, // showRatingsButton
        {0, 0, R.id.showRatingsButton,    R.id.submitRatingButton}, // cancelButtonRating
        {0, 0, R.id.cancelButtonRating, 0},                       // submitRatingButton
    };
    public static int navigateOverLayout(int oldFocusedViewId, int direction) {
        return navigationChooseRating[getRowWithId(oldFocusedViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.showRatingsButton) return 0;
        if (currentViewId == R.id.cancelButtonRating) return 1;
        if (currentViewId == R.id.submitRatingButton) return 2;
        return -1;
    }
}
