package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Map;

public class BigWorkingHoursLayout {
    public static void setupBigWorkingHoursButton(
        Context ctx, FrameLayout bigWorkingHoursLayout,
        ConstraintLayout background, TextView bigWorkingHoursTitle,
        TextView mondayTitle, TextView mondayContent,
        TextView tuesdayTitle, TextView tuesdayContent,
        TextView wednesdayTitle, TextView wednesdayContent,
        TextView thursdayTitle, TextView thursdayContent,
        TextView fridayTitle, TextView fridayContent,
        TextView saturdayTitle, TextView saturdayContent,
        TextView sundayTitle, TextView sundayContent,
        boolean visible, ArrayList<Map<String, Object>> workingHours,
        Language language, Theme theme
    ) {
        if (bigWorkingHoursLayout == null || background == null || bigWorkingHoursTitle == null ||
            mondayTitle == null || mondayContent == null ||
            tuesdayTitle == null || tuesdayContent == null ||
            wednesdayTitle == null || wednesdayContent == null ||
            thursdayTitle == null || thursdayContent == null ||
            fridayTitle == null || fridayContent == null ||
            saturdayTitle == null || saturdayContent == null ||
            sundayTitle == null || sundayContent == null) return;

        if (!visible) {
            bigWorkingHoursLayout.setVisibility(View.INVISIBLE);
            return;
        }
        bigWorkingHoursLayout.setVisibility(View.VISIBLE);

        switch (language) {
            case german:
                bigWorkingHoursTitle.setText(ContextCompat.getString(ctx, R.string.working_hours_de));
                mondayTitle.setText(ContextCompat.getString(ctx, R.string.monday_de));
                mondayContent.setText(timeToString((double)workingHours.get(0).get("from")) + " - " + timeToString((double)workingHours.get(0).get("to")));
                tuesdayTitle.setText(ContextCompat.getString(ctx, R.string.tuesday_de));
                tuesdayContent.setText(timeToString((double)workingHours.get(1).get("from")) + " - " + timeToString((double)workingHours.get(1).get("to")));
                wednesdayTitle.setText(ContextCompat.getString(ctx, R.string.wednesday_de));
                wednesdayContent.setText(timeToString((double)workingHours.get(2).get("from")) + " - " + timeToString((double)workingHours.get(2).get("to")));
                thursdayTitle.setText(ContextCompat.getString(ctx, R.string.thursday_de));
                thursdayContent.setText(timeToString((double)workingHours.get(3).get("from")) + " - " + timeToString((double)workingHours.get(3).get("to")));
                fridayTitle.setText(ContextCompat.getString(ctx, R.string.friday_de));
                fridayContent.setText(timeToString((double)workingHours.get(4).get("from")) + " - " + timeToString((double)workingHours.get(4).get("to")));
                saturdayTitle.setText(ContextCompat.getString(ctx, R.string.saturday_de));
                saturdayContent.setText(timeToString((double)workingHours.get(5).get("from")) + " - " + timeToString((double)workingHours.get(5).get("to")));
                sundayTitle.setText(ContextCompat.getString(ctx, R.string.sunday_de));
                sundayContent.setText(timeToString((double)workingHours.get(6).get("from")) + " - " + timeToString((double)workingHours.get(6).get("to")));
                break;
            case croatian:
                bigWorkingHoursTitle.setText(ContextCompat.getString(ctx, R.string.working_hours_hr));
                mondayTitle.setText(ContextCompat.getString(ctx, R.string.monday_hr));
                mondayContent.setText(timeToString((double)workingHours.get(0).get("from")) + " - " + timeToString((double)workingHours.get(0).get("to")));
                tuesdayTitle.setText(ContextCompat.getString(ctx, R.string.tuesday_hr));
                tuesdayContent.setText(timeToString((double)workingHours.get(1).get("from")) + " - " + timeToString((double)workingHours.get(1).get("to")));
                wednesdayTitle.setText(ContextCompat.getString(ctx, R.string.wednesday_hr));
                wednesdayContent.setText(timeToString((double)workingHours.get(2).get("from")) + " - " + timeToString((double)workingHours.get(2).get("to")));
                thursdayTitle.setText(ContextCompat.getString(ctx, R.string.thursday_hr));
                thursdayContent.setText(timeToString((double)workingHours.get(3).get("from")) + " - " + timeToString((double)workingHours.get(3).get("to")));
                fridayTitle.setText(ContextCompat.getString(ctx, R.string.friday_hr));
                fridayContent.setText(timeToString((double)workingHours.get(4).get("from")) + " - " + timeToString((double)workingHours.get(4).get("to")));
                saturdayTitle.setText(ContextCompat.getString(ctx, R.string.saturday_hr));
                saturdayContent.setText(timeToString((double)workingHours.get(5).get("from")) + " - " + timeToString((double)workingHours.get(5).get("to")));
                sundayTitle.setText(ContextCompat.getString(ctx, R.string.sunday_hr));
                sundayContent.setText(timeToString((double)workingHours.get(6).get("from")) + " - " + timeToString((double)workingHours.get(6).get("to")));
                break;
            default:
                bigWorkingHoursTitle.setText(ContextCompat.getString(ctx, R.string.working_hours_en));
                mondayTitle.setText(ContextCompat.getString(ctx, R.string.monday_en));
                mondayContent.setText(timeToString((double)workingHours.get(0).get("from")) + " - " + timeToString((double)workingHours.get(0).get("to")));
                tuesdayTitle.setText(ContextCompat.getString(ctx, R.string.tuesday_en));
                tuesdayContent.setText(timeToString((double)workingHours.get(1).get("from")) + " - " + timeToString((double)workingHours.get(1).get("to")));
                wednesdayTitle.setText(ContextCompat.getString(ctx, R.string.wednesday_en));
                wednesdayContent.setText(timeToString((double)workingHours.get(2).get("from")) + " - " + timeToString((double)workingHours.get(2).get("to")));
                thursdayTitle.setText(ContextCompat.getString(ctx, R.string.thursday_en));
                thursdayContent.setText(timeToString((double)workingHours.get(3).get("from")) + " - " + timeToString((double)workingHours.get(3).get("to")));
                fridayTitle.setText(ContextCompat.getString(ctx, R.string.friday_en));
                fridayContent.setText(timeToString((double)workingHours.get(4).get("from")) + " - " + timeToString((double)workingHours.get(4).get("to")));
                saturdayTitle.setText(ContextCompat.getString(ctx, R.string.saturday_en));
                saturdayContent.setText(timeToString((double)workingHours.get(5).get("from")) + " - " + timeToString((double)workingHours.get(5).get("to")));
                sundayTitle.setText(ContextCompat.getString(ctx, R.string.sunday_en));
                sundayContent.setText(timeToString((double)workingHours.get(6).get("from")) + " - " + timeToString((double)workingHours.get(6).get("to")));
        }

        if (theme == Theme.light) {
            background.setBackground(ContextCompat.getDrawable(ctx, R.color.light_theme));
            bigWorkingHoursTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            mondayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            mondayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            tuesdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            tuesdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            wednesdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            wednesdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            thursdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            thursdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            fridayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            fridayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            saturdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            saturdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            sundayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
            sundayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_light_mode));
        } else {
            background.setBackground(ContextCompat.getDrawable(ctx, R.color.dark_theme));
            bigWorkingHoursTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            mondayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            mondayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            tuesdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            tuesdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            wednesdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            wednesdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            thursdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            thursdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            fridayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            fridayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            saturdayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            saturdayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            sundayTitle.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
            sundayContent.setTextColor(ContextCompat.getColor(ctx, R.color.text_color_dark_mode));
        }
    }

    static String timeToString(double time) {
        time = time + 0.00001;
        int hours = (int)(time / 100);
        int minutes = (int)(time % 100);
        return addZeroPrefix(hours) + ":" + addZeroPrefix(minutes);
    }

    static String addZeroPrefix(int timeValue) {
        String timeValueString = "0" + timeValue;
        timeValueString = timeValueString.substring(timeValueString.length() - 2);
        return timeValueString;
    }
}
