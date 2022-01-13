package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.binuspostscheduler.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InstagramDashboardChart extends AppCompatActivity {
    BarChart likeBar, commentBar,saveBar, reachBar, impressionBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_dashboard_chart);

        likeBar = findViewById(R.id.barInstagramLike);
        commentBar = findViewById(R.id.barInstagramComment);
        saveBar = findViewById(R.id.barInstagramSave);
        reachBar = findViewById(R.id.barInstagramReach);
        impressionBar = findViewById(R.id.barInstagramImpression);
        setBar();
    }

    private void setBar(){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] reach = new int[25];
        int[] save = new int[25];
        int[] impression = new int[25];
        int[] count = new int[25];
        ArrayList<BarEntry> likeBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> commentBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> reachBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> saveBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> impressionBarEntries = new ArrayList<BarEntry>();
        ArrayList<String> time = new ArrayList<>();

        db.collection("dashboard").document("instagram").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()){
                        String time = ds.getString("created_time");
                        String timeSplit[] = time.split(":");

                        int idx = Integer.parseInt(timeSplit[0]);

                        like[idx] += Integer.parseInt(ds.getLong("like").toString());
                        comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                        save[idx] += Integer.parseInt(ds.getLong("save").toString());
                        reach[idx] += Integer.parseInt(ds.getLong("reach").toString());
                        impression[idx] += Integer.parseInt(ds.getLong("impression").toString());
                        count[idx]++;
                    }

                    //looping get data for bar
                    for(int i = 0; i < 24; i++){
                        float a=0 , b=0, c=0, d=0, e=0;
                        if(count[i] != 0)  {
                            a = like[i] / count[i];
                            b = comment[i] / count[i];
                            c = save[i] / count[i];
                            d = reach[i] / count[i];
                            e = impression[i] / count[i];
                        }
                        likeBarEntries.add(new BarEntry(i, a));
                        commentBarEntries.add(new BarEntry(i, b));
                        saveBarEntries.add(new BarEntry(i, c));
                        reachBarEntries.add(new BarEntry(i, d));
                        impressionBarEntries.add(new BarEntry(i, e));
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

                    //Save Bar
                    BarDataSet saveDataSet = new BarDataSet(saveBarEntries, "Save Count");
                    BarData saveData = new BarData(saveDataSet);
                    setBarAdvanced(time, saveBar, saveData);

                    //Reach bar
                    BarDataSet reachDataSet = new BarDataSet(reachBarEntries, "Reach Count");
                    BarData reachData = new BarData(reachDataSet);
                    setBarAdvanced(time, reachBar, reachData);

                    //Impression Bar
                    BarDataSet impressionDataSet = new BarDataSet(impressionBarEntries, "Impression Count");
                    BarData impressionData = new BarData(impressionDataSet);
                    setBarAdvanced(time, impressionBar, impressionData);
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