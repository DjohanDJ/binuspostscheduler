package com.example.binuspostscheduler.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.activities.ScheduleDetailActivity;
import com.example.binuspostscheduler.models.PostedSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AllScheduleAdapter extends RecyclerView.Adapter<AllScheduleAdapter.MyViewHolder> {

    Context ctx;
    ArrayList<PostedSchedule> list;

    public AllScheduleAdapter(Context ctx, ArrayList<PostedSchedule> list){
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public AllScheduleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.all_schedule_list, parent, false);

        return new AllScheduleAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllScheduleAdapter.MyViewHolder holder, final int position) {
        Date pDate = null;
        try {
            pDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(list.get(position).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = new SimpleDateFormat("EEE, d MMM yyyy HH:mm").format(pDate);

        holder.time.setText(time);

        String desc = list.get(position).getDescription();
        if(desc.length() > 50){
            desc = list.get(position).getDescription().substring(0,50) + "...";
        }
        holder.desc.setText(desc);

        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ctx, ScheduleDetailActivity.class);
                PostedSchedule post = list.get(position);
                myIntent.putExtra("id", post.getId());
                myIntent.putExtra("description", post.getDescription());
                myIntent.putExtra("image", post.getImage());
                myIntent.putExtra("hashtags", post.getHashtags());
                myIntent.putExtra("video", post.getVideo());
                myIntent.putExtra("time", post.getTime());
                myIntent.putExtra("selected_id", post.getSelected_id());
                myIntent.putExtra("type", post.getType());
                ctx.startActivity(myIntent);
//                ((Activity)view.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView time, desc;
        Button detail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.allScheduleTime);
            detail = itemView.findViewById(R.id.allScheduleDetailButton);
            desc = itemView.findViewById(R.id.textView5);
        }
    }
}
