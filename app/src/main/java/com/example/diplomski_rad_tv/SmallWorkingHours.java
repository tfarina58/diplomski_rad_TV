package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class SmallWorkingHours {
    public static void setupSmallWorkingHours(Context ctx, Button smallWorkingHours, TextView workingHours, TextView entryFee, TextView minimalAge, Element element, View focusedView, Language language, Theme theme) {
        if (smallWorkingHours == null || workingHours == null || entryFee == null || minimalAge == null) return;
        if (element.workingHours.size() == 0) {
            minimalAge.setVisibility(View.GONE);
            entryFee.setVisibility(View.GONE);
            workingHours.setVisibility(View.GONE);
            smallWorkingHours.setVisibility(View.GONE);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek - 1;
        dayOfWeek = (dayOfWeek + 6) % 7;

        int fromHours = (int)(((double)element.workingHours.get(dayOfWeek).get("from") + 0.00001) / 100);
        int fromMinutes = (int)(((double)element.workingHours.get(dayOfWeek).get("from") + 0.00001) % 100);
        int toHours = (int)(((double)element.workingHours.get(dayOfWeek).get("to") + 0.00001) / 100);
        int toMinutes = (int)(((double)element.workingHours.get(dayOfWeek).get("to") + 0.00001) % 100);

        String fromHoursString = addZeroPrefix(fromHours);
        String fromMinutesString = addZeroPrefix(fromMinutes);
        String toHoursString = addZeroPrefix(toHours);
        String toMinutesString = addZeroPrefix(toMinutes);

        switch (language) {
            case german:
                workingHours.setText(ContextCompat.getString(ctx, R.string.working_hours_de) + "\n" + fromHoursString + ":" + fromMinutesString + " - " + toHoursString + ":" + toMinutesString);
                entryFee.setText(ContextCompat.getString(ctx, R.string.entry_fee_de) + "\n" + element.entryFee);
                minimalAge.setText(ContextCompat.getString(ctx, R.string.minimal_age_de) + "\n" + element.minimalAge);
                break;
            case croatian:
                workingHours.setText(ContextCompat.getString(ctx, R.string.working_hours_hr) + "\n" + fromHoursString + ":" + fromMinutesString + " - " + toHoursString + ":" + toMinutesString);
                entryFee.setText(ContextCompat.getString(ctx, R.string.entry_fee_hr) + "\n" + element.entryFee);
                minimalAge.setText(ContextCompat.getString(ctx, R.string.minimal_age_hr) + "\n" + element.minimalAge);
                break;
            default:
                workingHours.setText(ContextCompat.getString(ctx, R.string.working_hours_en) + "\n" + fromHoursString + ":" + fromMinutesString + " - " + toHoursString + ":" + toMinutesString);
                entryFee.setText(ContextCompat.getString(ctx, R.string.entry_fee_en) + "\n" + element.entryFee);
                minimalAge.setText(ContextCompat.getString(ctx, R.string.minimal_age_en) + "\n" + element.minimalAge);
        }

        if (focusedView.getId() == R.id.smallWorkingHours) {
            if (theme == Theme.light) smallWorkingHours.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background_focused));
            else smallWorkingHours.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background_focused));
        } else {
            if (theme == Theme.light) smallWorkingHours.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_cream_background));
            else smallWorkingHours.setBackground(ContextCompat.getDrawable(ctx, R.drawable.login_string_field_purple_background));
        }

        if (theme == Theme.light) {
            workingHours.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            entryFee.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            minimalAge.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        } else {
            workingHours.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            entryFee.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            minimalAge.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }

    static String addZeroPrefix(long timeValue) {
        String timeValueString = "0" + timeValue;
        timeValueString = timeValueString.substring(timeValueString.length() - 2);
        return timeValueString;
    }
}
