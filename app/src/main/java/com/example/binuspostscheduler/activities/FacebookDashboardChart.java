package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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


public class FacebookDashboardChart extends AppCompatActivity {
    BarChart likeBar, commentBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_dashboard_chart);

        likeBar = findViewById(R.id.barFBLike);
        commentBar = findViewById(R.id.barFBComment);
        setBar();

    }

    private void setBar(){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] count = new int[25];
        ArrayList<BarEntry> likeBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> commentBarEntries = new ArrayList<BarEntry>();
        ArrayList<String> time = new ArrayList<>();

        db.collection("dashboard").document("facebook").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()) {
                        String time = ds.getString("created_time");
                        String timeSplit[] = time.split(":");

                        int idx = Integer.parseInt(timeSplit[0]);

                        like[idx] += Integer.parseInt(ds.getLong("like").toString());
                        comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                        count[idx]++;
                    }

                    //looping get data for bar
                    for(int i = 0; i < 24; i++){
                        float x = 0;
                        float y = 0;
                        if(count[i] != 0)  {
                            x = like[i] / count[i];
                            y = comment[i] / count[i];
                        }
                        likeBarEntries.add(new BarEntry(i, x));
                        commentBarEntries.add(new BarEntry(i, y));
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