package com.example.diplomski_rad_tv;

public enum Language {
    croatia, germany, united_kingdom;

    private static final Language[] values = values();
    public Language next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
