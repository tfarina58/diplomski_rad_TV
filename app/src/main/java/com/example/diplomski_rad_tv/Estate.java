package com.example.diplomski_rad_tv;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class Estate {
    String id;
    String image;
    GeoPoint coordinates;
    HashMap<String, String> name;
    String ownerId;
    String phone;

    public Estate(String id, String ownerId, String image, GeoPoint coordinates, HashMap<String, String> name) {
        this.id = id;
        this.ownerId = ownerId;
        this.image = image;
        this.coordinates = coordinates;
        this.name = name;
    }
}
