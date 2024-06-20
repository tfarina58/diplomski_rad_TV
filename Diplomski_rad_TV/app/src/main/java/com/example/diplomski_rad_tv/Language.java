package com.example.diplomski_rad_tv;

public enum Language {
    croatian, german, english;

    private static final Language[] values = values();
    public Language next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
