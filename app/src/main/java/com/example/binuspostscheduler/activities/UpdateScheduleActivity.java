package com.example.binuspostscheduler.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.models.PostedSchedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateScheduleActivity extends AppCompatActivity {

    private TextView detailDate;
    private Button changeDate, updateButton;
    private EditText detailDescription, detailHashTags;
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_schedule);

        initializeItems();

        Intent intent = getIntent();
        final PostedSchedule obj = new PostedSchedule();
        obj.setId(intent.getStringExtra("id"));
        obj.setDescription(intent.getStringExtra("description"));
        obj.setVideo(intent.getStringExtra("video"));
        obj.setImage(intent.getStringExtra("image"));
        obj.setTime(intent.getStringExtra("time"));
        obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
        obj.setSelected_id(intent.getStringArrayListExtra("selected_id"));
        fetchData(obj);

        buttonListener();

    }

    private void buttonListener() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);



//        DatePickerDialog datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
//                run {
//            year = i
//            month = i2 + 1
//            day = i3
//
//            val timePickerDialog = TimePickerDialog(view.context, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
//                    run {
//                hour = i
//                minute = i2
//                Log.d("Test", " Year = " + year + " , month = " + month + ", day =" + day + " , hour " + hour + " , minute = " + minute)
//            }
//            }, hour, minute, true)
//            timePickerDialog.show()
//        }
//        }, year, month, day)

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UpdateScheduleActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = day + "/" + month + "/" + year;
                detailDate.setText(date);
            }
        };

    }

    private void fetchData(PostedSchedule obj) {
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

        detailDate.setText(dFormat.format(pDate));
        detailDescription.setText(obj.getDescription());
//        image.setText(obj.getImage());
//        video.setText(obj.getVideo());
        detailHashTags.setText(allTags);
    }

    private void initializeItems() {
        this.detailDate = findViewById(R.id.detailDate);
        this.changeDate = findViewById(R.id.changeDate);
        this.detailDescription = findViewById(R.id.detailDescription);
        this.detailHashTags = findViewById(R.id.detailHashtags);
        this.changeDate = findViewById(R.id.changeDate);
        this.updateButton = findViewById(R.id.detailUpdateBtn);
    }
}