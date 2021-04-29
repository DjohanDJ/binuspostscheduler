package com.example.binuspostscheduler.ui.home;

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

import com.example.binuspostscheduler.Adapter.TodayScheduleAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView currDate;
    private RecyclerView tsList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init(root);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy ");
        Date date = new Date();
        currDate.setText("Today, "+formatter.format(date));

        setAdapter(root.getContext());


        return root;
    }

    public void init(View root){
        currDate = root.findViewById(R.id.currDate);
        tsList = root.findViewById(R.id.todayScheduleList);
    }

    public void setAdapter(final Context ctx){
        final ArrayList<PostedSchedule> postedList = new ArrayList<>();
        final ArrayList<PostedSchedule> todayPostedList = new ArrayList<>();

        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("users")
                .document(UserSession.getCurrentUser().getId()).collection("schedule")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postedList.clear();
                    todayPostedList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        PostedSchedule postedSchedule = documentSnapshot.toObject(PostedSchedule.class);
                        postedList.add(postedSchedule);
                    }

                    SimpleDateFormat dFormat = new SimpleDateFormat("dd MMMM yyyy");
                    Date date = new Date();
                    String today = dFormat.format(date);
                    String postDate;

                    for(PostedSchedule post : postedList){
                        Date pDate = null;
                        try {
                            pDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(post.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        postDate = dFormat.format(pDate);

                        if(today.equalsIgnoreCase(postDate)){
                            todayPostedList.add(post);
                        }
                    }

//                    Toast.makeText(ctx, postedList.get(0).getTime(), Toast.LENGTH_SHORT).show();

                    TodayScheduleAdapter comAdapter = new TodayScheduleAdapter(ctx, todayPostedList);
                    tsList.setAdapter(comAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    tsList.setLayoutManager(layoutManager);
                }
            }
        });



    }

}