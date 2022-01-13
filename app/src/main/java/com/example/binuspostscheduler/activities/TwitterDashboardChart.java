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

public class TwitterDashboardChart extends AppCompatActivity {
    BarChart retweetBar, replyBar, likeBar, quoteBar, viewBar, linkBar, profileBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_dashboard_chart);

        retweetBar = findViewById(R.id.barTwitterRetweet);
        replyBar = findViewById(R.id.barTwitterReply);
        likeBar = findViewById(R.id.barTwitterLike);
        quoteBar = findViewById(R.id.barTwitterQuote);
        viewBar = findViewById(R.id.barTwitterView);
        linkBar = findViewById(R.id.barTwitterLink);
        profileBar = findViewById(R.id.barTwitterProfile);

        setBar();
    }

    private void setBar(){
        int[] retweet = new int[25];
        int[] reply = new int[25];
        int[] like = new int[25];
        int[] quote = new int[25];
        int[] view = new int[25];
        int[] link = new int[25];
        int[] profile = new int[25];
        int[] count = new int[25];
        ArrayList<BarEntry> retweetBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> replyBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> likeBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> quoteBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> viewBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> linkBarEntries = new ArrayList<BarEntry>();
        ArrayList<BarEntry> profileBarEntries = new ArrayList<BarEntry>();
        ArrayList<String> time = new ArrayList<>();

        db.collection("dashboard").document("twitter").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()){
                        String time = ds.getString("created_time");
                        String timeSplit[] = time.split(":");

                        int idx = Integer.parseInt(timeSplit[0]);

                        retweet[idx] += Integer.parseInt(ds.getLong("retweet").toString());
                        reply[idx] += Integer.parseInt(ds.getLong("reply").toString());
                        like[idx] += Integer.parseInt(ds.getLong("like").toString());
                        quote[idx] += Integer.parseInt(ds.getLong("quote").toString());
                        view[idx] += Integer.parseInt(ds.getLong("view").toString());
                        link[idx] += Integer.parseInt(ds.getLong("link_click").toString());
                        profile[idx] += Integer.parseInt(ds.getLong("profile_click").toString());
                        count[idx]++;
                    }

                    //looping get data for bar
                    for(int i = 0; i < 24; i++){
                        float a=0 , b=0, c=0, d=0, e=0, f=0, g=0;
                        if(count[i] != 0)  {
                            a = retweet[i] / count[i];
                            b = reply[i] / count[i];
                            c = like[i] / count[i];
                            d = quote[i] / count[i];
                            e = view[i] / count[i];
                            f = link[i] / count[i];
                            g = profile[i] / count[i];
                        }
                        retweetBarEntries.add(new BarEntry(i, a));
                        replyBarEntries.add(new BarEntry(i, b));
                        likeBarEntries.add(new BarEntry(i, c));
                        quoteBarEntries.add(new BarEntry(i, d));
                        viewBarEntries.add(new BarEntry(i, e));
                        linkBarEntries.add(new BarEntry(i, f));
                        profileBarEntries.add(new BarEntry(i, g));
                        String t = i+"-" + i +".59";
                        time.add(t);
                    }

                    //retweet Bar
                    BarDataSet retweetDataSet = new BarDataSet(retweetBarEntries, "Retweet Count");
                    BarData retweetData = new BarData(retweetDataSet);
                    setBarAdvanced(time, retweetBar, retweetData);

                    //reply Bar
                    BarDataSet replyDataSet = new BarDataSet(replyBarEntries, "Reply Count");
                    BarData replyData = new BarData(replyDataSet);
                    setBarAdvanced(time, replyBar, replyData);

                    //like Bar
                    BarDataSet likeDataSet = new BarDataSet(likeBarEntries, "Like Count");
                    BarData likeData = new BarData(likeDataSet);
                    setBarAdvanced(time, likeBar, likeData);

                    //quote bar
                    BarDataSet quoteDataSet = new BarDataSet(quoteBarEntries, "Quote Tweet Count");
                    BarData quoteData = new BarData(quoteDataSet);
                    setBarAdvanced(time, quoteBar, quoteData);

                    //view Bar
                    BarDataSet viewDataSet = new BarDataSet(viewBarEntries, "View Count");
                    BarData viewData = new BarData(viewDataSet);
                    setBarAdvanced(time, viewBar, viewData);

                    //link Bar
                    BarDataSet linkDataSet = new BarDataSet(linkBarEntries, "Link Click Count");
                    BarData linkData = new BarData(linkDataSet);
                    setBarAdvanced(time, linkBar, linkData);

                    //profile Bar
                    BarDataSet profileDataSet = new BarDataSet(profileBarEntries, "Profile Click Count");
                    BarData profileData = new BarData(profileDataSet);
                    setBarAdvanced(time, profileBar, profileData);
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