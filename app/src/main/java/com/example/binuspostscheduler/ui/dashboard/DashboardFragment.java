package com.example.binuspostscheduler.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.binuspostscheduler.Adapter.AllScheduleAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.activities.FacebookDashboardChart;
import com.example.binuspostscheduler.activities.InstagramDashboardChart;
import com.example.binuspostscheduler.activities.TwitterDashboardChart;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class DashboardFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView allSchedule;

    TextView fbLike, fbComment,insLike, insComment, insSave, insReach, insImpression;
    TextView tRetweet, tReply, tLike,tQuote, tView, tLinkClick, tProfileClick;
    TextView fbDash, tDash, insDash;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);


        init(root);

        return root;
    }

    private void init(View view){
        fbLike = view.findViewById(R.id.facebookMostLike);
        fbComment = view.findViewById(R.id.facebookMostComment);

        insLike = view.findViewById(R.id.instagramMostLike);
        insComment = view.findViewById(R.id.instagramMostComment);
        insSave = view.findViewById(R.id.instagramMostSave);
        insReach = view.findViewById(R.id.instagramMostReach);
        insImpression = view.findViewById(R.id.instagramMostImpression);

        tRetweet = view.findViewById(R.id.twitterMostRetweet);
        tReply = view.findViewById(R.id.twitterMostReply);
        tLike = view.findViewById(R.id.twitterMostLike);
        tQuote = view.findViewById(R.id.twitterMostQuoteTweet);
        tView = view.findViewById(R.id.twitterMostView);
        tLinkClick = view.findViewById(R.id.twitterMostLinkClick);
        tProfileClick = view.findViewById(R.id.twitterMostProfileClick);

        fbDash = view.findViewById(R.id.viewDashboardFB);
        tDash = view.findViewById(R.id.viewDashboardTwitter);
        insDash = view.findViewById(R.id.viewDashboardInstagram);

        fbDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(view.getContext(), FacebookDashboardChart.class);
                startActivity(in);
            }
        });

        tDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(view.getContext(), TwitterDashboardChart.class);
                startActivity(in);
            }
        });

        insDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(view.getContext(), InstagramDashboardChart.class);
                startActivity(in);
            }
        });

        facebookAnalytic();
        instagramAnalytic();
        twitterAnalytic();
    }

    private void facebookAnalytic(){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] count = new int[25];

        db.collection("dashboard").document("facebook").collection("post")
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
                        count[idx]++;
                    }
                    int maxLikeIdx = 0;
                    int maxLike = 0;
                    int maxCommentIdx = 0;
                    int maxComment = 0;
                    for(int i = 0; i < 24; i++){
                        if(count[i] != 0){
                            int currLike = like[i] / count[i];
                            int currComment = comment[i] / count[i];
                            if(maxLike < currLike){
                                maxLike = currLike;
                                maxLikeIdx = i;
                            }
                            if(maxComment < currComment){
                                maxComment = currLike;
                                maxCommentIdx = i;
                            }
                        }
                    }

                    fbLike.setText(maxLikeIdx + ".00 - " + maxLikeIdx+ ".59");
                    fbComment.setText(maxCommentIdx + ".00 - " + maxCommentIdx+ ".59");
                }
            }
        });
    }

    private void instagramAnalytic(){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] reach = new int[25];
        int[] save = new int[25];
        int[] impression = new int[25];
        int[] count = new int[25];

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
                    int maxLikeIdx = 0;
                    int maxLike = 0;
                    int maxCommentIdx = 0;
                    int maxComment = 0;
                    int maxSaveIdx = 0;
                    int maxSave = 0;
                    int maxReachIdx = 0;
                    int maxReach = 0;
                    int maxImpIdx = 0;
                    int maxImp = 0;
                    for(int i = 0; i < 24; i++){
                        if(count[i] != 0){
                            int currLike = like[i] / count[i];
                            int currComment = comment[i] / count[i];
                            int currSave = comment[i] / count[i];
                            int currReach = comment[i] / count[i];
                            int currImp = comment[i] / count[i];

                            if(maxLike < currLike){
                                maxLike = currLike;
                                maxLikeIdx = i;
                            }
                            if(maxComment < currComment){
                                maxComment = currLike;
                                maxCommentIdx = i;
                            }
                            if(maxSave < currSave){
                                maxSave = currSave;
                                maxSaveIdx = i;
                            }
                            if(maxReach < currReach){
                                maxReach = currReach;
                                maxReachIdx = i;
                            }
                            if(maxImp < currImp){
                                maxImp = currImp;
                                maxImpIdx = i;
                            }
                        }
                    }

                    insLike.setText(maxLikeIdx + ".00 - " + maxLikeIdx+ ".59");
                    insComment.setText(maxCommentIdx + ".00 - " + maxCommentIdx+ ".59");
                    insSave.setText(maxSaveIdx + ".00 - " + maxSaveIdx+ ".59");
                    insReach.setText(maxReachIdx + ".00 - " + maxReachIdx+ ".59");
                    insImpression.setText(maxImpIdx + ".00 - " + maxImpIdx+ ".59");

                }
            }
        });
    }

    private void twitterAnalytic(){
        int[] retweet = new int[25];
        int[] reply = new int[25];
        int[] like = new int[25];
        int[] quote = new int[25];
        int[] view = new int[25];
        int[] link = new int[25];
        int[] profile = new int[25];
        int[] count = new int[25];

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
                    int maxRetweetIdx = 0;
                    int maxRetweet = 0;
                    int maxReplytIdx = 0;
                    int maxReply = 0;
                    int maxLikeIdx = 0;
                    int maxLike = 0;
                    int maxQuotehIdx = 0;
                    int maxQuote = 0;
                    int maxViewIdx = 0;
                    int maxView = 0;
                    int maxLinkIdx = 0;
                    int maxLink = 0;
                    int maxProfileIdx = 0;
                    int maxProfile = 0;
                    for(int i = 0; i < 24; i++){
                        if(count[i] != 0){
                            int currRetweet = retweet[i] / count[i];
                            int currReply = reply[i] / count[i];
                            int currLike = like[i] / count[i];
                            int currQuote = quote[i] / count[i];
                            int currView = view[i] / count[i];
                            int currLink = link[i] / count[i];
                            int currProfile = profile[i] / count[i];


                            if(maxRetweet < currRetweet){
                                maxRetweet = currRetweet;
                                maxRetweetIdx = i;
                            }
                            if(maxReply < currReply){
                                maxReply = currReply;
                                maxReplytIdx = i;
                            }
                            if(maxLike < currLike){
                                maxLike = currLike;
                                maxLikeIdx = i;
                            }
                            if(maxQuote < currQuote){
                                maxQuote = currQuote;
                                maxQuotehIdx = i;
                            }
                            if(maxView < currView){
                                maxView = currView;
                                maxViewIdx = i;
                            }
                            if(maxLink < currLink){
                                maxLink = currLink;
                                maxLinkIdx = i;
                            }
                            if(maxProfile < currProfile){
                                maxProfile = currProfile;
                                maxProfileIdx = i;
                            }
                        }
                    }

                    tRetweet.setText(maxRetweetIdx + ".00 - " + maxRetweetIdx+ ".59");
                    tReply.setText(maxReplytIdx + ".00 - " + maxReplytIdx+ ".59");
                    tLike.setText(maxLikeIdx + ".00 - " + maxLikeIdx+ ".59");
                    tQuote.setText(maxQuotehIdx + ".00 - " + maxQuotehIdx+ ".59");
                    tView.setText(maxViewIdx + ".00 - " + maxViewIdx+ ".59");
                    tLinkClick.setText(maxLinkIdx + ".00 - " + maxLinkIdx+ ".59");
                    tProfileClick.setText(maxProfileIdx + ".00 - " + maxProfileIdx+ ".59");

                }
            }
        });
    }

    private void create20DummyDataFacebook(){
        Random rand = new Random();

        for(int i = 0; i < 20; i++){
            int n = rand.nextInt(200);
            n += 1;

            int x = rand.nextInt(n);
            x += 1;

            int jam = rand.nextInt(24);
            int menit = rand.nextInt(60);
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = hour + ":" + minute;

            Map<String, Object> map = new HashMap<>();
            map.put("comment", x);
            map.put("like", n);
            map.put("created_time", time);

            db.collection("dashboard").document("instagram").collection("post").document().set(map);
        }
    }

    private void create20DummyDataInstagram(){

        //total like, total komen, total org yg save, reach sm impression
        Random rand = new Random();

        for(int i = 0; i < 20; i++){
            int n = rand.nextInt(200);
            n += 1;

            int x = rand.nextInt(n/3);
            x += 1;

            int save = rand.nextInt(n/5);
            int reach = rand.nextInt(200);
            reach+= n;

            int impression = rand.nextInt(reach);


            int jam = rand.nextInt(24);
            int menit = rand.nextInt(60);
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = hour + ":" + minute;

            Map<String, Object> map = new HashMap<>();
            map.put("comment", x);
            map.put("like", n);
            map.put("save", save);
            map.put("reach", reach);
            map.put("impression", impression);
            map.put("created_time", time);

            db.collection("dashboard").document("instagram").collection("post").document().set(map);
        }
    }

    private void create20DummyDataTwitter(){

        //total like, total komen, total org yg save, reach sm impression
        Random rand = new Random();

        for(int i = 0; i < 20; i++){
            int v = rand.nextInt(300);
            v += 1;

            int r = rand.nextInt(v/10);
            r += 1;

            int rep = rand.nextInt(v/10);
            rep += 1;

            int like = rand.nextInt(v);
            int quoteTweet = rand.nextInt(v/10);

            int lClick = rand.nextInt(v/3);
            int pClick = rand.nextInt(v/10);


            int jam = rand.nextInt(24);
            int menit = rand.nextInt(60);
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = hour + ":" + minute;

            Map<String, Object> map = new HashMap<>();
            map.put("retweet", r);
            map.put("reply", rep);
            map.put("like", like);
            map.put("quote", quoteTweet);
            map.put("view", v);
            map.put("link_click", lClick);
            map.put("profile_click", pClick);
            map.put("created_time", time);

            db.collection("dashboard").document("twitter").collection("post").document().set(map);
        }
    }
}