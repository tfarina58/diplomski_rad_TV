package com.example.diplomski_rad_tv;

public class DescriptionNavigation {
    private static final int[][] navigationDescription1 = {
            {0,                       R.id.descriptionContent, 0,                      R.id.themeButton},       // languageButton
            {0,                       R.id.descriptionContent, R.id.languageButton,    R.id.textClock},         // themeButton
            {0,                       R.id.descriptionContent, R.id.themeButton,       0},                      // textClock
            {R.id.languageButton,     R.id.descriptionImage1,  0,                      0},                      // descriptionText
            {R.id.descriptionContent, R.id.descriptionImage4,  0,                      R.id.descriptionImage2}, // image1
            {R.id.descriptionContent, R.id.descriptionImage5,  R.id.descriptionImage1, R.id.descriptionImage3}, // image2
            {R.id.descriptionContent, R.id.descriptionImage6,  R.id.descriptionImage2, 0},                      // image3
            {R.id.descriptionImage1,  R.id.descriptionImage7,  0,                      R.id.descriptionImage5}, // image4
            {R.id.descriptionImage2,  R.id.descriptionImage8,  R.id.descriptionImage4, R.id.descriptionImage6}, // image5
            {R.id.descriptionImage3,  R.id.descriptionImage9,  R.id.descriptionImage5, 0},                      // image6
            {R.id.descriptionImage4,  R.id.descriptionLink1,   0,                      R.id.descriptionImage8}, // image7
            {R.id.descriptionImage5,  R.id.descriptionLink1,   R.id.descriptionImage7, R.id.descriptionImage9}, // image8
            {R.id.descriptionImage6,  R.id.descriptionLink1,   R.id.descriptionImage8, 0},                      // image9
            {R.id.descriptionImage7,  0,                       0,                      R.id.descriptionLink2},  // link1
            {R.id.descriptionImage8,  0,                       R.id.descriptionLink1,  R.id.descriptionLink3},  // link2
            {R.id.descriptionImage9,  0,                       R.id.descriptionLink2,  0}                       // link3
    };
    public static int navigateOverActivity(int oldFocusedViewId, int direction) {
        return navigationDescription1[getRowWithId(oldFocusedViewId)][direction];
    }
    public static int getRowWithId(int currentViewId) {
        if (currentViewId == R.id.languageButton) return 0;
        if (currentViewId == R.id.themeButton) return 1;
        if (currentViewId == R.id.textClock) return 2;
        if (currentViewId == R.id.descriptionContent) return 3;
        if (currentViewId == R.id.descriptionImage1) return 4;
        if (currentViewId == R.id.descriptionImage2) return 5;
        if (currentViewId == R.id.descriptionImage3) return 6;
        if (currentViewId == R.id.descriptionImage4) return 7;
        if (currentViewId == R.id.descriptionImage5) return 8;
        if (currentViewId == R.id.descriptionImage6) return 9;
        if (currentViewId == R.id.descriptionImage7) return 10;
        if (currentViewId == R.id.descriptionImage8) return 11;
        if (currentViewId == R.id.descriptionImage9) return 12;
        if (currentViewId == R.id.descriptionLink1) return 13;
        if (currentViewId == R.id.descriptionLink2) return 14;
        if (currentViewId == R.id.descriptionLink3) return 15;
        return -1;
    }

    static boolean isImage(int viewId) {
        return isFirstRow(viewId) || isSecondRow(viewId) || isThirdRow(viewId);
    }
    static boolean isFirstRow(int viewId) {
        return viewId == R.id.descriptionImage1 || viewId == R.id.descriptionImage2 || viewId == R.id.descriptionImage3;
    }

    static boolean isSecondRow(int viewId) {
        return viewId == R.id.descriptionImage4 || viewId == R.id.descriptionImage5 || viewId == R.id.descriptionImage6;
    }

    static boolean isThirdRow(int viewId) {
        return viewId == R.id.descriptionImage7 || viewId == R.id.descriptionImage8 || viewId == R.id.descriptionImage9;
    }

    static boolean isLeftColumn(int viewId) {
        return viewId == R.id.descriptionImage1 || viewId == R.id.descriptionImage4 || viewId == R.id.descriptionImage7;
    }

    static boolean isMiddleColumn(int viewId) {
        return viewId == R.id.descriptionImage2 || viewId == R.id.descriptionImage5 || viewId == R.id.descriptionImage8;
    }

    static boolean isRightColumn(int viewId) {
        return viewId == R.id.descriptionImage3 || viewId == R.id.descriptionImage6 || viewId == R.id.descriptionImage9;
    }

    public static boolean isUpperButtons(int viewId) {
        return viewId == R.id.languageButton || viewId == R.id.themeButton || viewId == R.id.textClock;
    }

    public static boolean isLinks(int viewId) {
        return viewId == R.id.descriptionLink1 || viewId == R.id.descriptionLink2 || viewId == R.id.descriptionLink3;
    }

    public static int getLinkIndexByViewId(int linkViewId) {
        if (linkViewId == R.id.descriptionLink1) return 0;
        if (linkViewId == R.id.descriptionLink2) return 1;
        if (linkViewId == R.id.descriptionLink3) return 2;
        return -1;
    }

    public static int getImageIndexByViewId(int imageViewId) {
        if (imageViewId == R.id.descriptionImage1) return 0;
        if (imageViewId == R.id.descriptionImage2) return 1;
        if (imageViewId == R.id.descriptionImage3) return 2;
        if (imageViewId == R.id.descriptionImage4) return 3;
        if (imageViewId == R.id.descriptionImage5) return 4;
        if (imageViewId == R.id.descriptionImage6) return 5;
        if (imageViewId == R.id.descriptionImage7) return 6;
        if (imageViewId == R.id.descriptionImage8) return 7;
        if (imageViewId == R.id.descriptionImage9) return 8;
        return -1;
    }
}
