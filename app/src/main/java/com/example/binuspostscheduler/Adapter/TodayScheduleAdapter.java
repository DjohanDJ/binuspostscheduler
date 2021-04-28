package com.example.binuspostscheduler.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.models.PostedSchedule;

import java.util.ArrayList;

public class TodayScheduleAdapter extends RecyclerView.Adapter<TodayScheduleAdapter.MyViewHolder> {

    Context ctx;
    ArrayList<PostedSchedule> list;

    public TodayScheduleAdapter(Context ctx, ArrayList<PostedSchedule> list){
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.today_schedule_list_adapter, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
