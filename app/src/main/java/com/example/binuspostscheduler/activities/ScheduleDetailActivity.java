package com.example.binuspostscheduler.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.models.PostedSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleDetailActivity extends AppCompatActivity {

    private TextView date,desc,hashtags,image,video;
    private Button back;

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

        Intent intent = getIntent();
        PostedSchedule obj = new PostedSchedule();
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

    }
}