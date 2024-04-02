package com.example.diplomski_rad_tv;

public enum Grid {
    one, three, six;
    private static final Grid[] values = values();
    public Grid next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
