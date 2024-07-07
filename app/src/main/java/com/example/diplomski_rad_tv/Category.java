package com.example.diplomski_rad_tv;

import java.util.HashMap;

public class Category {
    String id;
    String estateId;
    HashMap<String, String> title;
    String image;

    public Category(String id, String estateId, HashMap<String, String> title, String image) {
        this.id = id;
        this.estateId = estateId;
        this.title = title;
        this.image = image;
    }
}
