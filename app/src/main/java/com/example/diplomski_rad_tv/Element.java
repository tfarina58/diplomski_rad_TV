package com.example.diplomski_rad_tv;

import java.util.ArrayList;
import java.util.HashMap;

public class Element {
    String id;
    String categoryId;
    String background;
    ArrayList<String> images;
    HashMap<String, String> description;
    ArrayList<HashMap<String, Object>> links;
    HashMap<String, String> title;

    public Element(String id, String categoryId, String background, HashMap<String, String> title, HashMap<String, String> description, ArrayList<String> images, ArrayList<HashMap<String, Object>> links) {
        this.id = id;
        this.categoryId = categoryId;
        this.background = background;
        this.images = images;
        this.description = description;
        this.links = links;
        this.title = title;
    }
}
