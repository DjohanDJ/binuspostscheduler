package com.example.binuspostscheduler.notification;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.binuspostscheduler.models.Account;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.conf.ConfigurationBuilder;

public class NotificationBroadcast extends BroadcastReceiver {
    final ArrayList<PostedSchedule> postedListRaw = new ArrayList<>();
    final ArrayList<PostedSchedule> postedList = new ArrayList<>();
    final ArrayList<FacebookPages> pages = new ArrayList<>();
    String user_id = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String instagramBId = "";
    Context ctx;
    @Override
    public void onReceive(Context context, Intent intent) {
        user_id = intent.getStringExtra("user_id");
        this.ctx = context;
        checkSchedule(context);
//        Toast.makeText(context, "HAIHAI", Toast.LENGTH_SHORT).show();
    }

    void checkSchedule(Context context){
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
                            Log.d("HAI", post.getSelected_id().toString());
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
        // auto post
        setApiDatabase(post);

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


        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifChannel").setSmallIcon(R.drawable.messenger_bubble_large_blue)
                .setContentTitle("Autopost")
                .setContentText("Your content has been published")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);

        notifManager.notify(200, builder.build());


    }

    private void getDataPublish(PostedSchedule post){
        db.collection("users").document(user_id).collection("accounts")
          .document("facebook").collection("pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String accT, id, name, uid , status;
                        accT= doc.getString("access_token");
                        id = doc.getString("id");
                        name = doc.getString("name");
                        uid = doc.getString("uid");
                        status = doc.getString("status");
                        FacebookPages fp = new FacebookPages(accT,id,name,uid);
                        fp.setStatus(status);
                        pages.add(fp);
                        //post facebook
                        if(status.equals("active")){
                            if(isFacebookPagesExists(post, id)){
                                publishFacebook(post,id, accT);
                            }
                        }
                    }

            }
        });

        // post to twitter
        if(isTwitterExists(post))
        {
            db.collection("users").document(post.getUser_id()).collection("accounts").document("twitter").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Account acc = documentSnapshot.toObject(Account.class);
                    postTwitter(acc,post);
                }
            });
        }

        //post instagram
        if(isInstagramExists(post)) {
            db.collection("users").document(user_id).collection("accounts").document("instagram")
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    instagramBId = documentSnapshot.get("id").toString();
                    publishInstagram(post);
                }
            });
        }
    }

    private void publishInstagram(PostedSchedule post) {
        AccessToken accessToken = new AccessToken(pages.get(0).getAccess_token(), "472685857272246", pages.get(0).getUid(),
                null, null, null,null, null, null, null, null);

        String desc = post.getDescription() + "\n\n.\n.\n.\n.\n";

        for(String hash : post.getHashtags()){
            desc += hash + " ";
        }

        Bundle params = new Bundle();
        params.putString("image_url", post.getImage().get(0));
        params.putString("caption", desc);

        Bundle paramPost = new Bundle();

        new GraphRequest(
                accessToken,
                "/"+ instagramBId +"/media",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject postObj = response.getJSONObject();
                        try {
                            String postId = postObj.getString("id");
                            paramPost.putString("creation_id", postId);
                            new GraphRequest(
                                    accessToken,
                                    "/"+ instagramBId +"/media_publish",
                                    paramPost,
                                    HttpMethod.POST,
                                    new GraphRequest.Callback() {
                                        public void onCompleted(GraphResponse response) {

                                        }
                                    }
                            ).executeAsync();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    private void publishFacebook(PostedSchedule post, String pages_id, String accT){
        AccessToken acc = new AccessToken(accT, "472685857272246", pages.get(0).getUid(),
                null, null, null,null, null, null, null, null);

        String desc = post.getDescription() + "\n\n";

        for(String hash : post.getHashtags()){
            desc += hash + " ";
        }

        Bundle params = new Bundle();
        params.putString("message", desc);
        params.putString("url", post.getImage().get(0));


        new GraphRequest(
                acc,
                "/ "+ pages_id +"/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //success
                    }
                }
        ).executeAsync();
    }

    private void getFacebookPages(String accToken, String id, String name, String uid, int index){
        pages.add(new FacebookPages(accToken, id, name, uid));

        db.collection("users").document(user_id).collection("accounts")
                .document("facebook").collection("pages").document("id").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot ds = task.getResult();
                    String status = ds.getString("status");
                    pages.get(index).setStatus(status);
                }
            }
        });

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

                    db.collection("users").document(user_id).collection("accounts").document("facebook")
                            .update(map);

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

                        db.collection("users").document(user_id).collection("accounts")
                                .document("facebook").collection("pages").document(b)
                                .update(page);

                        getFacebookPages(a,b,c,uid, i);
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

    private void postTwitter( Account account, PostedSchedule obj) {

        String access_token = account.getAccess_token();
        String access_secret = account.getAccess_secret();
        ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey(ctx.getString(R.string.twitter_CONSUMER_KEY));
        cb.setOAuthConsumerSecret(ctx.getString(R.string.twitter_CONSUMER_SECRET));
        twitter4j.auth.AccessToken twToken = new twitter4j.auth.AccessToken(access_token, access_secret);
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        Twitter twitter = twitterFactory.getInstance();
        twitter.setOAuthAccessToken(twToken);
        Long mediaIds[] = new Long[5];

        String desc = obj.getDescription() + "\n\n";

        for(String hash : obj.getHashtags()){
            desc += hash + " ";
        }

        StatusUpdate statusUpdate = new StatusUpdate(desc);
        File files[] = new File[5];
        for (int i = 0; i < obj.getImage().size(); i++) {
            String path = obj.getImage().get(i);
            int finalI = i;
            Picasso.get().load(path).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    File file = new File(ctx.getCacheDir(), "temp.jpg");
                    try {
                        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

                        if(file.exists())Log.d("File","EXISTS");
                        else Log.d("File","NOT EXISTS");
                        files[finalI] = file;

//                                os.close();
                        if (finalI == obj.getImage().size() - 1) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Status status = null;
                                    try {
                                        for(int i=0;i<obj.getImage().size();i++){
                                            Log.d("I","i ke "+i);
                                            File file = files[i];
                                            UploadedMedia upload = twitter.uploadMedia(file);
                                            mediaIds[i] = upload.getMediaId();
                                            statusUpdate.setMediaIds(upload.getMediaId());
                                            Log.d("I","i ke "+i+ " End");
                                        }
                                        status = twitter.updateStatus(statusUpdate);
                                    } catch (TwitterException e) {
                                        e.printStackTrace();
                                    }



                                }
                            });
                            thread.start();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }
    }

    private boolean isTwitterExists(PostedSchedule post){
        for(Account acc : post.getSelected_id())if(acc.getType().equals("twitter"))return true;
        return false;
    }

    private boolean isFacebookPagesExists(PostedSchedule post, String pid){
        for(Account acc : post.getSelected_id()){
            if(acc.getType().equals("facebook") && acc.getPid().equalsIgnoreCase(pid)) return true;
        }
        return false;
    }

    private boolean isInstagramExists(PostedSchedule post){
        for(Account acc : post.getSelected_id())if(acc.getType().equals("instagram"))return true;
        return false;
    }
}
