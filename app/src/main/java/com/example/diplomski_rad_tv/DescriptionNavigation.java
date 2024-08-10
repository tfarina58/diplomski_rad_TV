package com.example.diplomski_rad_tv;

public class DescriptionNavigation {
    private static final int[][] navigationDescription1 = {
            {0,                       R.id.descriptionContent, 0,                      R.id.languageButton},    // ratingButton
            {0,                       R.id.descriptionContent, R.id.ratingButton,      R.id.themeButton},       // languageButton
            {0,                       R.id.descriptionContent, R.id.languageButton,    R.id.textClock},         // themeButton
            {0,                       R.id.descriptionContent, R.id.themeButton,       R.id.weatherButton},     // textClock
            {0,                       R.id.descriptionContent, R.id.textClock,         0},                      // weatherButton
            {R.id.languageButton,     R.id.descriptionImage1,  0,                      0},                      // descriptionText
            {R.id.descriptionContent, R.id.descriptionImage4,  0,                      R.id.descriptionImage2}, // image1
            {R.id.descriptionContent, R.id.descriptionImage5,  R.id.descriptionImage1, R.id.descriptionImage3}, // image2
            {R.id.descriptionContent, R.id.descriptionImage6,  R.id.descriptionImage2, 0},                      // image3
            {R.id.descriptionImage1,  R.id.descriptionImage7,  0,                      R.id.descriptionImage5}, // image4
            {R.id.descriptionImage2,  R.id.descriptionImage8,  R.id.descriptionImage4, R.id.descriptionImage6}, // image5
            {R.id.descriptionImage3,  R.id.descriptionImage9,  R.id.descriptionImage5, 0},                      // image6
            {R.id.descriptionImage4,  R.id.smallWorkingHours,  0,                      R.id.descriptionImage8}, // image7
            {R.id.descriptionImage5,  R.id.smallWorkingHours,  R.id.descriptionImage7, R.id.descriptionImage9}, // image8
            {R.id.descriptionImage6,  R.id.smallWorkingHours,  R.id.descriptionImage8, 0},                      // image9
            {R.id.descriptionImage7,  R.id.descriptionLink1,   0,                      0},                      // smallWorkingHours
            {R.id.smallWorkingHours,  0,                       0,                      R.id.descriptionLink2},  // link1
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink1,  R.id.descriptionLink3},  // link2
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink2,  0}                       // link3
    };

    private static final int[][] navigationDescription2 = {
            {0,                       R.id.descriptionContent, 0,                       R.id.languageButton},    // ratingButton
            {0,                       R.id.descriptionContent, R.id.ratingButton,       R.id.themeButton},       // languageButton
            {0,                       R.id.descriptionContent, R.id.languageButton,     R.id.textClock},         // themeButton
            {0,                       R.id.descriptionContent, R.id.themeButton,        R.id.weatherButton},     // textClock
            {0,                       R.id.descriptionContent, R.id.textClock,          0},                      // weatherButton
            {R.id.languageButton,     R.id.smallWorkingHours,  0,                       R.id.descriptionImage1}, // descriptionText
            {R.id.languageButton,     R.id.smallWorkingHours,  R.id.descriptionContent, 0},                      // image1
            {R.id.descriptionContent, R.id.descriptionLink1,   0,                       0},                      // smallWorkingHours
            {R.id.smallWorkingHours,  0,                       0,                       R.id.descriptionLink2},  // link1
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink1,   R.id.descriptionLink3},  // link2
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink2,   0}                       // link3
    };

    private static final int[][] navigationDescription3 = {
            {0,                      R.id.viewPager,         0,                     R.id.languageButton},   // ratingButton
            {0,                      R.id.viewPager,         R.id.ratingButton,     R.id.themeButton},      // languageButton
            {0,                      R.id.viewPager,         R.id.languageButton,   R.id.textClock},        // themeButton
            {0,                      R.id.viewPager,         R.id.themeButton,      R.id.weatherButton},    // textClock
            {0,                      R.id.viewPager,         R.id.textClock,        0},                     // weatherButton
            {R.id.languageButton,    R.id.smallWorkingHours, 0,                     0},                     // viewPager
            {R.id.viewPager,         R.id.descriptionLink1,  0,                     0},                     // smallWorkingHours
            {R.id.smallWorkingHours, 0,                      0,                     R.id.descriptionLink2}, // link1
            {R.id.smallWorkingHours, 0,                      R.id.descriptionLink1, R.id.descriptionLink3}, // link2
            {R.id.smallWorkingHours, 0,                      R.id.descriptionLink2, 0}                      // link3
    };

    private static final int[][] navigationDescription1WithoutWeather = {
            {0,                       R.id.descriptionContent, 0,                      R.id.languageButton},    // ratingButton
            {0,                       R.id.descriptionContent, R.id.ratingButton,      R.id.themeButton},       // languageButton
            {0,                       R.id.descriptionContent, R.id.languageButton,    R.id.textClock},         // themeButton
            {0,                       R.id.descriptionContent, R.id.themeButton,       0},                      // textClock
            {R.id.languageButton,     R.id.descriptionImage1,  0,                      0},                      // descriptionText
            {R.id.descriptionContent, R.id.descriptionImage4,  0,                      R.id.descriptionImage2}, // image1
            {R.id.descriptionContent, R.id.descriptionImage5,  R.id.descriptionImage1, R.id.descriptionImage3}, // image2
            {R.id.descriptionContent, R.id.descriptionImage6,  R.id.descriptionImage2, 0},                      // image3
            {R.id.descriptionImage1,  R.id.descriptionImage7,  0,                      R.id.descriptionImage5}, // image4
            {R.id.descriptionImage2,  R.id.descriptionImage8,  R.id.descriptionImage4, R.id.descriptionImage6}, // image5
            {R.id.descriptionImage3,  R.id.descriptionImage9,  R.id.descriptionImage5, 0},                      // image6
            {R.id.descriptionImage4,  R.id.smallWorkingHours,  0,                      R.id.descriptionImage8}, // image7
            {R.id.descriptionImage5,  R.id.smallWorkingHours,  R.id.descriptionImage7, R.id.descriptionImage9}, // image8
            {R.id.descriptionImage6,  R.id.smallWorkingHours,  R.id.descriptionImage8, 0},                      // image9
            {R.id.descriptionImage7,  R.id.descriptionLink1,   0,                      0},                      // smallWorkingHours
            {R.id.smallWorkingHours,  0,                       0,                      R.id.descriptionLink2},  // link1
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink1,  R.id.descriptionLink3},  // link2
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink2,  0}                       // link3
    };

    private static final int[][] navigationDescription2WithoutWeather = {
            {0,                       R.id.descriptionContent, 0,                       R.id.languageButton},    // ratingButton
            {0,                       R.id.descriptionContent, R.id.ratingButton,       R.id.themeButton},       // languageButton
            {0,                       R.id.descriptionContent, R.id.languageButton,     R.id.textClock},         // themeButton
            {0,                       R.id.descriptionContent, R.id.themeButton,        0},                      // textClock
            {R.id.languageButton,     R.id.smallWorkingHours,  0,                       R.id.descriptionImage1}, // descriptionText
            {R.id.languageButton,     R.id.smallWorkingHours,  R.id.descriptionContent, 0},                      // image1
            {R.id.descriptionContent, R.id.descriptionLink1,   0,                       0},                      // smallWorkingHours
            {R.id.smallWorkingHours,  0,                       0,                       R.id.descriptionLink2},  // link1
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink1,   R.id.descriptionLink3},  // link2
            {R.id.smallWorkingHours,  0,                       R.id.descriptionLink2,   0}                       // link3
    };

    private static final int[][] navigationDescription3WithoutWeather = {
            {0,                      R.id.viewPager,         0,                     R.id.languageButton},   // ratingButton
            {0,                      R.id.viewPager,         R.id.ratingButton,     R.id.themeButton},      // languageButton
            {0,                      R.id.viewPager,         R.id.languageButton,   R.id.textClock},        // themeButton
            {0,                      R.id.viewPager,         R.id.themeButton,      0},                     // textClock
            {R.id.languageButton,    R.id.smallWorkingHours, 0,                     0},                     // viewPager
            {R.id.viewPager,         R.id.descriptionLink1,  0,                     0},                     // smallWorkingHours
            {R.id.smallWorkingHours, 0,                      0,                     R.id.descriptionLink2}, // link1
            {R.id.smallWorkingHours, 0,                      R.id.descriptionLink1, R.id.descriptionLink3}, // link2
            {R.id.smallWorkingHours, 0,                      R.id.descriptionLink2, 0}                      // link3
    };
    public static int navigateOverActivity(long template, boolean hasWeatherButton, int oldFocusedViewId, int direction) {
        if (!hasWeatherButton) {
            if (template == 1) return navigationDescription1WithoutWeather[getDescription1RowWithId(oldFocusedViewId)][direction];
            if (template == 2) return navigationDescription2WithoutWeather[getDescription2RowWithId(oldFocusedViewId)][direction];
            if (template == 3) return navigationDescription3WithoutWeather[getDescription3RowWithId(oldFocusedViewId)][direction];
        } else {
            if (template == 1) return navigationDescription1[getDescription1RowWeatherButtonWithId(oldFocusedViewId)][direction];
            if (template == 2) return navigationDescription2[getDescription2RowWeatherButtonWithId(oldFocusedViewId)][direction];
            if (template == 3) return navigationDescription3[getDescription3RowWeatherButtonWithId(oldFocusedViewId)][direction];
        }
        return -1;
    }

    public static int getRowWithId(long template, boolean hasWeatherButton, int currentViewId) {
        if (!hasWeatherButton) {
            if (template == 1) return getDescription1RowWithId(currentViewId);
            if (template == 2) return getDescription2RowWithId(currentViewId);
            if (template == 3) return getDescription3RowWithId(currentViewId);
        } else {
            if (template == 1) return getDescription1RowWeatherButtonWithId(currentViewId);
            if (template == 2) return getDescription2RowWeatherButtonWithId(currentViewId);
            if (template == 3) return getDescription3RowWeatherButtonWithId(currentViewId);
        }

        return -1;
    }

    public static int getDescription1RowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.descriptionContent) return 4;
        if (currentViewId == R.id.descriptionImage1) return 5;
        if (currentViewId == R.id.descriptionImage2) return 6;
        if (currentViewId == R.id.descriptionImage3) return 7;
        if (currentViewId == R.id.descriptionImage4) return 8;
        if (currentViewId == R.id.descriptionImage5) return 9;
        if (currentViewId == R.id.descriptionImage6) return 10;
        if (currentViewId == R.id.descriptionImage7) return 11;
        if (currentViewId == R.id.descriptionImage8) return 12;
        if (currentViewId == R.id.descriptionImage9) return 13;
        if (currentViewId == R.id.smallWorkingHours) return 14;
        if (currentViewId == R.id.descriptionLink1) return 15;
        if (currentViewId == R.id.descriptionLink2) return 16;
        if (currentViewId == R.id.descriptionLink3) return 17;
        return -1;
    }

    public static int getDescription2RowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.descriptionContent) return 4;
        if (currentViewId == R.id.descriptionImage1) return 5;
        if (currentViewId == R.id.smallWorkingHours) return 6;
        if (currentViewId == R.id.descriptionLink1) return 7;
        if (currentViewId == R.id.descriptionLink2) return 8;
        if (currentViewId == R.id.descriptionLink3) return 9;
        return -1;
    }

    public static int getDescription3RowWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.viewPager) return 4;
        if (currentViewId == R.id.smallWorkingHours) return 5;
        if (currentViewId == R.id.descriptionLink1) return 6;
        if (currentViewId == R.id.descriptionLink2) return 7;
        if (currentViewId == R.id.descriptionLink3) return 8;
        return -1;
    }

    public static int getDescription1RowWeatherButtonWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.weatherButton) return 4;
        if (currentViewId == R.id.descriptionContent) return 5;
        if (currentViewId == R.id.descriptionImage1) return 6;
        if (currentViewId == R.id.descriptionImage2) return 7;
        if (currentViewId == R.id.descriptionImage3) return 8;
        if (currentViewId == R.id.descriptionImage4) return 9;
        if (currentViewId == R.id.descriptionImage5) return 10;
        if (currentViewId == R.id.descriptionImage6) return 11;
        if (currentViewId == R.id.descriptionImage7) return 12;
        if (currentViewId == R.id.descriptionImage8) return 13;
        if (currentViewId == R.id.descriptionImage9) return 14;
        if (currentViewId == R.id.smallWorkingHours) return 15;
        if (currentViewId == R.id.descriptionLink1) return 16;
        if (currentViewId == R.id.descriptionLink2) return 17;
        if (currentViewId == R.id.descriptionLink3) return 18;
        return -1;
    }

    public static int getDescription2RowWeatherButtonWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.weatherButton) return 4;
        if (currentViewId == R.id.descriptionContent) return 5;
        if (currentViewId == R.id.descriptionImage1) return 6;
        if (currentViewId == R.id.smallWorkingHours) return 7;
        if (currentViewId == R.id.descriptionLink1) return 8;
        if (currentViewId == R.id.descriptionLink2) return 9;
        if (currentViewId == R.id.descriptionLink3) return 10;
        return -1;
    }

    public static int getDescription3RowWeatherButtonWithId(int currentViewId) {
        if (currentViewId == R.id.ratingButton) return 0;
        if (currentViewId == R.id.languageButton) return 1;
        if (currentViewId == R.id.themeButton) return 2;
        if (currentViewId == R.id.textClock) return 3;
        if (currentViewId == R.id.weatherButton) return 4;
        if (currentViewId == R.id.viewPager) return 5;
        if (currentViewId == R.id.smallWorkingHours) return 6;
        if (currentViewId == R.id.descriptionLink1) return 7;
        if (currentViewId == R.id.descriptionLink2) return 8;
        if (currentViewId == R.id.descriptionLink3) return 9;
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
