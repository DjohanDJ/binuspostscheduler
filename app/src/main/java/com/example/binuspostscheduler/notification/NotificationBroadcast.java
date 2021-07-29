package com.example.binuspostscheduler.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.binuspostscheduler.Adapter.TodayScheduleAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.example.binuspostscheduler.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class NotificationBroadcast extends BroadcastReceiver {
    final ArrayList<PostedSchedule> postedListRaw = new ArrayList<>();
    final ArrayList<PostedSchedule> postedList = new ArrayList<>();
    String user_id = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        user_id = intent.getStringExtra("user_id");
        checkSchdule(context);
    }

    void checkSchdule(Context context){
        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    postedList.clear();
                    postedListRaw.clear();
                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        PostedSchedule postedSchedule = documentSnapshot.toObject(PostedSchedule.class);
                        postedListRaw.add(postedSchedule);
                    }



                    for(PostedSchedule post : postedListRaw){
                        if(post.getUser_id().equalsIgnoreCase(user_id)){
                            postedList.add(post);
                        }
                    }

                    SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
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

//                        Toast.makeText(context, today + "--" +postDate, Toast.LENGTH_SHORT).show();

                        if(today.equalsIgnoreCase(postDate)){
                            sendNotif(context);
                            break;
                        }
                    }
                }
            }
        });

    }


    void sendNotif(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifChannel").setSmallIcon(R.drawable.messenger_bubble_large_blue)
                .setContentTitle("Posting Reminder")
                .setContentText("Time to post the content")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

        notifManager.notify(200, builder.build());
    }
}
