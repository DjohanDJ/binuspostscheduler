package com.example.binuspostscheduler.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.example.binuspostscheduler.activities.UpdateScheduleActivity;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Vector;

public class DashboardFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView allSchedule;

    private int filterIndex = 0;
    Spinner spinner;

    TextView fbLike, fbComment, fbView, fbShare;
    TextView insLike, insComment, insSave, insReach, insImpression;
    TextView tRetweet, tReply, tLike,tQuote, tView, tLinkClick, tProfileClick;
    TextView fbDash, tDash, insDash;
    Context ctx ;

    TableLayout tlFacebook, tlInstagram, tlTwitter;
    Vector<Vector<String>> listFacebookDashboard = new Vector<>();
    Vector<Vector<String>> listInstagramDashboard = new Vector<>();
    Vector<Vector<String>> listTwitterDashboard = new Vector<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);

        ctx = root.getContext();
        init(root);


        return root;
    }

    private void init(View view){
//        fbLike = view.findViewById(R.id.facebookMostLike);
//        fbComment = view.findViewById(R.id.facebookMostComment);
//        fbView = view.findViewById(R.id.facebookMostView);
//        fbShare = view.findViewById(R.id.facebookMostShare);
//
//        insLike = view.findViewById(R.id.instagramMostLike);
//        insComment = view.findViewById(R.id.instagramMostComment);
//        insSave = view.findViewById(R.id.instagramMostSave);
//        insReach = view.findViewById(R.id.instagramMostReach);
//        insImpression = view.findViewById(R.id.instagramMostImpression);
//
//        tRetweet = view.findViewById(R.id.twitterMostRetweet);
//        tReply = view.findViewById(R.id.twitterMostReply);
//        tLike = view.findViewById(R.id.twitterMostLike);
//        tQuote = view.findViewById(R.id.twitterMostQuoteTweet);
//        tView = view.findViewById(R.id.twitterMostView);
//        tLinkClick = view.findViewById(R.id.twitterMostLinkClick);
//        tProfileClick = view.findViewById(R.id.twitterMostProfileClick);
//
//        fbDash = view.findViewById(R.id.viewDashboardFB);
//        tDash = view.findViewById(R.id.viewDashboardTwitter);
//        insDash = view.findViewById(R.id.viewDashboardInstagram);
//
//        spinner = view.findViewById(R.id.filterSpinner);
//        this.spinner.setAdapter(new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item,
//                    getResources().getStringArray(R.array.filterDay)));

//
//        fbDash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(view.getContext(), FacebookDashboardChart.class);
//                in.putExtra("idx", filterIndex);
//                startActivity(in);
//            }
//        });
//
//        tDash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(view.getContext(), TwitterDashboardChart.class);
//                in.putExtra("idx", filterIndex);
//                startActivity(in);
//            }
//        });
//
//        insDash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent in = new Intent(view.getContext(), InstagramDashboardChart.class);
//                in.putExtra("idx", filterIndex);
//                startActivity(in);
//            }
//        });

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                facebookAnalytic(position);
//                instagramAnalytic(position);
//                twitterAnalytic(position);
//                filterIndex = position;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        facebookAnalytic(0);
//        instagramAnalytic(0);
//        twitterAnalytic(0);


//        new Dashboard ----------------------------------------------------------------------------------

        for(int i = 0; i <= 7; i++){
            Vector<String> hour1 = new Vector<>();
            Vector<String> hour2 = new Vector<>();
            Vector<String> hour3 = new Vector<>();
            for(int j = 0; j < 24; j++){
                hour1.add("");
                hour2.add("");
                hour3.add("");
            }
            listFacebookDashboard.add(hour1);
            listInstagramDashboard.add(hour2);
            listTwitterDashboard.add(hour3);
        }

        //FACEBOOK TABLE
        tlFacebook = view.findViewById(R.id.facebookTable);
        facebookAnalyticSetting();

        //INSTAGRAM TABLE
        tlInstagram = view.findViewById(R.id.instagramTable);
        instagramAnalyticSetting();

        //TWITTER TABLE
        tlTwitter = view.findViewById(R.id.twitterTable);
        twitterAnalyticSetting();


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
    }

    private void addRowToFacebookTable(){
        for(int i = 0; i < 24; i++){
            TableRow tr = new TableRow(ctx);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(Color.GRAY);
            for(int j = 0; j <= 7; j++){
                if(j==0){
                    TextView textView = new TextView(ctx);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(2,0,1,1);
                    if(i == 0){
                        params.setMargins(2,2,1,1);
                    }
                    if(i == 23){
                        params.setMargins(2,0,1,2);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setText(String.valueOf(i) + ".00");
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundColor(Color.rgb(210, 252, 239));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);
                }else{
                    TextView textView = new TextView(ctx);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setText(listFacebookDashboard.get(j).get(i));
                    if(!listFacebookDashboard.get(j).get(i).equals("")){
                        textView.setBackgroundColor(Color.rgb(222, 228, 255));
                    }
                    textView.setGravity(Gravity.CENTER);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(0,0,1,1);

                    if(j == 7){
                        params.setMargins(0,0,0,1);
                    }
                    if(i == 23){
                        params.setMargins(0,0,1,2);
                    }
                    if(i == 0){
                        params.setMargins(0,2,1,1);
                    }
                    if(i == 0 && j==7){
                        params.setMargins(0,2,0,1);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);

                }

            }
            tlFacebook.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        };

    }

    private void addRowToInstagramTable(){
        for(int i = 0; i < 24; i++){
            TableRow tr = new TableRow(ctx);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(Color.GRAY);
            for(int j = 0; j <= 7; j++){
                if(j==0){
                    TextView textView = new TextView(ctx);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(2,0,1,1);
                    if(i == 0){
                        params.setMargins(2,2,1,1);
                    }
                    if(i == 23){
                        params.setMargins(2,0,1,2);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setText(String.valueOf(i) + ".00");
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundColor(Color.rgb(210, 252, 239));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);
                }else{
                    TextView textView = new TextView(ctx);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setText(listInstagramDashboard.get(j).get(i));
                    if(!listInstagramDashboard.get(j).get(i).equals("")){
                        textView.setBackgroundColor(Color.rgb(222, 228, 255));
                    }
                    textView.setGravity(Gravity.CENTER);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(0,0,1,1);

                    if(j == 7){
                        params.setMargins(0,0,0,1);
                    }
                    if(i == 23){
                        params.setMargins(0,0,1,2);
                    }
                    if(i == 0){
                        params.setMargins(0,2,1,1);
                    }
                    if(i == 0 && j==7){
                        params.setMargins(0,2,0,1);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);

                }

            }
            tlInstagram.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        };
    }

    private void addRowToTwitterTable(){
        for(int i = 0; i < 24; i++){
            TableRow tr = new TableRow(ctx);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(Color.GRAY);
            for(int j = 0; j <= 7; j++){
                if(j==0){
                    TextView textView = new TextView(ctx);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(2,0,1,1);
                    if(i == 0){
                        params.setMargins(2,2,1,1);
                    }
                    if(i == 23){
                        params.setMargins(2,0,1,2);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setText(String.valueOf(i) + ".00");
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackgroundColor(Color.rgb(210, 252, 239));
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);
                }else{
                    TextView textView = new TextView(ctx);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setText(listTwitterDashboard.get(j).get(i));
                    if(!listTwitterDashboard.get(j).get(i).equals("")){
                        textView.setBackgroundColor(Color.rgb(222, 228, 255));
                    }
                    textView.setGravity(Gravity.CENTER);
                    TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                    params.setMargins(0,0,1,1);

                    if(j == 7){
                        params.setMargins(0,0,0,1);
                    }
                    if(i == 23){
                        params.setMargins(0,0,1,2);
                    }
                    if(i == 0){
                        params.setMargins(0,2,1,1);
                    }
                    if(i == 0 && j==7){
                        params.setMargins(0,2,0,1);
                    }
                    textView.setTextSize(10);
                    textView.setLayoutParams(params);
                    textView.setTextColor(getResources().getColor(R.color.colorBlack));
                    textView.setPadding(3,3,3,3);
                    tr.addView(textView);

                }

            }
            tlTwitter.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        };
    }

    private void facebookAnalyticSetting(){


        db.collection("dashboard").document("facebook").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(int day = 1; day <= 7; day++){
                        int[] like = new int[25];
                        int[] comment = new int[25];
                        int[] share = new int[25];
                        int[] view = new int[25];
                        int[] count = new int[25];
                        for(QueryDocumentSnapshot ds : task.getResult()){
                            String time = ds.getString("created_time");
                            String dateSplit[] = time.split(" ");
                            String timeSplit[] = dateSplit[1].split(":");
                            String cdateSplit[] = dateSplit[0].split("-");

                            int idx = Integer.parseInt(timeSplit[0]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                            int hari = cal.get(Calendar.DAY_OF_WEEK);

                            if(hari == day || day == 0){
                                share[idx] += Integer.parseInt(ds.getLong("share").toString());
                                view[idx] += Integer.parseInt(ds.getLong("view").toString());
                                like[idx] += Integer.parseInt(ds.getLong("like").toString());
                                comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                                count[idx]++;
                            }
                        }
                        int maxLikeIdx = 0;
                        int maxLike = 0;
                        int maxCommentIdx = 0;
                        int maxComment = 0;
                        int maxShareIdx = 0;
                        int maxShare = 0;
                        int maxViewIdx = 0;
                        int maxView = 0;
                        for(int i = 0; i < 24; i++){
                            if(count[i] != 0){
                                int currLike = like[i] / count[i];
                                int currComment = comment[i] / count[i];
                                int currShare = share[i] / count[i];
                                int currView = view[i] / count[i];
                                if(maxLike < currLike){
                                    maxLike = currLike;
                                    maxLikeIdx = i;
                                }
                                if(maxComment < currComment){
                                    maxComment = currLike;
                                    maxCommentIdx = i;
                                }
                                if(maxShare < currShare){
                                    maxShare = currShare;
                                    maxShareIdx = i;
                                }
                                if(maxView < currView){
                                    maxView = currView;
                                    maxViewIdx = i;
                                }
                            }
                        }
                        if(checkNeedEnter(listFacebookDashboard.get(day).get(maxViewIdx), 2)){
                            listFacebookDashboard.get(day).set(maxViewIdx,  listFacebookDashboard.get(day).get(maxViewIdx) + "\nV ");
                        }else listFacebookDashboard.get(day).set(maxViewIdx,  listFacebookDashboard.get(day).get(maxViewIdx) + "V ");

                        if(checkNeedEnter(listFacebookDashboard.get(day).get(maxShareIdx), 2)){
                            listFacebookDashboard.get(day).set(maxShareIdx,  listFacebookDashboard.get(day).get(maxShareIdx) + "\nS ");
                        }else listFacebookDashboard.get(day).set(maxShareIdx,  listFacebookDashboard.get(day).get(maxShareIdx) + "S ");

                        if(checkNeedEnter(listFacebookDashboard.get(day).get(maxLikeIdx), 2)){
                            listFacebookDashboard.get(day).set(maxLikeIdx,  listFacebookDashboard.get(day).get(maxLikeIdx) + "\nL ");
                        }else listFacebookDashboard.get(day).set(maxLikeIdx,  listFacebookDashboard.get(day).get(maxLikeIdx) + "L ");

                        if(checkNeedEnter(listFacebookDashboard.get(day).get(maxCommentIdx), 2)){
                            listFacebookDashboard.get(day).set(maxCommentIdx,  listFacebookDashboard.get(day).get(maxCommentIdx) + "\nC ");
                        }else listFacebookDashboard.get(day).set(maxCommentIdx,  listFacebookDashboard.get(day).get(maxCommentIdx) + "C ");
                    }
                    addRowToFacebookTable();
                }
            }
        });
    }

    private void instagramAnalyticSetting(){


        db.collection("dashboard").document("instagram").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(int day = 1; day <= 7; day++) {
                        int[] like = new int[25];
                        int[] comment = new int[25];
                        int[] reach = new int[25];
                        int[] save = new int[25];
                        int[] impression = new int[25];
                        int[] count = new int[25];
                        for (QueryDocumentSnapshot ds : task.getResult()) {
                            String time = ds.getString("created_time");
                            String dateSplit[] = time.split(" ");
                            String timeSplit[] = dateSplit[1].split(":");
                            String cdateSplit[] = dateSplit[0].split("-");

                            int idx = Integer.parseInt(timeSplit[0]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                            int hari = cal.get(Calendar.DAY_OF_WEEK);

                            if (hari == day || day == 0) {
                                like[idx] += Integer.parseInt(ds.getLong("like").toString());
                                comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                                save[idx] += Integer.parseInt(ds.getLong("save").toString());
                                reach[idx] += Integer.parseInt(ds.getLong("reach").toString());
                                impression[idx] += Integer.parseInt(ds.getLong("impression").toString());
                                count[idx]++;
                            }
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
                        for (int i = 0; i < 24; i++) {
                            if (count[i] != 0) {
                                int currLike = like[i] / count[i];
                                int currComment = comment[i] / count[i];
                                int currSave = comment[i] / count[i];
                                int currReach = comment[i] / count[i];
                                int currImp = comment[i] / count[i];

                                if (maxLike < currLike) {
                                    maxLike = currLike;
                                    maxLikeIdx = i;
                                }
                                if (maxComment < currComment) {
                                    maxComment = currLike;
                                    maxCommentIdx = i;
                                }
                                if (maxSave < currSave) {
                                    maxSave = currSave;
                                    maxSaveIdx = i;
                                }
                                if (maxReach < currReach) {
                                    maxReach = currReach;
                                    maxReachIdx = i;
                                }
                                if (maxImp < currImp) {
                                    maxImp = currImp;
                                    maxImpIdx = i;
                                }
                            }
                        }

                        if(checkNeedEnter(listInstagramDashboard.get(day).get(maxLikeIdx), 2)){
                            listInstagramDashboard.get(day).set(maxLikeIdx,  listInstagramDashboard.get(day).get(maxLikeIdx) + "\nL ");
                        }else listInstagramDashboard.get(day).set(maxLikeIdx,  listInstagramDashboard.get(day).get(maxLikeIdx) + "L ");

                        if(checkNeedEnter(listInstagramDashboard.get(day).get(maxCommentIdx), 2)){
                            listInstagramDashboard.get(day).set(maxCommentIdx,  listInstagramDashboard.get(day).get(maxCommentIdx) + "\nC ");
                        }else listInstagramDashboard.get(day).set(maxCommentIdx,  listInstagramDashboard.get(day).get(maxCommentIdx) + "C ");

                        if(checkNeedEnter(listInstagramDashboard.get(day).get(maxSaveIdx), 2)){
                            listInstagramDashboard.get(day).set(maxSaveIdx,  listInstagramDashboard.get(day).get(maxSaveIdx) + "\nS ");
                        }else listInstagramDashboard.get(day).set(maxSaveIdx,  listInstagramDashboard.get(day).get(maxSaveIdx) + "S ");

                        if(checkNeedEnter(listInstagramDashboard.get(day).get(maxReachIdx), 2)){
                            listInstagramDashboard.get(day).set(maxReachIdx,  listInstagramDashboard.get(day).get(maxReachIdx) + "\nR ");
                        }else listInstagramDashboard.get(day).set(maxReachIdx,  listInstagramDashboard.get(day).get(maxReachIdx) + "R ");

                        if(checkNeedEnter(listInstagramDashboard.get(day).get(maxImpIdx), 2)){
                            listInstagramDashboard.get(day).set(maxImpIdx,  listInstagramDashboard.get(day).get(maxImpIdx) + "\nI ");
                        }else listInstagramDashboard.get(day).set(maxImpIdx,  listInstagramDashboard.get(day).get(maxImpIdx) + "I ");
                    }
                    addRowToInstagramTable();
                }
            }
        });
    }

    private void twitterAnalyticSetting(){


        db.collection("dashboard").document("twitter").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(int day = 1; day <= 7; day++) {
                        int[] retweet = new int[25];
                        int[] reply = new int[25];
                        int[] like = new int[25];
                        int[] quote = new int[25];
                        int[] view = new int[25];
                        int[] link = new int[25];
                        int[] profile = new int[25];
                        int[] count = new int[25];
                        for (QueryDocumentSnapshot ds : task.getResult()) {
                            String time = ds.getString("created_time");
                            String dateSplit[] = time.split(" ");
                            String timeSplit[] = dateSplit[1].split(":");
                            String cdateSplit[] = dateSplit[0].split("-");

                            int idx = Integer.parseInt(timeSplit[0]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                            int hari = cal.get(Calendar.DAY_OF_WEEK);

                            if (hari == day || day == 0) {
                                retweet[idx] += Integer.parseInt(ds.getLong("retweet").toString());
                                reply[idx] += Integer.parseInt(ds.getLong("reply").toString());
                                like[idx] += Integer.parseInt(ds.getLong("like").toString());
                                quote[idx] += Integer.parseInt(ds.getLong("quote").toString());
                                view[idx] += Integer.parseInt(ds.getLong("view").toString());
                                link[idx] += Integer.parseInt(ds.getLong("link_click").toString());
                                profile[idx] += Integer.parseInt(ds.getLong("profile_click").toString());
                                count[idx]++;
                            }
                        }
                        int maxRetweetIdx = 0;
                        int maxRetweet = 0;
                        int maxReplytIdx = 0;
                        int maxReply = 0;
                        int maxLikeIdx = 0;
                        int maxLike = 0;
                        int maxQuoteIdx = 0;
                        int maxQuote = 0;
                        int maxViewIdx = 0;
                        int maxView = 0;
                        int maxLinkIdx = 0;
                        int maxLink = 0;
                        int maxProfileIdx = 0;
                        int maxProfile = 0;
                        for (int i = 0; i < 24; i++) {
                            if (count[i] != 0) {
                                int currRetweet = retweet[i] / count[i];
                                int currReply = reply[i] / count[i];
                                int currLike = like[i] / count[i];
                                int currQuote = quote[i] / count[i];
                                int currView = view[i] / count[i];
                                int currLink = link[i] / count[i];
                                int currProfile = profile[i] / count[i];


                                if (maxRetweet < currRetweet) {
                                    maxRetweet = currRetweet;
                                    maxRetweetIdx = i;
                                }
                                if (maxReply < currReply) {
                                    maxReply = currReply;
                                    maxReplytIdx = i;
                                }
                                if (maxLike < currLike) {
                                    maxLike = currLike;
                                    maxLikeIdx = i;
                                }
                                if (maxQuote < currQuote) {
                                    maxQuote = currQuote;
                                    maxQuoteIdx = i;
                                }
                                if (maxView < currView) {
                                    maxView = currView;
                                    maxViewIdx = i;
                                }
                                if (maxLink < currLink) {
                                    maxLink = currLink;
                                    maxLinkIdx = i;
                                }
                                if (maxProfile < currProfile) {
                                    maxProfile = currProfile;
                                    maxProfileIdx = i;
                                }
                            }
                        }

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxRetweetIdx), 3)){
                            listTwitterDashboard.get(day).set(maxRetweetIdx,  listTwitterDashboard.get(day).get(maxRetweetIdx) + "\nRt ");
                        }else listTwitterDashboard.get(day).set(maxRetweetIdx,  listTwitterDashboard.get(day).get(maxRetweetIdx) + "Rt ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxReplytIdx), 3)){
                            listTwitterDashboard.get(day).set(maxReplytIdx,  listTwitterDashboard.get(day).get(maxReplytIdx) + "\nRy ");
                        }else listTwitterDashboard.get(day).set(maxReplytIdx,  listTwitterDashboard.get(day).get(maxReplytIdx) + "Ry ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxLikeIdx), 3)){
                            listTwitterDashboard.get(day).set(maxLikeIdx,  listTwitterDashboard.get(day).get(maxLikeIdx) + "\nLk ");
                        }else listTwitterDashboard.get(day).set(maxLikeIdx,  listTwitterDashboard.get(day).get(maxLikeIdx) + "Lk ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxQuoteIdx), 2)){
                            listTwitterDashboard.get(day).set(maxQuoteIdx,  listTwitterDashboard.get(day).get(maxQuoteIdx) + "\nQ ");
                        }else listTwitterDashboard.get(day).set(maxQuoteIdx,  listTwitterDashboard.get(day).get(maxQuoteIdx) + "Q ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxViewIdx), 2)){
                            listTwitterDashboard.get(day).set(maxViewIdx,  listTwitterDashboard.get(day).get(maxViewIdx) + "\nV ");
                        }else listTwitterDashboard.get(day).set(maxViewIdx,  listTwitterDashboard.get(day).get(maxViewIdx) + "V ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxLinkIdx), 3)){
                            listTwitterDashboard.get(day).set(maxLinkIdx,  listTwitterDashboard.get(day).get(maxLinkIdx) + "\nLi ");
                        }else listTwitterDashboard.get(day).set(maxLinkIdx,  listTwitterDashboard.get(day).get(maxLinkIdx) + "Li ");

                        if(checkNeedEnter(listTwitterDashboard.get(day).get(maxProfileIdx), 2)){
                            listTwitterDashboard.get(day).set(maxProfileIdx,  listTwitterDashboard.get(day).get(maxProfileIdx) + "\nP ");
                        }else listTwitterDashboard.get(day).set(maxProfileIdx,  listTwitterDashboard.get(day).get(maxProfileIdx) + "P ");

                    }
                    addRowToTwitterTable();
                }
            }
        });
    }

    private boolean checkNeedEnter(String word, int plus){
        int len = word.length();

        if(word.contains("\n")){
            if(word.length() + plus > 13 && word.length() + plus < 16){
                return true;
            }
        }else if(len + plus > 6){
            return true;
        }
         return false;
    }

    private void facebookAnalytic(int day){
        int[] like = new int[25];
        int[] comment = new int[25];
        int[] share = new int[25];
        int[] view = new int[25];
        int[] count = new int[25];

        db.collection("dashboard").document("facebook").collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot ds : task.getResult()){
                        String time = ds.getString("created_time");
                        String dateSplit[] = time.split(" ");
                        String timeSplit[] = dateSplit[1].split(":");
                        String cdateSplit[] = dateSplit[0].split("-");

                        int idx = Integer.parseInt(timeSplit[0]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                        int hari = cal.get(Calendar.DAY_OF_WEEK);

                        if(hari == day || day == 0){
                            share[idx] += Integer.parseInt(ds.getLong("share").toString());
                            view[idx] += Integer.parseInt(ds.getLong("view").toString());
                            like[idx] += Integer.parseInt(ds.getLong("like").toString());
                            comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                            count[idx]++;
                        }
                    }
                    int maxLikeIdx = 0;
                    int maxLike = 0;
                    int maxCommentIdx = 0;
                    int maxComment = 0;
                    int maxShareIdx = 0;
                    int maxShare = 0;
                    int maxViewIdx = 0;
                    int maxView = 0;
                    for(int i = 0; i < 24; i++){
                        if(count[i] != 0){
                            int currLike = like[i] / count[i];
                            int currComment = comment[i] / count[i];
                            int currShare = share[i] / count[i];
                            int currView = view[i] / count[i];
                            if(maxLike < currLike){
                                maxLike = currLike;
                                maxLikeIdx = i;
                            }
                            if(maxComment < currComment){
                                maxComment = currLike;
                                maxCommentIdx = i;
                            }
                            if(maxShare < currShare){
                                maxShare = currShare;
                                maxShareIdx = i;
                            }
                            if(maxView < currView){
                                maxView = currView;
                                maxViewIdx = i;
                            }
                        }
                    }
                    fbView.setText(maxViewIdx + ".00 - " + maxViewIdx+ ".59");
                    fbShare.setText(maxShareIdx + ".00 - " + maxShareIdx+ ".59");
                    fbLike.setText(maxLikeIdx + ".00 - " + maxLikeIdx+ ".59");
                    fbComment.setText(maxCommentIdx + ".00 - " + maxCommentIdx+ ".59");
                }
            }
        });
    }

    private void instagramAnalytic(int day){
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
                        String dateSplit[] = time.split(" ");
                        String timeSplit[] = dateSplit[1].split(":");
                        String cdateSplit[] = dateSplit[0].split("-");

                        int idx = Integer.parseInt(timeSplit[0]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                        int hari = cal.get(Calendar.DAY_OF_WEEK);

                        if(hari == day || day == 0) {
                            like[idx] += Integer.parseInt(ds.getLong("like").toString());
                            comment[idx] += Integer.parseInt(ds.getLong("comment").toString());
                            save[idx] += Integer.parseInt(ds.getLong("save").toString());
                            reach[idx] += Integer.parseInt(ds.getLong("reach").toString());
                            impression[idx] += Integer.parseInt(ds.getLong("impression").toString());
                            count[idx]++;
                        }
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

    private void twitterAnalytic(int day){
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
                        String dateSplit[] = time.split(" ");
                        String timeSplit[] = dateSplit[1].split(":");
                        String cdateSplit[] = dateSplit[0].split("-");

                        int idx = Integer.parseInt(timeSplit[0]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(cdateSplit[2]), Integer.parseInt(cdateSplit[1]), Integer.parseInt(cdateSplit[0]));
                        int hari = cal.get(Calendar.DAY_OF_WEEK);

                        if(hari == day || day == 0) {
                            retweet[idx] += Integer.parseInt(ds.getLong("retweet").toString());
                            reply[idx] += Integer.parseInt(ds.getLong("reply").toString());
                            like[idx] += Integer.parseInt(ds.getLong("like").toString());
                            quote[idx] += Integer.parseInt(ds.getLong("quote").toString());
                            view[idx] += Integer.parseInt(ds.getLong("view").toString());
                            link[idx] += Integer.parseInt(ds.getLong("link_click").toString());
                            profile[idx] += Integer.parseInt(ds.getLong("profile_click").toString());
                            count[idx]++;
                        }
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
            int view= rand.nextInt(300);

            int n = rand.nextInt(view);
            n += 1;

            int x = rand.nextInt(view);
            x += 1;


            int share = rand.nextInt(view);

            int jam = rand.nextInt(24);
            int menit = rand.nextInt(60);
            int day = rand.nextInt(28) + 1;
            int month = rand.nextInt(12) + 1;
            int year = 2022;
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = day+ "-" + month+ "-" + year +" "+hour + ":" + minute;

            Map<String, Object> map = new HashMap<>();
            map.put("comment", x);
            map.put("like", n);
            map.put("view", view);
            map.put("share", share);
            map.put("created_time", time);

            db.collection("dashboard").document("facebook").collection("post").document().set(map);
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
            int day = rand.nextInt(28) + 1;
            int month = rand.nextInt(12) + 1;
            int year = 2022;
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = day+ "-" + month+ "-" + year +" "+hour + ":" + minute;

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

            int r = rand.nextInt(v);
            r += 1;

            int rep = rand.nextInt(v);
            rep += 1;

            int like = rand.nextInt(v);
            int quoteTweet = rand.nextInt(v);

            int lClick = rand.nextInt(v);
            int pClick = rand.nextInt(v);


            int jam = rand.nextInt(24);
            int menit = rand.nextInt(60);
            int day = rand.nextInt(28) + 1;
            int month = rand.nextInt(12) + 1;
            int year = 2022;
            String hour = null, minute = null;

            if(jam < 10){
                hour= "0" + String.valueOf(jam);
            }else hour = String.valueOf(jam);
            if(menit < 10){
                minute = "0" + String.valueOf(menit);
            }else minute = String.valueOf(menit);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String time = day+ "-" + month+ "-" + year +" "+hour + ":" + minute;

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