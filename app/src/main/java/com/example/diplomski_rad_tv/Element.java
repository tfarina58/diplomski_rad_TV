package com.example.diplomski_rad_tv;

import java.util.ArrayList;
import java.util.HashMap;

public class Element {
    String id;
    String categoryId;
    String background;
    ArrayList<String> images;
    String description;
    ArrayList<HashMap<String, String>> links;
    String title;

    public Element(String id, String categoryId, String background, String title, String description, ArrayList<String> images, ArrayList<HashMap<String, String>> links) {
        this.id = id;
        this.categoryId = categoryId;
        this.background = background;
        this.images = images;
        this.description = description;
        this.links = links;
        this.title = title;
    }
}
