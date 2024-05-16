package com.example.diplomski_rad_tv;

import java.util.HashMap;
import java.util.Map;

public class Category {
    String image;
    String name;

    public Category(String image, String title) {
        this.image = image;
        this.name = title;
    }

    public Category() {
        this.image = "";
        this.name = "";
    }

    public Category(int type) {
        if (type == 1) {
            this.image = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Beach_at_Fort_Lauderdale.jpg/1200px-Beach_at_Fort_Lauderdale.jpg";
            this.name = "First category title";
        } else if (type == 2) {
            this.image = "https://images.axios.com/skpkBe_uWLPLI1cgG63jwttto14=/0x253:8232x4884/1920x1080/2023/05/26/1685120985250.jpg";
            this.name = "Second category title";
        }
    }
}
