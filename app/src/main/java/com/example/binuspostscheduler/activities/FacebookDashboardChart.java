package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.binuspostscheduler.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;


public class FacebookDashboardChart extends AppCompatActivity {
    BarChart likeBar, commentBar, viewBar, shareBar;
    ImageView back;
    TextView textDay;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_dashboard_chart);

        Intent in = getIntent();
        int day = in.getIntExtra("idx", 0);

        String[] days = {"All", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        likeBar = findViewById(R.id.barFBLike);
        commentBar = findViewById(R.id.barFBComment);
        viewBar = findViewById(R.id.barFBView);
        shareBar = findViewById(R.id.barFBShare);
        back = findViewById(R.id.dashboardFBBack);
        textDay = findViewById(R.id.FacebookDay);
        setBar(day);

        textDay.setText("Day : " + days[day]);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setBar(int day){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] share = new int[25];
        int[] view = new int[25];
        int[] count = new int[25];
        ArrayList<BarEntry> likeBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> commentBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> shareBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> viewBarEntries = new ArrayList<BarEntry>();
        ArrayList<String> time = new ArrayList<>();

        db.collection("dashboard").document("facebook").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()) {
                        String time = ds.getString("created_time");
                        String dateSplit[] = time.split(" ");
                        String timeSplit[] = dateSplit[1].split(":");
                        String cdateSplit[] = dateSplit[0].split("-");

                        int idx = Integer.parseInt(timeSplit[0]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                        int hari = cal.get(Calendar.DAY_OF_WEEK);

                        if(hari == day || day == 0) {
                            share[idx] += Integer.parseInt(ds.getLong("share").toString());
                            view[idx] += Integer.parseInt(ds.getLong("view").toString());
                            like[idx] += Integer.parseInt(ds.getLong("like").toString());
                            comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                            count[idx]++;
                        }
                    }

                    //looping get data for bar
                    for(int i = 0; i < 24; i++){
                        float x = 0;
                        float y = 0;
                        float a = 0;
                        float b = 0;
                        if(count[i] != 0)  {
                            x = like[i] / count[i];
                            y = comment[i] / count[i];
                            a = share[i] / count[i];
                            b = view[i] / count[i];
                        }
                        likeBarEntries.add(new BarEntry(i, x));
                        commentBarEntries.add(new BarEntry(i, y));
                        shareBarEntries.add(new BarEntry(i, a));
                        viewBarEntries.add(new BarEntry(i, b));
                        String t = i+"-" + i +".59";
                        time.add(t);
                    }

                    //Like Bar
                    BarDataSet likeDataSet = new BarDataSet(likeBarEntries, "Like Count");
                    BarData likeData = new BarData(likeDataSet);
                    setBarAdvanced(time, likeBar, likeData);

                    //Comment Bar
                    BarDataSet commentDataSet = new BarDataSet(commentBarEntries, "Comment Count");
                    BarData commentData = new BarData(commentDataSet);
                    setBarAdvanced(time, commentBar, commentData);

                    //View Bar
                    BarDataSet ViewDataSet = new BarDataSet(viewBarEntries, "View Count");
                    BarData ViewData = new BarData(ViewDataSet);
                    setBarAdvanced(time, viewBar, ViewData);

                    //Share Bar
                    BarDataSet ShareDataSet = new BarDataSet(shareBarEntries, "Share Count");
                    BarData ShareData = new BarData(ShareDataSet);
                    setBarAdvanced(time, shareBar, ShareData);
                }
            }
        });
    }

    private void setBarAdvanced(ArrayList<String> time, BarChart x, BarData barData){
        Description desc = new Description();
        desc.setText("");
        x.setDescription(desc);
        x.setData(barData);

        XAxis xAxis2 = x.getXAxis();
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(time));
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis2.setDrawGridLines(false);
        xAxis2.setDrawAxisLine(false);
        xAxis2.setLabelCount(time.size());
        xAxis2.setLabelRotationAngle(270);
        x.invalidate();
        x.setTouchEnabled(true);
        x.setDragEnabled(true);
        x.setScaleEnabled(true);
    }
}