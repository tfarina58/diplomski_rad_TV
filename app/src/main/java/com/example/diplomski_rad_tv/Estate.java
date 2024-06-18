package com.example.diplomski_rad_tv;

import java.util.HashMap;
import java.util.Map;

public class Estate {
    String id;
    String image;
    double latitude;
    double longitude;
    String name;
    String ownerId;
    String phone;
    Map<String, Object> variables;

    public Estate(String id, String ownerId, String image, double latitude, double longitude, String name, Map<String, Object> variables) {
        this.id = id;
        this.ownerId = ownerId;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.variables = variables;
    }
}
