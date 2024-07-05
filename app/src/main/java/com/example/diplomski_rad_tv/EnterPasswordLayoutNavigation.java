package com.example.diplomski_rad_tv;

public class EnterPasswordLayoutNavigation {
    private static final int[][] navigationDescription = {
            {0,             R.id.cancelButton, 0,                 0},                 // password
            {R.id.password, 0,                 0,                 R.id.submitButton}, // cancelButton
            {R.id.password, 0,                 R.id.cancelButton, 0},                 // submitButton
    };
    public static int navigateOverActivity(int oldFocusedViewId, int direction) {
        return navigationDescription[getRowWithId(oldFocusedViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.password) return 0;
        if (currentViewId == R.id.cancelButton) return 1;
        if (currentViewId == R.id.submitButton) return 2;
        return -1;
    }
}
