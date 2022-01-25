package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.binuspostscheduler.Adapter.PostDetailImageAdapter;
import com.example.binuspostscheduler.Adapter.TodayScheduleAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.fragments.CreatePostFragment;
import com.example.binuspostscheduler.models.Account;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView date,desc,hashtags, type;
    private Button deleteBtn, updateBtn, igShare,twitterShareBtn;
    private ImageView back, plb, cpl, getBitmap;
    private RecyclerView imageRec;
    private ScrollView sc;
    private ConstraintLayout plc;
    private VideoView video;
    private View b50ab;
    private Button shareFB;
    private FirebaseFirestore db;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final Context ctx = this;
    private PostedSchedule obj = new PostedSchedule();

    private static  int REQUEST_CODE = 100;
    OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        verifyStoragePermissions(this);

        db = SingletonFirebaseTool.getInstance().getMyFireStoreReference();

        Intent intent = getIntent();
        obj.setId(intent.getStringExtra("id"));
        obj.setDescription(intent.getStringExtra("description"));
        obj.setVideo(intent.getStringExtra("video"));
        obj.setImage(intent.getStringArrayListExtra("image"));
        obj.setTime(intent.getStringExtra("time"));
        obj.setType(intent.getStringExtra("type"));
        obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
        obj.setSelected_id(intent.getParcelableArrayListExtra("selected_id"));

        setObj(obj.getId());





        date = findViewById(R.id.detailDate);
        desc = findViewById(R.id.detailDescription);
        hashtags = findViewById(R.id.detailHashtags);
        type = findViewById(R.id.detailType);

        video = findViewById(R.id.post_detail_video);
        back = findViewById(R.id.post_detail_back_arrow);
        deleteBtn = findViewById(R.id.deleteButton);
        updateBtn = findViewById(R.id.detailUpdateBtn);
        imageRec = findViewById(R.id.post_detail_image_recycler);
        sc = findViewById(R.id.scrollView2);

        plb = findViewById(R.id.postListButton);
        plc = findViewById(R.id.postListConstraint);
        b50ab = findViewById(R.id.back_50alpha_black);
        cpl = findViewById(R.id.closePostList);

        shareFB = findViewById(R.id.FacebookShareButton);
        igShare = findViewById(R.id.InstagramShareButton);
        twitterShareBtn = findViewById(R.id.TwitterShareButton);








        SimpleDateFormat dFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date pDate = null;
        try {
            pDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(obj.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String allTags = "";

        for(String tag : obj.getHashtags()){
            if(allTags.equals("")){
                allTags += tag;
            }else{
                allTags += " " + tag;
            }
        }

        date.setText(dFormat.format(pDate));
        desc.setText(obj.getDescription());
        hashtags.setText(allTags);
        type.setText(obj.getType());


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        plb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plc.setVisibility(View.VISIBLE);
                b50ab.setVisibility(View.VISIBLE);
            }
        });

        cpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plc.setVisibility(View.GONE);
                b50ab.setVisibility(View.GONE);
            }
        });

        b50ab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plc.setVisibility(View.GONE);
                b50ab.setVisibility(View.GONE);
            }
        });

        twitterShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts").document("twitter").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Account account = task.getResult().toObject(Account.class);
                            postTwitter(account);
                        }
                        else{
                            Toast.makeText(ctx, "Twitter not connected", Toast.LENGTH_SHORT).show();
                        }
                        plc.setVisibility(View.GONE);
                        b50ab.setVisibility(View.GONE);
                    }
                });
            }
        });





        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(ScheduleDetailActivity.this, obj.getId(), Toast.LENGTH_SHORT).show();
                deleteSchedule(obj.getId());
            }
        });

//        String url = obj.getVideo();
//        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(url , MediaStore.Video.Thumbnails.MICRO_KIND);

        video.setVisibility(View.GONE);

        // set video
//        if(obj.getVideo().equals("-")){
//            video.setVisibility(View.GONE);
//        }else{
//            Uri uri = Uri.parse(obj.getVideo());
//            video.setVideoURI(uri);
//            MediaController mediaController = new MediaController(this);
//            video.setMediaController(mediaController);
//            mediaController.setAnchorView(video);
//        }

        ArrayList<String> imgList = obj.getImage();

        // facebook manual
//        setShareFacebook(imgList, obj, this);


        if(imgList.get(0).equals("-")){
            imageRec.setVisibility(View.GONE);
        }

        PostDetailImageAdapter comAdapter = new PostDetailImageAdapter(this, imgList);
        imageRec.setAdapter(comAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageRec.setLayoutManager(layoutManager);


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentUpdate = new Intent(ScheduleDetailActivity.this, UpdateScheduleActivity.class);
                intentUpdate.putExtra("id", intent.getStringExtra("id"));
                intentUpdate.putExtra("description", intent.getStringExtra("description"));
                intentUpdate.putExtra("video", intent.getStringExtra("video"));
                intentUpdate.putExtra("image", intent.getStringArrayListExtra("image"));
                intentUpdate.putExtra("time", intent.getStringExtra("time"));
                intentUpdate.putExtra("hashtags", intent.getStringArrayListExtra("hashtags"));
                intentUpdate.putExtra("selected_id", intent.getStringArrayListExtra("selected_id"));
                intentUpdate.putExtra("type", intent.getStringExtra("type"));
                startActivity(intentUpdate);
                finish();
            }
        });

        igShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setApiDatabase(obj, "IG");
                plc.setVisibility(View.GONE);
                b50ab.setVisibility(View.GONE);
            }
        });

        shareFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setApiDatabase(obj, "FB");
                plc.setVisibility(View.GONE);
                b50ab.setVisibility(View.GONE);
            }
        });
    }

    private void setObj(String id){
        db.collection("schedules").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                    PostedSchedule postedSchedule = documentSnapshot.toObject(PostedSchedule.class);
                    if(postedSchedule.getId().equalsIgnoreCase(id)){
                        obj = postedSchedule;
                    }
                }
            }
        });


    }

    private void postInstagram(String instagramBId,String accT, String uid){
        com.facebook.AccessToken accessToken = new com.facebook.AccessToken(accT, "472685857272246", uid,
                null, null, null,null, null, null, null, null);

        String desc = obj.getDescription() + "\n\n.\n.\n.\n.\n";

        for(String hash : obj.getHashtags()){
            desc += hash + " ";
        }

        Bundle params = new Bundle();
        params.putString("image_url", obj.getImage().get(0));
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
                                            Toast.makeText(ctx, "Instagram Post Success", Toast.LENGTH_SHORT).show();
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

    private void postFacebook(String id, String accT, String uid){
        com.facebook.AccessToken acc = new com.facebook.AccessToken(accT, "472685857272246", uid,
                null, null, null,null, null, null, null, null);

        String desc = obj.getDescription() + "\n\n";

        for(String hash : obj.getHashtags()){
            desc += hash + " ";
        }

        Bundle params = new Bundle();
        params.putString("message", desc);
        params.putString("url", obj.getImage().get(0));


        new GraphRequest(
                acc,
                "/ "+ id +"/photos",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Toast.makeText(ctx, "Facebook Post Success", Toast.LENGTH_SHORT).show();
                    }
                }
        ).executeAsync();
    }

    private void setApiDatabase(PostedSchedule post, String type){
        GraphRequest graphRequest = GraphRequest.newMeRequest(com.facebook.AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if(object != null){
                    try {
                        String name = object.getString("name").toString();
                        String uid = object.getString("id").toString();

                        Map<String, Object> map = new HashMap<>();
                        map.put("uid", uid);
                        map.put("name", name);

                        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("accounts").document("facebook")
                                .update(map);

                        int len = object.getJSONObject("accounts").getJSONArray("data").length();

                        for(int i = 0; i < len; i++) {
                            String a = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("access_token");
                            String b = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("id");
                            String c = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("name");

                            Map<String, Object> page = new HashMap<>();
                            page.put("access_token", a);
                            page.put("id", b);
                            page.put("name", c);
                            page.put("uid", uid);

                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("accounts")
                                    .document("facebook").collection("pages").document(b)
                                    .update(page);
                        }
                        checkingFacebookPagesData(type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ctx, "Account not connected", Toast.LENGTH_SHORT).show();
//                    Log.d("HAIHAI error nih", response.getError().getErrorMessage().toString());
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name, accounts");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    private void checkingFacebookPagesData(String type){
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("accounts")
                .document("facebook").collection("pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String accT, id, name, uid = null;
                    ArrayList<String> accT_list = new ArrayList();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        accT= doc.getString("access_token");
                        id = doc.getString("id");
                        name = doc.getString("name");
                        uid = doc.getString("uid");
                        accT_list.add(accT);
                        //post facebook
                        if(type.equalsIgnoreCase("FB")){
                            if(isFacebookPagesExists(id)){
                                postFacebook(id, accT, uid);
                            }
                        }
                    }
                    //post instagram
                    if(type.equalsIgnoreCase("IG")){
                        if(isInstagramExists()){
                            String finalUid = uid;
                            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("accounts").document("instagram")
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String instagramBId = documentSnapshot.get("id").toString();
                                    postInstagram(instagramBId, accT_list.get(0), finalUid);
                                }
                            });
                        }
                    }
                }else{
                    //error mungkin karena ga ke login
                }
            }
        });
    }

    private boolean isFacebookPagesExists(String pid){
        for(Account acc : obj.getSelected_id()){
            if(acc.getType().equals("facebook") && acc.getPid().equalsIgnoreCase(pid)) return true;
        }
        return false;
    }

    private boolean isInstagramExists(){
        for(Account acc : obj.getSelected_id())if(acc.getType().equals("instagram"))return true;
        return false;
    }

    private void manualIgPost(){
        String desc = obj.getDescription() + "\n\n";

        for(String hash: obj.getHashtags()){
            desc += hash + " ";
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", desc);
        clipboard.setPrimaryClip(clip);


        ActivityCompat.requestPermissions(ScheduleDetailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(ScheduleDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 1);
            } else {
                saveImage(obj.getImage());
            }
        }


        Toast.makeText(ctx, "Description copied and image saved", Toast.LENGTH_SHORT).show();

        Uri uri = Uri.parse("http://instagram.com/");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com")));
        }

    }

    private void saveImage(ArrayList<String> imgList) {
        for(String urlImg : imgList){
            Picasso.get().load(urlImg).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    FileOutputStream outputStream = null;
                    File file = Environment.getExternalStorageDirectory();
                    File dir = new File(file + "/DCIM");
                    dir.mkdirs();

                    File file1 = new File(dir, System.currentTimeMillis() + ".PNG");
                    try {
                        outputStream = new FileOutputStream(file1);
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Toast.makeText(ScheduleDetailActivity.this, "Image success", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ScheduleDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }

    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

//    private void setShareFacebook(ArrayList<String> imgList,PostedSchedule obj, Context ctx) {
//
//        ArrayList<SharePhoto> sharePhotos = new ArrayList<>();
//
//        //set video masih belum
//        //set image udah benar tinggal ganti atribut jadi array list di firestore
//
//        for(String urlImg : imgList){
//            Bitmap pcBitmap = null;
//
//            Picasso.get().load(urlImg).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    SharePhoto sharePhoto = new SharePhoto.Builder()
//                            .setBitmap(bitmap)
//                            .build();
//
//                    sharePhotos.add(sharePhoto);
//
//                    if(sharePhotos.size() == imgList.size()){
//                        ShareContent shareContent = null;
//
//                        if(sharePhotos.size() == 1){
//                            shareContent = new ShareMediaContent.Builder()
//                                    .addMedium(sharePhotos.get(0))
//                                    .build();
//                        }else if(sharePhotos.size() == 2){
//                            shareContent = new ShareMediaContent.Builder()
//                                    .addMedium(sharePhotos.get(0))
//                                    .addMedium(sharePhotos.get(1))
//                                    .build();
//                        }else if(sharePhotos.size() == 3){
//                            shareContent = new ShareMediaContent.Builder()
//                                    .addMedium(sharePhotos.get(0))
//                                    .addMedium(sharePhotos.get(1))
//                                    .addMedium(sharePhotos.get(2))
//                                    .build();
//                        }else if(sharePhotos.size() == 4){
//                            shareContent = new ShareMediaContent.Builder()
//                                    .addMedium(sharePhotos.get(0))
//                                    .addMedium(sharePhotos.get(1))
//                                    .addMedium(sharePhotos.get(2))
//                                    .addMedium(sharePhotos.get(3))
//                                    .build();
//                        }
//
//                        shareFB.setShareContent(shareContent);
//
//                        shareFB.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String desc = obj.getDescription() + "\n\n";
//
//                                for(String hash: obj.getHashtags()){
//                                    desc += hash + " ";
//                                }
//
//                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                ClipData clip = ClipData.newPlainText("", desc);
//                                clipboard.setPrimaryClip(clip);
//                                Toast.makeText(ctx, "Description copied to clipboard", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                    Toast.makeText(ctx, "fail", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                }
//            });
//        }
//
//    }

    private void deleteSchedule(final String id) {
        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .document(id)
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            SingletonFirebaseTool.getInstance().getMyFireStoreReference()
//                                    .collection("users/" + UserSession.getCurrentUser().getId() + "/schedule")
//                                    .document(id).delete();
//
//                    }
//                });


                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ScheduleDetailActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ScheduleDetailActivity.this, MainActivity.class));
                        }
                    }
                });
    }

    private void postTwitter( Account account) {

                String access_token = account.getAccess_token();
                String access_secret = account.getAccess_secret();
                ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder();
                cb.setDebugEnabled(true);
                cb.setOAuthConsumerKey(getString(R.string.twitter_CONSUMER_KEY));
                cb.setOAuthConsumerSecret(getString(R.string.twitter_CONSUMER_SECRET));
                AccessToken twToken = new AccessToken(access_token, access_secret);
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
                Log.d("twitter","Uploaded");
                String statusUrl = "https://twitter.com/" + status.getUser().getScreenName()
                        .toString() + "/status/" + status.getId();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                alertDialogBuilder.setTitle("Upload Successful");
                alertDialogBuilder.setMessage("Upload Succesful");
                alertDialogBuilder.setPositiveButton("View Tweets", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(statusUrl));
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
//                Looper.prepare();
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        alertDialogBuilder.show();
                    }
                });
            } catch (TwitterException e) {
                e.printStackTrace();
            }



        }
        });
        thread.start();


    }

}