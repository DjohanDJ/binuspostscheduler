package com.example.binuspostscheduler.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.binuspostscheduler.activities.MainActivity;
import com.example.binuspostscheduler.activities.RegisterActivity;
import com.example.binuspostscheduler.activities.ScheduleDetailActivity;
import com.example.binuspostscheduler.activities.UpdateScheduleActivity;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.example.binuspostscheduler.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

                        pDate.setTime(pDate.getTime() - (1000 *60));
                        postDate = dFormat.format(pDate);

                        if(today.equalsIgnoreCase(postDate)){
                            sendNotif(context, post);
                            if (post.getType().equalsIgnoreCase("daily")){
                                setDaily(post, context);
                            }else if(post.getType().equalsIgnoreCase("weekly")){
                                setWeekly(post, context);
                            }else if(post.getType().equalsIgnoreCase("monthly")){
                                setMonthly(post, context);
                            }
                            break;
                        }
                    }
                }
            }
        });

    }

    void setDaily(PostedSchedule post , Context context){

        SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        long oneDay = 1000 * 60 * 60 * 24;

        Date newDate = null;
        try {
            newDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(post.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newDate.setTime(newDate.getTime() + oneDay);

        String newestDate = dFormat.format(newDate);

        post.setTime(newestDate);

        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .document(post.getId()).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });;

    }

    void setWeekly(PostedSchedule post, Context context){
        SimpleDateFormat dFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        long sevenDay = 1000 * 60 * 60 * 24 * 7;

        Date newDate = null;
        try {
            newDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(post.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        newDate.setTime(newDate.getTime() + sevenDay);

        String newestDate = dFormat.format(newDate);
        post.setTime(newestDate);
        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .document(post.getId()).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                    }
                });;
    }

    void setMonthly(PostedSchedule post, Context context){
        String splitDateTime[] = post.getTime().split(" ");
        String splitDate[] = splitDateTime[0].split("-");

        if(splitDate[1].equalsIgnoreCase("12")){
            splitDate[1] = "1";
            int year = Integer.parseInt(splitDate[2]) + 1;
            splitDate[2] = String.valueOf(year);
        }else{
            int month = Integer.parseInt(splitDate[1]) + 1;
            splitDate[1] = String.valueOf(month);
        }

        String newDate = splitDate[0] + "-"+ splitDate[1] + "-" + splitDate[2] + " " + splitDateTime[1];
        post.setTime(newDate);
        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .document(post.getId()).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });;

    }


    void sendNotif(Context context, PostedSchedule post){
        Intent myIntent = new Intent(context, ScheduleDetailActivity.class);
        myIntent.putExtra("id", post.getId());
        myIntent.putExtra("description", post.getDescription());
        myIntent.putExtra("image", post.getImage());
        myIntent.putExtra("hashtags", post.getHashtags());
        myIntent.putExtra("video", post.getVideo());
        myIntent.putExtra("time", post.getTime());
        myIntent.putExtra("selected_id", post.getSelected_id());
        myIntent.putExtra("type", post.getType());
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        //buka paksa
        context.startActivity(myIntent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifChannel").setSmallIcon(R.drawable.messenger_bubble_large_blue)
                .setContentTitle("Posting Reminder")
                .setContentText("Time to post the content")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

        notifManager.notify(200, builder.build());
    }
}
