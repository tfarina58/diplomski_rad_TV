package com.example.diplomski_rad_tv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Element {
    String id;
    String categoryId;
    String background;
    ArrayList<String> images;
    HashMap<String, String> description;
    ArrayList<HashMap<String, Object>> links;
    HashMap<String, String> title;
    long template;
    ArrayList<Map<String, Object>> workingHours;
    String entryFee;
    long minimalAge;

    public Element(String id, String categoryId, String background, HashMap<String, String> title, HashMap<String, String> description, ArrayList<String> images, ArrayList<HashMap<String, Object>> links, long template, ArrayList<Map<String, Object>> workingHours, String entryFee, long minimalAge) {
        this.id = id;
        this.categoryId = categoryId;
        this.background = background;
        this.images = images;
        this.description = description;
        this.links = links;
        this.title = title;
        this.template = template;
        this.workingHours = workingHours;
        this.entryFee = entryFee;
        this.minimalAge = minimalAge;
    }
}
