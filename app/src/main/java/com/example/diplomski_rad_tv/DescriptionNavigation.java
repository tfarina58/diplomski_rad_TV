package com.example.diplomski_rad_tv;

public class DescriptionNavigation {
    private static int[][] navigationDescription = {
            {0,                    R.id.descriptionText, 0,                   R.id.themeButton}, // languageButton
            {0,                    R.id.descriptionText, R.id.languageButton, R.id.textClock},   // themeButton
            {0,                    R.id.descriptionText, R.id.themeButton,    0},                // textClock
            {R.id.languageButton,  R.id.image1,          0,                   0},                // descriptionText
            {R.id.descriptionText, R.id.image4,          0,                   R.id.image2},      // image1
            {R.id.descriptionText, R.id.image5,          R.id.image1,         R.id.image3},      // image2
            {R.id.descriptionText, R.id.image6,          R.id.image2,         0},                // image3
            {R.id.image1,          R.id.image7,          0,                   R.id.image5},      // image4
            {R.id.image2,          R.id.image8,          R.id.image4,         R.id.image6},      // image5
            {R.id.image3,          R.id.image9,          R.id.image5,         0},                // image6
            {R.id.image4,          R.id.link1,           0,                   R.id.image8},      // image7
            {R.id.image5,          R.id.link1,           R.id.image7,         R.id.image9},      // image8
            {R.id.image6,          R.id.link1,           R.id.image8,         0},                // image9
            {R.id.image7,          R.id.link2,           0,                   0},                // link1
            {R.id.link1,           R.id.link3,           0,                   0},                // link2
            {R.id.link2,           0,                    0,                   0}                 // link3
    };
    public static int navigateOverActivity(int oldFocusedViewId, int direction) {
        return navigationDescription[getRowWithId(oldFocusedViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.languageButton) return 0;
        if (currentViewId == R.id.themeButton) return 1;
        if (currentViewId == R.id.textClock) return 2;
        if (currentViewId == R.id.descriptionText) return 3;
        if (currentViewId == R.id.image1) return 4;
        if (currentViewId == R.id.image2) return 5;
        if (currentViewId == R.id.image3) return 6;
        if (currentViewId == R.id.image4) return 7;
        if (currentViewId == R.id.image5) return 8;
        if (currentViewId == R.id.image6) return 9;
        if (currentViewId == R.id.image7) return 10;
        if (currentViewId == R.id.image8) return 11;
        if (currentViewId == R.id.image9) return 12;
        if (currentViewId == R.id.link1) return 13;
        if (currentViewId == R.id.link2) return 14;
        if (currentViewId == R.id.link3) return 15;
        return -1;
    }

    static boolean isImage(int viewId) {
        return isFirstRow(viewId) || isSecondRow(viewId) || isThirdRow(viewId);
    }
    static boolean isFirstRow(int viewId) {
        return viewId == R.id.image1 || viewId == R.id.image2 || viewId == R.id.image3;
    }

    static boolean isSecondRow(int viewId) {
        return viewId == R.id.image4 || viewId == R.id.image5 || viewId == R.id.image6;
    }

    static boolean isThirdRow(int viewId) {
        return viewId == R.id.image7 || viewId == R.id.image8 || viewId == R.id.image9;
    }

    static boolean isLeftColumn(int viewId) {
        return viewId == R.id.image1 || viewId == R.id.image4 || viewId == R.id.image7;
    }

    static boolean isMiddleColumn(int viewId) {
        return viewId == R.id.image2 || viewId == R.id.image5 || viewId == R.id.image8;
    }

    static boolean isRightColumn(int viewId) {
        return viewId == R.id.image3 || viewId == R.id.image6 || viewId == R.id.image9;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.textClock;
    }

    public static boolean isLinks(int viewId) {
        return viewId == R.id.link1 || viewId == R.id.link2 || viewId == R.id.link3;
    }
}
