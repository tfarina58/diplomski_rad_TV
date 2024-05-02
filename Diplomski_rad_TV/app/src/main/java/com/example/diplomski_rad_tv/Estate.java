package com.example.diplomski_rad_tv;

import java.util.HashMap;
import java.util.Map;

public class Estate {
    String image;
    double latitude;
    double longitude;
    String name;
    String ownerId;
    String phone;
    Map<String, Object> variables;

    public Estate(String image, double latitude, double longitude, String name, String ownerId, Map<String, Object> variables) {
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.ownerId = ownerId;
        this.variables = variables;
    }

    public Estate() {
        this.image = "";
        this.latitude = 0;
        this.longitude = 0;
        this.name = "";
        this.ownerId = "";
        this.phone = "";
        this.variables = new HashMap<>();
    }

    public Estate(int type) {
        if (type == 1) {
            this.image = "https://media.geeksforgeeks.org/wp-content/cdn-uploads/gfg_200x200-min.png";
            this.latitude = 0;
            this.longitude = 0;
            this.name = "First real estate";
            this.ownerId = "0x4512";
            this.phone = "+385 99...";
            this.variables = new HashMap<>();
        } else if (type == 2) {
            this.image = "https://a.cdn-hotels.com/gdcs/production96/d1236/0623a678-0ad7-4bdf-8d7f-58aac43acef4.jpg";
            this.latitude = 10;
            this.longitude = 40;
            this.name = "Second real estate";
            this.ownerId = "0x9032";
            this.phone = "+362 94";
            this.variables = new HashMap<>();
        }

    }
}
