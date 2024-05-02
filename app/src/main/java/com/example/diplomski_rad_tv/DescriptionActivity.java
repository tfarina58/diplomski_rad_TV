package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class DescriptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);

        TextView buttonTitle = findViewById(R.id.title1);
        buttonTitle.setText("Element name (description)");
    }
}
