package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Map;

public class BigDescriptionTemplate2 {
    public static void setupBigDescriptionTemplate2(
        Context ctx, TextView bigDescriptionContent, boolean visible, Map<String, String> description, Language language, Theme theme) {
        if (bigDescriptionContent == null) return;

        if (!visible) {
            bigDescriptionContent.setVisibility(View.INVISIBLE);
            return;
        }
        bigDescriptionContent.setVisibility(View.VISIBLE);

        switch (language) {
            case german:
                bigDescriptionContent.setText(description.get("de"));
                break;
            case croatian:
                bigDescriptionContent.setText(description.get("hr"));
                break;
            default:
                bigDescriptionContent.setText(description.get("en"));
        }

        if (theme == Theme.light) {
            bigDescriptionContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
            bigDescriptionContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        } else {
            bigDescriptionContent.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
            bigDescriptionContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }
}
