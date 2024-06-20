package com.example.diplomski_rad_tv;
public enum GridNavigation {
    one, three, six;
    private static final GridNavigation[] values = values();
    public GridNavigation next() {
        return values[(this.ordinal() + 1) % values.length];
    }
    private static final int[][] navigationGrid1 = {
        {0,                   R.id.main,       0,                   R.id.themeButton}, // languageButton
        {0,                   R.id.main,       R.id.languageButton, R.id.gridButton},  // themeButton
        {0,                   R.id.main,       R.id.themeButton,    R.id.textClock},   // gridButton
        {0,                   R.id.main,       R.id.gridButton,     0},                // textClock
        {R.id.main,           0,               0,                   R.id.pageIndex},   // searchView
        {R.id.main,           0,               R.id.searchView,     0},                // pageIndex
        {R.id.languageButton, R.id.searchView, R.id.main,           R.id.main}         // main
    };
    private static final int[][] navigationGrid3 = {
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
    private static final int[][] navigationGrid6 = {
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
    public static int navigateOverActivity(GridNavigation currentGrid, int currentViewId, int direction) {
        switch (currentGrid) {
            case one:
                return navigationGrid1[getRowWithId(currentViewId)][direction];
            case three:
                return navigationGrid3[getRowWithId(currentViewId)][direction];
            case six:
                return navigationGrid6[getRowWithId(currentViewId)][direction];
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

    public static boolean isLeftColumn(int viewId) {
        return viewId == R.id.imageButton1 || viewId == R.id.imageButton4;
    }

    public static boolean isMiddleColumn(int viewId) {
        return viewId == R.id.imageButton2 || viewId == R.id.imageButton5;
    }

    public static boolean isRightColumn(int viewId) {
        return viewId == R.id.imageButton3 || viewId == R.id.imageButton6;
    }

    public static int getGridTypeAsInt(GridNavigation grid) {
        switch (grid) {
            case one:
                return 1;
            case three:
                return 3;
            case six:
                return 6;
        }
        return 0;
    }
}
