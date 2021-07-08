package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binuspostscheduler.Adapter.PostDetailImageAdapter;
import com.example.binuspostscheduler.Adapter.TodayScheduleAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView date,desc,hashtags,video;
    private Button deleteBtn, updateBtn;
    private ImageView back;
    private RecyclerView imageRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        date = findViewById(R.id.detailDate);
        desc = findViewById(R.id.detailDescription);
        hashtags = findViewById(R.id.detailHashtags);

//        video = findViewById(R.id.detailVideo);
        back = findViewById(R.id.post_detail_back_arrow);
//        deleteBtn = findViewById(R.id.detailDeleteBtn);
        updateBtn = findViewById(R.id.detailUpdateBtn);
        imageRec = findViewById(R.id.post_detail_image_recycler);

        Intent intent = getIntent();
        final PostedSchedule obj = new PostedSchedule();
        obj.setId(intent.getStringExtra("id"));
        obj.setDescription(intent.getStringExtra("description"));
        obj.setVideo(intent.getStringExtra("video"));
        obj.setImage(intent.getStringExtra("image"));
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

//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(ScheduleDetailActivity.this, obj.getId(), Toast.LENGTH_SHORT).show();
//                deleteSchedule(obj.getId());
//            }
//        });

        ArrayList<String> imgList = new ArrayList<>();
        imgList.add(obj.getImage());




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
//                obj.setId(intent.getStringExtra("id"));
//                obj.setDescription(intent.getStringExtra("description"));
//                obj.setVideo(intent.getStringExtra("video"));
//                obj.setImage(intent.getStringExtra("image"));
//                obj.setTime(intent.getStringExtra("time"));
//                obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
//                obj.setSelected_id(intent.getStringArrayListExtra("selected_id"));
            }
        });

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