package com.example.binuspostscheduler.Adapter;

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

public class TodayScheduleAdapter extends RecyclerView.Adapter<TodayScheduleAdapter.TSViewHolder> {

    Context ctx;
    ArrayList<PostedSchedule> list;

    public TodayScheduleAdapter(Context ctx, ArrayList<PostedSchedule> list){
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public TSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.today_schedule_list_adapter, parent, false);

        return new TSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TSViewHolder holder, final int position) {
        Date pDate = null;
        try {
            pDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(list.get(position).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = new SimpleDateFormat("HH:mm").format(pDate);

        holder.hour.setText(time);

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
                myIntent.putParcelableArrayListExtra("selected_id", post.getSelected_id());
                myIntent.putExtra("type", post.getType());
                ctx.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TSViewHolder extends  RecyclerView.ViewHolder{
        TextView hour;
        Button detail;

        public TSViewHolder(@NonNull View itemView) {
            super(itemView);
            hour = itemView.findViewById(R.id.todayHour);
            detail = itemView.findViewById(R.id.todayDetailButton);
        }
    }
}
