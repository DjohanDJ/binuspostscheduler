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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.fragments.CreatePostFragment;
import com.example.binuspostscheduler.interfaces.AddMediaInterface;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class AddMediaAdapter extends RecyclerView.Adapter<AddMediaAdapter.ViewHolder> {
    private Context ctx;
    private ArrayList<File> imgs;
    private AddMediaInterface fragment;
    private boolean isVideo;
    public AddMediaAdapter(Context ctx, ArrayList<File> imgs, AddMediaInterface fragment, boolean isVideo) {
        this.ctx = ctx;
        this.imgs = imgs;
        this.fragment = fragment;
        this.isVideo = isVideo;
    }


    @NonNull
    @NotNull
    @Override
    public AddMediaAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.image_media_layout, parent, false);

        return new AddMediaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AddMediaAdapter.ViewHolder holder, int position) {
        if(this.isVideo){
            String url = imgs.get(position).getAbsolutePath();
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(url , MediaStore.Video.Thumbnails.MICRO_KIND);
//            bMap.setHeight(200);
//            bMap.setWidth(200);
            holder.imgPreview.setImageBitmap(bMap);
        }
        else Picasso.get().load(imgs.get(position)).resize(200,200).into(holder.imgPreview);
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.removeMedia(position);

                AddMediaAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPreview;
        ImageView removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.add_media_img);
            removeBtn = itemView.findViewById(R.id.add_media_remove);
        }
    }
}
