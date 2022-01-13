package com.example.binuspostscheduler.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    TextView fbLike, fbComment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dashboard_fragment, container, false);


        init(root);

        return root;
    }

    private void init(View view){
        fbLike = view.findViewById(R.id.facebookMostLike);
        fbComment = view.findViewById(R.id.facebookMostComment);
        facebookAnalytic();
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

    private void create20DummyData(){
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

            db.collection("dashboard").document("facebook").collection("post").document().set(map);
        }
    }

}