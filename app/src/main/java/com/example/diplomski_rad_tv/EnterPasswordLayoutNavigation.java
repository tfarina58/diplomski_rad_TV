package com.example.diplomski_rad_tv;

public class EnterPasswordLayoutNavigation {
    private static final int[][] navigationEnterPassword = {
        {0,             R.id.cancelButtonPassword, 0,                         0},                 // password
        {R.id.password, 0,                         0,                         R.id.submitButton}, // cancelButtonPassword
        {R.id.password, 0,                         R.id.cancelButtonPassword, 0},                 // submitButton
    };
    public static int navigateOverLayout(int oldFocusedViewId, int direction) {
        return navigationEnterPassword[getRowWithId(oldFocusedViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.password) return 0;
        if (currentViewId == R.id.cancelButtonPassword) return 1;
        if (currentViewId == R.id.submitButton) return 2;
        return -1;
    }
}
