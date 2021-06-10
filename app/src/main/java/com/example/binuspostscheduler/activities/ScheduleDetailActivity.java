package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView date,desc,hashtags,image,video;
    private Button back, deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        date = findViewById(R.id.detailDate);
        desc = findViewById(R.id.detailDescription);
        hashtags = findViewById(R.id.detailHashtags);
        image = findViewById(R.id.detailImage);
        video = findViewById(R.id.detailVideo);
        back = findViewById(R.id.detailBackBtn);
        deleteBtn = findViewById(R.id.detailDeleteBtn);

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
        image.setText(obj.getImage());
        video.setText(obj.getVideo());
        hashtags.setText(allTags);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScheduleDetailActivity.this, obj.getId(), Toast.LENGTH_SHORT).show();
                deleteSchedule(obj.getId());
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