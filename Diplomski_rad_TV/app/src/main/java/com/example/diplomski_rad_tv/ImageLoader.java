package com.example.diplomski_rad_tv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    private ImageButton imageButton;

    public ImageLoader(ImageButton imageView) {
        this.imageButton = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (imageButton != null) imageButton.setImageBitmap(result);
    }
}