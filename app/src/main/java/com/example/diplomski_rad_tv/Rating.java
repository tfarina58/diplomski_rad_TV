package com.example.diplomski_rad_tv;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

public class Rating {
    String id;
    String comment;
    Timestamp created;
    String estateId;
    double rating;
    String username;
    public Rating(String id, String estateId, String comment, double rating, Timestamp created, String username) {
        this.id = id;
        this.comment = comment;
        this.created = created;
        this.estateId = estateId;
        this.rating = rating;
        this.username = username;
    }
}
