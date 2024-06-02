package com.example.diplomski_rad_tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class CategoryListActivity extends Activity {
    Category[] categories;
    Language language = Language.germany;
    Theme theme = Theme.light;
    Grid grid = Grid.one;
    Clock format = Clock.h12;
    View focusedView;
    String searchbarText = "";
    int overallIndex = 0;
    int currentIndex = 0;
    int currentPage = 0;
    int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
