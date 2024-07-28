package com.example.diplomski_rad_tv;

public enum GridNavigation {
    one, three, six;
    private static final GridNavigation[] values = values();
    public GridNavigation next() {
        return values[(this.ordinal() + 1) % values.length];
    }
    private static final int[][] normalNavigationGrid1 = {
        {0,                    R.id.backgroundGrid1, 0,                    R.id.languageButton}, // ratingButton
        {0,                    R.id.backgroundGrid1, R.id.ratingButton,    R.id.themeButton},    // languageButton
        {0,                    R.id.backgroundGrid1, R.id.languageButton,  R.id.gridButton},     // themeButton
        {0,                    R.id.backgroundGrid1, R.id.themeButton,     R.id.textClock},      // gridButton
        {0,                    R.id.backgroundGrid1, R.id.gridButton,      0},                   // textClock
        {R.id.backgroundGrid1, 0,                    0,                    R.id.pagination},     // searchView
        {R.id.backgroundGrid1, 0,                    R.id.searchView,      0},                   // pagination
        {R.id.languageButton,  R.id.searchView,      R.id.backgroundGrid1, R.id.backgroundGrid1} // backgroundGrid1
    };
    private static final int[][] normalNavigationGrid3 = {
        {0,                   R.id.gridButton1, 0,                   R.id.languageButton}, // ratingButton
        {0,                   R.id.gridButton1, R.id.ratingButton,   R.id.themeButton},    // languageButton
        {0,                   R.id.gridButton1, R.id.languageButton, R.id.gridButton},     // themeButton
        {0,                   R.id.gridButton1, R.id.themeButton,    R.id.textClock},      // gridButton
        {0,                   R.id.gridButton1, R.id.gridButton,     0},                   // textClock
        {R.id.gridButton1,    0,                0,                   R.id.pagination},     // searchView
        {R.id.gridButton1,    0,                R.id.searchView,     0},                   // pagination
        {R.id.languageButton, R.id.searchView,  R.id.gridButton3,    R.id.gridButton2},    // gridButton1
        {R.id.languageButton, R.id.searchView,  R.id.gridButton1,    R.id.gridButton3},    // gridButton2
        {R.id.languageButton, R.id.searchView,  R.id.gridButton2,    R.id.gridButton1}     // gridButton3
    };
    private static final int[][] normalNavigationGrid6 = {
        {0,                   R.id.gridButton1, 0,                   R.id.languageButton}, // ratingButton
        {0,                   R.id.gridButton1, R.id.ratingButton,   R.id.themeButton},    // languageButton
        {0,                   R.id.gridButton1, R.id.languageButton, R.id.gridButton},     // themeButton
        {0,                   R.id.gridButton1, R.id.themeButton,    R.id.textClock},      // gridButton
        {0,                   R.id.gridButton1, R.id.gridButton,     0},                   // textClock
        {R.id.gridButton1,    0,                0,                   R.id.pagination},     // searchView
        {R.id.gridButton1,    0,                R.id.searchView,     0},                   // pagination
        {R.id.languageButton, R.id.gridButton4, R.id.gridButton3,    R.id.gridButton2},    // gridButton1
        {R.id.languageButton, R.id.gridButton5, R.id.gridButton1,    R.id.gridButton3},    // gridButton2
        {R.id.languageButton, R.id.gridButton6, R.id.gridButton2,    R.id.gridButton1},    // gridButton3
        {R.id.gridButton1,    R.id.searchView,  R.id.gridButton6,    R.id.gridButton5},    // gridButton4
        {R.id.gridButton2,    R.id.searchView,  R.id.gridButton4,    R.id.gridButton6},    // gridButton5
        {R.id.gridButton3,    R.id.searchView,  R.id.gridButton5,    R.id.gridButton4}     // gridButton6
    };

    private static final int[][] estateNavigationGrid1 = {
            {0,                    R.id.backgroundGrid1, 0,                    R.id.themeButton},    // languageButton
            {0,                    R.id.backgroundGrid1, R.id.languageButton,  R.id.gridButton},     // themeButton
            {0,                    R.id.backgroundGrid1, R.id.themeButton,     R.id.textClock},      // gridButton
            {0,                    R.id.backgroundGrid1, R.id.gridButton,      0},                   // textClock
            {R.id.backgroundGrid1, 0,                    0,                    R.id.pagination},     // searchView
            {R.id.backgroundGrid1, 0,                    R.id.searchView,      0},                   // pagination
            {R.id.languageButton,  R.id.searchView,      R.id.backgroundGrid1, R.id.backgroundGrid1} // backgroundGrid1
    };
    private static final int[][] estateNavigationGrid3 = {
            {0,                   R.id.gridButton1, 0,                   R.id.themeButton},    // languageButton
            {0,                   R.id.gridButton1, R.id.languageButton, R.id.gridButton},     // themeButton
            {0,                   R.id.gridButton1, R.id.themeButton,    R.id.textClock},      // gridButton
            {0,                   R.id.gridButton1, R.id.gridButton,     0},                   // textClock
            {R.id.gridButton1,    0,                0,                   R.id.pagination},     // searchView
            {R.id.gridButton1,    0,                R.id.searchView,     0},                   // pagination
            {R.id.languageButton, R.id.searchView,  R.id.gridButton3,    R.id.gridButton2},    // gridButton1
            {R.id.languageButton, R.id.searchView,  R.id.gridButton1,    R.id.gridButton3},    // gridButton2
            {R.id.languageButton, R.id.searchView,  R.id.gridButton2,    R.id.gridButton1}     // gridButton3
    };
    private static final int[][] estateNavigationGrid6 = {
            {0,                   R.id.gridButton1, 0,                   R.id.themeButton},    // languageButton
            {0,                   R.id.gridButton1, R.id.languageButton, R.id.gridButton},     // themeButton
            {0,                   R.id.gridButton1, R.id.themeButton,    R.id.textClock},      // gridButton
            {0,                   R.id.gridButton1, R.id.gridButton,     0},                   // textClock
            {R.id.gridButton1,    0,                0,                   R.id.pagination},     // searchView
            {R.id.gridButton1,    0,                R.id.searchView,     0},                   // pagination
            {R.id.languageButton, R.id.gridButton4, R.id.gridButton3,    R.id.gridButton2},    // gridButton1
            {R.id.languageButton, R.id.gridButton5, R.id.gridButton1,    R.id.gridButton3},    // gridButton2
            {R.id.languageButton, R.id.gridButton6, R.id.gridButton2,    R.id.gridButton1},    // gridButton3
            {R.id.gridButton1,    R.id.searchView,  R.id.gridButton6,    R.id.gridButton5},    // gridButton4
            {R.id.gridButton2,    R.id.searchView,  R.id.gridButton4,    R.id.gridButton6},    // gridButton5
            {R.id.gridButton3,    R.id.searchView,  R.id.gridButton5,    R.id.gridButton4}     // gridButton6
    };
    public static int navigateOverActivity(boolean isEstateActivity, GridNavigation currentGrid, int currentViewId, int direction) {
        if (!isEstateActivity) {
            switch (currentGrid) {
                case one:
                    return normalNavigationGrid1[getNormalRowWithId(currentViewId)][direction];
                case three:
                    return normalNavigationGrid3[getNormalRowWithId(currentViewId)][direction];
                case six:
                    return normalNavigationGrid6[getNormalRowWithId(currentViewId)][direction];
            }
        } else {
            switch (currentGrid) {
                case one:
                    return estateNavigationGrid1[getEstateRowWithId(currentViewId)][direction];
                case three:
                    return estateNavigationGrid3[getEstateRowWithId(currentViewId)][direction];
                case six:
                    return estateNavigationGrid6[getEstateRowWithId(currentViewId)][direction];
            }
        }
        return 0;
    }
    public static int getNormalRowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.gridButton) return 3;
        if (currentViewId == R.id.textClock) return 4;
        if (currentViewId == R.id.searchView) return 5;
        if (currentViewId == R.id.pagination) return 6;
        if (currentViewId == R.id.backgroundGrid1 || currentViewId == R.id.gridButton1) return 7;
        if (currentViewId == R.id.gridButton2) return 8;
        if (currentViewId == R.id.gridButton3) return 9;
        if (currentViewId == R.id.gridButton4) return 10;
        if (currentViewId == R.id.gridButton5) return 11;
        if (currentViewId == R.id.gridButton6) return 12;
        return -1;
    }

    public static int getEstateRowWithId(int currentViewId) {
        if (currentViewId == R.id.languageButton) return 0;
        if (currentViewId == R.id.themeButton) return 1;
        if (currentViewId == R.id.gridButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.searchView) return 4;
        if (currentViewId == R.id.pagination) return 5;
        if (currentViewId == R.id.backgroundGrid1 || currentViewId == R.id.gridButton1) return 6;
        if (currentViewId == R.id.gridButton2) return 7;
        if (currentViewId == R.id.gridButton3) return 8;
        if (currentViewId == R.id.gridButton4) return 9;
        if (currentViewId == R.id.gridButton5) return 10;
        if (currentViewId == R.id.gridButton6) return 11;
        return -1;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.ratingButton || viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.gridButton || viewId == R.id.textClock;
    }

    public static boolean isLowerButtons(int viewId) {
        return viewId == R.id.searchView || viewId == R.id.pagination;
    }

    public static boolean isFirstRow(int viewId) {
        return viewId == R.id.gridButton1 || viewId == R.id.gridButton2 || viewId == R.id.gridButton3;
    }

    public static boolean isSecondRow(int viewId) {
        return viewId == R.id.gridButton4 || viewId == R.id.gridButton5 || viewId == R.id.gridButton6;
    }

    public static boolean isLeftColumn(int viewId) {
        return viewId == R.id.gridButton1 || viewId == R.id.gridButton4;
    }

    public static boolean isMiddleColumn(int viewId) {
        return viewId == R.id.gridButton2 || viewId == R.id.gridButton5;
    }

    public static boolean isRightColumn(int viewId) {
        return viewId == R.id.gridButton3 || viewId == R.id.gridButton6;
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
        return -1;
    }

    // This method is used only when the grid changes. It returns the current overallIndex based on the previous page number and grid type.
    public static int getNewPageNumber(int oldPageNumber, GridNavigation newGrid) {
        if (newGrid == GridNavigation.one) {
            return oldPageNumber * 6;
        } else if (newGrid == GridNavigation.three) {
            return oldPageNumber / 3;
        } else if (newGrid == GridNavigation.six) {
            return oldPageNumber / 2;
        }
        return -1;
    }

    public static int getViewIndexAsInt(int viewId) {
        if (viewId == R.id.backgroundGrid1 || viewId == R.id.gridButton1) return 0;
        if (viewId == R.id.gridButton2) return 1;
        if (viewId == R.id.gridButton3) return 2;
        if (viewId == R.id.gridButton4) return 3;
        if (viewId == R.id.gridButton5) return 4;
        if (viewId == R.id.gridButton6) return 5;
        return -1;
    }
}
