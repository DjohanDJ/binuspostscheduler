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
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.provider.MediaStore;
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
import com.example.binuspostscheduler.models.PostedSchedule;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;

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
import java.util.Timer;
import java.util.TimerTask;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView date,desc,hashtags;
    private Button deleteBtn, updateBtn, igShare;
    private ImageView back, plb, cpl, getBitmap;
    private RecyclerView imageRec;
    private ScrollView sc;
    private ConstraintLayout plc;
    private VideoView video;
    private View b50ab;
    private ShareButton shareFB;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static  int REQUEST_CODE = 100;
    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        verifyStoragePermissions(this);

        Context ctx = this;

        date = findViewById(R.id.detailDate);
        desc = findViewById(R.id.detailDescription);
        hashtags = findViewById(R.id.detailHashtags);

        video = findViewById(R.id.post_detail_video);
        back = findViewById(R.id.post_detail_back_arrow);
//        deleteBtn = findViewById(R.id.detailDeleteBtn);
        updateBtn = findViewById(R.id.detailUpdateBtn);
        imageRec = findViewById(R.id.post_detail_image_recycler);
        sc = findViewById(R.id.scrollView2);

        plb = findViewById(R.id.postListButton);
        plc = findViewById(R.id.postListConstraint);
        b50ab = findViewById(R.id.back_50alpha_black);
        cpl = findViewById(R.id.closePostList);

        shareFB = findViewById(R.id.FacebookShareButton);
        igShare = findViewById(R.id.InstagramShareButton);



        Intent intent = getIntent();
        final PostedSchedule obj = new PostedSchedule();
        obj.setId(intent.getStringExtra("id"));
        obj.setDescription(intent.getStringExtra("description"));
        obj.setVideo(intent.getStringExtra("video"));
        obj.setImage(intent.getStringArrayListExtra("image"));
        obj.setTime(intent.getStringExtra("time"));
        obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
        obj.setSelected_id(intent.getStringArrayListExtra("selected_id"));



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
//        video.setText(obj.getVideo());
        hashtags.setText(allTags);


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


//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(ScheduleDetailActivity.this, obj.getId(), Toast.LENGTH_SHORT).show();
//                deleteSchedule(obj.getId());
//            }
//        });

//        String url = obj.getVideo();
//        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(url , MediaStore.Video.Thumbnails.MICRO_KIND);
        if(obj.getVideo().equals("-")){
            video.setVisibility(View.GONE);
        }else{
            Uri uri = Uri.parse(obj.getVideo());
            video.setVideoURI(uri);
            MediaController mediaController = new MediaController(this);
            video.setMediaController(mediaController);
            mediaController.setAnchorView(video);
        }

        ArrayList<String> imgList = obj.getImage();

        setShareFacebook(imgList, obj, this);

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
                intentUpdate.putExtra("image", intent.getStringExtra("image"));
                intentUpdate.putExtra("time", intent.getStringExtra("time"));
                intentUpdate.putExtra("hashtags", intent.getStringArrayListExtra("hashtags"));
                intentUpdate.putExtra("selected_id", intent.getStringArrayListExtra("selected_id"));
                startActivity(intentUpdate);
                finish();
//                obj.setId(intent.getStringExtra("id"));
//                obj.setDescription(intent.getStringExtra("description"));
//                obj.setVideo(intent.getStringExtra("video"));
//                obj.setImage(intent.getStringExtra("image"));
//                obj.setTime(intent.getStringExtra("time"));
//                obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
//                obj.setSelected_id(intent.getStringArrayListExtra("selected_id"));
            }
        });

        igShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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



//                saveImage(obj.getImage());

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
        });
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

    private void setShareFacebook(ArrayList<String> imgList,PostedSchedule obj, Context ctx) {

        ArrayList<SharePhoto> sharePhotos = new ArrayList<>();

        //set video masih belum
        //set image udah benar tinggal ganti atribut jadi array list di firestore

        for(String urlImg : imgList){
            Bitmap pcBitmap = null;

            Picasso.get().load(urlImg).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    SharePhoto sharePhoto = new SharePhoto.Builder()
                            .setBitmap(bitmap)
                            .build();

                    sharePhotos.add(sharePhoto);

                    if(sharePhotos.size() == imgList.size()){
                        ShareContent shareContent = null;

                        if(sharePhotos.size() == 1){
                            shareContent = new ShareMediaContent.Builder()
                                    .addMedium(sharePhotos.get(0))
                                    .build();
                        }else if(sharePhotos.size() == 2){
                            shareContent = new ShareMediaContent.Builder()
                                    .addMedium(sharePhotos.get(0))
                                    .addMedium(sharePhotos.get(1))
                                    .build();
                        }else if(sharePhotos.size() == 3){
                            shareContent = new ShareMediaContent.Builder()
                                    .addMedium(sharePhotos.get(0))
                                    .addMedium(sharePhotos.get(1))
                                    .addMedium(sharePhotos.get(2))
                                    .build();
                        }else if(sharePhotos.size() == 4){
                            shareContent = new ShareMediaContent.Builder()
                                    .addMedium(sharePhotos.get(0))
                                    .addMedium(sharePhotos.get(1))
                                    .addMedium(sharePhotos.get(2))
                                    .addMedium(sharePhotos.get(3))
                                    .build();
                        }

                        shareFB.setShareContent(shareContent);

                        shareFB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String desc = obj.getDescription() + "\n\n";

                                for(String hash: obj.getHashtags()){
                                    desc += hash + " ";
                                }

                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("", desc);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(ctx, "Description copied to clipboard", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Toast.makeText(ctx, "fail", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

    }

    private void deleteSchedule(final String id) {
//        Toast.makeText(this, UserSession.getCurrentUser().getId(), Toast.LENGTH_SHORT).show();
        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("users" )
                .document(UserSession.getCurrentUser().getId())
                .collection("schedule")
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
                            finish();
                        }
                    }
                });
    }

}