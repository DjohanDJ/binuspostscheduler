package com.example.binuspostscheduler.ui.schedules;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class SchedulesFragment extends Fragment {

    private RecyclerView allSchedule;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedules, container, false);
//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        init(root);
        setAdapter(root.getContext());

        return root;
    }

    public void init(View root){
        allSchedule = root.findViewById(R.id.allScheduleList);
    }

    public void setAdapter(final Context ctx){
        final ArrayList<PostedSchedule> postedListRaw = new ArrayList<>();
        final ArrayList<PostedSchedule> postedList = new ArrayList<>();

        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postedListRaw.clear();
                    postedList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        PostedSchedule postedSchedule = documentSnapshot.toObject(PostedSchedule.class);
                        postedListRaw.add(postedSchedule);
                    }

                    for(PostedSchedule post : postedListRaw){
                        if(post.getUser_id().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            postedList.add(post);
                        }
                    }

                    Collections.sort(postedList, new Comparator<PostedSchedule>() {
                        @Override
                        public int compare(PostedSchedule o1, PostedSchedule o2) {

                            Date date = null, date2 = null;
                            try {
                                date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(o1.getTime());
                                date2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(o2.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Timestamp ts1 = new Timestamp(date.getTime());
                            Timestamp ts2 = new Timestamp(date2.getTime());

                            return ts1.compareTo(ts2);
                        }
                    });




//                    Toast.makeText(ctx, postedList.get(0).getTime(), Toast.LENGTH_SHORT).show();

                    AllScheduleAdapter comAdapter = new AllScheduleAdapter(ctx, postedList);
                    allSchedule.setAdapter(comAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    allSchedule.setLayoutManager(layoutManager);
                }
            }
        });



    }
}