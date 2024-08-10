package com.example.diplomski_rad_tv;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class WeatherHeaderButton {
    public static void setupWeatherButton(Context ctx, Button weatherButton, ImageView weatherIcon, View focusedView, boolean daytime, int weatherCode, double temperature, String unit, Theme theme) {
        if (weatherButton == null || weatherIcon == null) return;

        if (weatherCode == 0) {
            weatherButton.setVisibility(View.GONE);
            weatherIcon.setVisibility(View.GONE);
            return;
        }
        weatherButton.setVisibility(View.VISIBLE);
        weatherIcon.setVisibility(View.VISIBLE);

        weatherButton.setText(getTemperature(temperature, unit));
        weatherIcon.setBackground(ContextCompat.getDrawable(ctx, getWeatherImageId(daytime, weatherCode)));

        if (focusedView.getId() == R.id.weatherButton) {
            if (theme == Theme.light) weatherButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.cream_background));
            else weatherButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.purple_background));
        } else {
            if (theme == Theme.light) weatherButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_cream_background));
            else weatherButton.setBackground(ContextCompat.getDrawable(ctx, R.drawable.secondary_purple_background));
        }

        if (theme == Theme.light) weatherButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_light_mode));
        else weatherButton.setTextColor(ContextCompat.getColor(ctx, R.color.header_button_text_dark_mode));
    }

    private static int getWeatherImageId(boolean daytime, int weatherCode) {
        if (!daytime) return R.drawable.moon;

        int generalCode = weatherCode / 100;
        switch (generalCode) {
            case 2:
                return R.drawable.thunderstorm;
            case 3:
                return R.drawable.drizzle;
            case 5:
                return R.drawable.rain;
            case 6:
                return R.drawable.snow;
            case 7:
                if (weatherCode >= 701 && weatherCode <= 762) return R.drawable.fog;
                if (weatherCode == 771) return R.drawable.rain;
                if (weatherCode == 781) return R.drawable.tornado;
            case 8:
                if (weatherCode == 800) return R.drawable.sun;
                if (weatherCode > 800) return R.drawable.clouds;
        }
        return R.drawable.sun;
    }
    private static String getTemperature(double temperature, String unit) {
        String temperatureString = "";
        if (unit.equals("F")) temperatureString += convertKelvinToFahrenheit(temperature);
        else temperatureString += convertKelvinToCelsius(temperature);
        temperatureString += "°" + unit;
        return temperatureString;
    }
    private static int convertKelvinToFahrenheit(double kelvin) {
        return (int)Math.round((kelvin - 273.15) * 1.8 + 32);
    }
    private static int convertKelvinToCelsius(double kelvin) {
        return (int)Math.round(kelvin - 273.15);
    }
}
