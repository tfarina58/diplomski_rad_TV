package com.example.diplomski_rad_tv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Do not intercept touch events, so child views can handle them
        return false;
    }

    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        // Do not intercept touch events, so child views can handle them
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Do not handle touch events
        return false;
    }

    @Override
    public void scrollTo(int x, int y) {
        // Do nothing when scrollTo is called
    }
}
