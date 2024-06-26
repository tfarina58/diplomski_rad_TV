package com.example.diplomski_rad_tv;

import android.view.View;

public enum Grid {
    one, three, six;
    private static final Grid[] values = values();
    public Grid next() {
        return values[(this.ordinal() + 1) % values.length];
    }
    private static int[][] navigationBasic1 = {
        {0,                   R.id.main,       0,                   R.id.themeButton}, // languageButton
        {0,                   R.id.main,       R.id.languageButton, R.id.gridButton},  // themeButton
        {0,                   R.id.main,       R.id.themeButton,    R.id.textClock},   // gridButton
        {0,                   R.id.main,       R.id.gridButton,     0},                // textClock
        {R.id.main,           0,               0,                   R.id.pageIndex},   // searchView
        {R.id.main,           0,               R.id.searchView,     0},                // pageIndex
        {R.id.languageButton, R.id.searchView, 0,                   0}                 // main
    };
    private static int[][] navigationBasic3 = {
        {0,                   R.id.imageButton1, 0,                   R.id.themeButton},  // languageButton
        {0,                   R.id.imageButton1, R.id.languageButton, R.id.gridButton},   // themeButton
        {0,                   R.id.imageButton1, R.id.themeButton,    R.id.textClock},    // gridButton
        {0,                   R.id.imageButton1, R.id.gridButton,     0},                 // textClock
        {R.id.imageButton1,   0,                 0,                   R.id.pageIndex},    // searchView
        {R.id.imageButton1,   0,                 R.id.searchView,     0},                 // pageIndex
        {R.id.languageButton, R.id.searchView,   R.id.imageButton3,   R.id.imageButton2}, // imageButton1
        {R.id.languageButton, R.id.searchView,   R.id.imageButton1,   R.id.imageButton3}, // imageButton2
        {R.id.languageButton, R.id.searchView,   R.id.imageButton2,   R.id.imageButton1}  // imageButton3
    };
    private static int[][] navigationBasic6 = {
        {0,                   R.id.imageButton1, 0,                   R.id.themeButton},  // languageButton
        {0,                   R.id.imageButton1, R.id.languageButton, R.id.gridButton},   // themeButton
        {0,                   R.id.imageButton1, R.id.themeButton,    R.id.textClock},    // gridButton
        {0,                   R.id.imageButton1, R.id.gridButton,     0},                 // textClock
        {R.id.imageButton1,   0,                 0,                   R.id.pageIndex},    // searchView
        {R.id.imageButton1,   0,                 R.id.searchView,     0},                 // pageIndex
        {R.id.languageButton, R.id.imageButton4, R.id.imageButton3,   R.id.imageButton2}, // imageButton1
        {R.id.languageButton, R.id.imageButton5, R.id.imageButton1,   R.id.imageButton3}, // imageButton2
        {R.id.languageButton, R.id.imageButton6, R.id.imageButton2,   R.id.imageButton1}, // imageButton3
        {R.id.imageButton1,   R.id.searchView,   R.id.imageButton6,   R.id.imageButton5}, // imageButton4
        {R.id.imageButton2,   R.id.searchView,   R.id.imageButton4,   R.id.imageButton6}, // imageButton5
        {R.id.imageButton3,   R.id.searchView,   R.id.imageButton5,   R.id.imageButton4}  // imageButton6
    };
    public static int navigateOverActivity(Grid currentGrid, int currentViewId, int direction) {
        if (currentGrid == BasicGridNavigation.one) {
            return navigationBasic1[getRowWithId(currentViewId)][direction];
        } else if (currentGrid == BasicGridNavigation.three) {
            return navigationBasic3[getRowWithId(currentViewId)][direction];
        } else if (currentGrid == BasicGridNavigation.six) {
            return navigationBasic6[getRowWithId(currentViewId)][direction];
        }
        return 0;
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.languageButton) return 0;
        if (currentViewId == R.id.themeButton) return 1;
        if (currentViewId == R.id.gridButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.searchView) return 4;
        if (currentViewId == R.id.pageIndex) return 5;
        if (currentViewId == R.id.main || currentViewId == R.id.imageButton1) return 6;
        if (currentViewId == R.id.imageButton2) return 7;
        if (currentViewId == R.id.imageButton3) return 8;
        if (currentViewId == R.id.imageButton4) return 9;
        if (currentViewId == R.id.imageButton5) return 10;
        if (currentViewId == R.id.imageButton6) return 11;
        return -1;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.gridButton || viewId == R.id.textClock;
    }

    public static boolean isLowerButtons(int viewId) {
        return viewId == R.id.searchView || viewId == R.id.pageIndex;
    }

    public static boolean isFirstRow(int viewId) {
        return viewId == R.id.imageButton1 || viewId == R.id.imageButton2 || viewId == R.id.imageButton3;
    }

    public static boolean isSecondRow(int viewId) {
        return viewId == R.id.imageButton4 || viewId == R.id.imageButton5 || viewId == R.id.imageButton6;
    }

    public static int getGridTypeAsInt(Grid grid) {
        if (grid == BasicGridNavigation.one) return 1;
        else if (grid == BasicGridNavigation.three) return 3;
        else return 6;
    }
}
