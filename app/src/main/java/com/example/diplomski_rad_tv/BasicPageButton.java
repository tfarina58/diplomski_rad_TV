package com.example.diplomski_rad_tv;

public enum BasicPageButton {
    main, language, theme, grid, clock, searchbar, pagination;
    private static final BasicPageButton[] values = values();
    public BasicPageButton next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
