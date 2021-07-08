package com.example.binuspostscheduler.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.fragments.CreatePostFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class PostDetailImageAdapter extends RecyclerView.Adapter<PostDetailImageAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<String> imgs;

    public PostDetailImageAdapter(Context ctx, ArrayList<String> imgs) {
        this.ctx = ctx;
        this.imgs = imgs;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.post_detail_image_recycler_layout, parent, false);

        return new PostDetailImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
//        Toast.makeText(ctx, imgs.get(position), Toast.LENGTH_SHORT).show();
        Picasso.get().load(imgs.get(position)).into(holder.imgPreview);
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPreview;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.image_on_recycler);

        }
    }
}
