package com.example.diplomski_rad_tv;

public class LoginNavigation {
    private static int[][] navigationLogin = {
            {0,                R.id.password,    0, 0},  // emailAddress
            {R.id.password,    R.id.loginButton, 0, 0},  // password
            {R.id.loginButton, 0,                0, 0},  // loginButton
    };
    public static int navigateOverActivity(int currentViewId, int direction) {
        return navigationLogin[getRowWithId(currentViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.emailAddress) return 0;
        if (currentViewId == R.id.password) return 1;
        if (currentViewId == R.id.loginButton) return 2;
        return -1;
    }
}
