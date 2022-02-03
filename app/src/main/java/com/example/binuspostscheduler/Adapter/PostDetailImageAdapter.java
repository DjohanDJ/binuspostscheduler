package com.example.binuspostscheduler.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostDetailImageAdapter extends RecyclerView.Adapter<PostDetailImageAdapter.PDIViewHolder> {
    private Context ctx;
    private ArrayList<String> imgs;

    public PostDetailImageAdapter(Context ctx, ArrayList<String> imgs) {
        this.ctx = ctx;
        this.imgs = imgs;
    }

    @NonNull
    @NotNull
    @Override
    public PDIViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.post_detail_image_recycler_layout, parent, false);

        return new PDIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PDIViewHolder holder, int position) {
//        Toast.makeText(ctx, imgs.get(position), Toast.LENGTH_SHORT).show();
        Picasso.get().load(imgs.get(position)).into(holder.imgPreview);
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public class PDIViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPreview;


        public PDIViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.image_on_recycler);

        }
    }
}
