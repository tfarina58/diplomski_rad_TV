package com.example.diplomski_rad_tv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<String> imagesUrl;

    public Adapter(ArrayList<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (imagesUrl.get(position) == null || imagesUrl.get(position).isEmpty()) holder.fullscreenImage.setImageDrawable(null);
        else {
            try {
                Picasso.get()
                        .load(imagesUrl.get(position))
                        .fit()
                        .into(holder.fullscreenImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fullscreenImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullscreenImage = itemView.findViewById(R.id.fullscreenImage);
        }
    }
}
