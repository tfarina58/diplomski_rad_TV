package com.example.diplomski_rad_tv;

public enum Clock {
    h24, h12;
    private static final Clock[] values = values();
    public Clock next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
