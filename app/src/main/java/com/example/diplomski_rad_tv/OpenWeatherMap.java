package com.example.diplomski_rad_tv;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class OpenWeatherMap {

    public interface WeatherCallback {
        void onWeatherDataReceived(JSONObject weatherData);
        void onError(String errorMessage);
    }
    public static void sendPostRequest(String urlString, WeatherCallback callback) {
        new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... params) {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        return new JSONObject(response.toString());
                    } else {
                        callback.onError("Error: " + responseCode);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError("Exception: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (result != null) {
                    callback.onWeatherDataReceived(result);
                } else {
                    callback.onError("Failed to retrieve weather data");
                }
            }
        }.execute();
    }

}
