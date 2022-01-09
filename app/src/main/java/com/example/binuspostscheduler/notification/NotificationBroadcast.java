package com.example.binuspostscheduler.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.binuspostscheduler.models.FacebookPages;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.example.binuspostscheduler.ui.home.HomeFragment;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationBroadcast extends BroadcastReceiver {
    final ArrayList<PostedSchedule> postedListRaw = new ArrayList<>();
    final ArrayList<PostedSchedule> postedList = new ArrayList<>();
    final ArrayList<FacebookPages> pages = new ArrayList<>();
    String user_id = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        user_id = intent.getStringExtra("user_id");
        checkSchdule(context);
        Toast.makeText(context, "HAIHAI", Toast.LENGTH_SHORT).show();
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
//                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
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



        // send notif
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
//        context.startActivity(myIntent);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifChannel").setSmallIcon(R.drawable.messenger_bubble_large_blue)
                .setContentTitle("Posting Reminder")
                .setContentText("Time to post the content")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

        notifManager.notify(200, builder.build());

        // auto post
        setApiDatabase(post);
    }

    private void getDataPublish(PostedSchedule post){
        Log.d("HAIHAI", UserSession.getCurrentUser().getId().toString());
        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
          .document("facebook").collection("pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String accT, id, name,uid ;
                        accT= doc.getString("access_token");
                        id = doc.getString("id");
                        name = doc.getString("name");
                        uid = doc.getString("uid");
                        FacebookPages fp = new FacebookPages(accT,id,name,uid);
                        pages.add(fp);
                    }
                    publish(post);
            }
        });
    }

    private void publish(PostedSchedule post){
        AccessToken acc = new AccessToken(pages.get(0).getAccess_token(), "216327573669340", pages.get(0).getUid(),
                null, null, null,null, null, null, null, null);

        String desc = post.getDescription() + "\n\n";

        for(String hash : post.getHashtags()){
            desc += hash + " ";
        }

        Bundle params = new Bundle();
        params.putString("message", desc);
        params.putString("url", post.getImage().get(0));

//        int c = 0;
//        for(String url: post.getImage()){
//            params.putString("attached_media["+c+"]", url);
//            c++;
//        }

        new GraphRequest(
                acc,
                "/ "+ pages.get(0).getId() +"/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //success
                    }
                }
        ).executeAsync();
    }


    private void setApiDatabase(PostedSchedule post){
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String name = object.getString("name").toString();
                    String uid = object.getString("id").toString();

                    Map<String, Object> map = new HashMap<>();
                    map.put("uid", uid);
                    map.put("name", name);

                    db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts").document("facebook")
                            .set(map);

                    int len = object.getJSONObject("accounts").getJSONArray("data").length();
                    pages.clear();
                    for(int i = 0; i < len; i++) {
                        String a = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("access_token");
                        String b = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("id");
                        String c = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("name");

                        Map<String, Object> page = new HashMap<>();
                        page.put("access_token", a);
                        page.put("id", b);
                        page.put("name", c);
                        page.put("uid", uid);

                        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                                .document("facebook").collection("pages").document(b)
                                .set(page);

                        pages.add(new FacebookPages(a, b, c, uid));
                    }
                    getDataPublish(post);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name, accounts");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }
}
