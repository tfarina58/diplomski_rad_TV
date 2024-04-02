package com.example.diplomski_rad_tv;

public enum Theme {
    light, dark;

    private static final Theme[] values = values();
    public Theme next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
