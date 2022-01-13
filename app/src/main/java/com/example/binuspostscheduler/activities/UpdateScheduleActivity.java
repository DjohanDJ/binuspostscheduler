package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binuspostscheduler.Adapter.AddMediaAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.animations.LoadingAnimation;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.helpers.RealPathHelper;
import com.example.binuspostscheduler.interfaces.AddMediaInterface;
import com.example.binuspostscheduler.models.PostedSchedule;
import com.example.binuspostscheduler.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateScheduleActivity extends AppCompatActivity implements AddMediaInterface {

    private TextView detailDate;
    private RecyclerView rvImages;
    private Button changeDate, updateButton, chooseImage, uploadImage;
    private EditText detailDescription, detailHashTags, timeHour;
    private ImageView edit_post_back_arrow;
    DatePickerDialog.OnDateSetListener setListener;
    private Spinner dropdownType;

    private String role = "Once";

    ArrayList<String> imagePaths = new ArrayList<>();

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
        obj.setImage(intent.getStringArrayListExtra("image"));
        obj.setTime(intent.getStringExtra("time"));
        obj.setHashtags(intent.getStringArrayListExtra("hashtags"));
        obj.setSelected_id(intent.getParcelableArrayListExtra("selected_id"));
        obj.setType(intent.getStringExtra("type"));
//        Toast.makeText(this, intent.getStringExtra("type"), Toast.LENGTH_SHORT).show();
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


        edit_post_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                if(mediaPaths.isEmpty()){
                    Toast.makeText(UpdateScheduleActivity.this, "Minimum of 1 image is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                role = dropdownType.getItemAtPosition((int) dropdownType.getSelectedItemId()).toString();

                if (mediaPaths.isEmpty()) {
                    updateData();
                } else {
                    for(Uri media : mediaPaths){

                        String randomString = RandomStringUtils.randomAlphanumeric(24);
                        StorageReference ref = SingletonFirebaseTool.getInstance().getMyStorageReference().getReference().child("images/" + randomString);
                        uploadImage(ref, media);
                    }
                }

                LoadingAnimation.startLoading(UpdateScheduleActivity.this);
                startActivity(new Intent(UpdateScheduleActivity.this, MainActivity.class));

//                SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("users" )
//                        .document(UserSession.getCurrentUser().getId())
//                        .collection("schedule")
//                        .document(getIntent().getStringExtra("id")).set(updatedSchedule)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(UpdateScheduleActivity.this, getResources().getString(R.string.meeting_updated), Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//                        });;
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                // ini day of month
                String date = dayOfMonth + "-" + month + "-" + year;
                Date pDate = null;
                SimpleDateFormat dFormat = new SimpleDateFormat("dd-mm-yyyy");
                try {
                    pDate = new SimpleDateFormat("dd-mm-yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                detailDate.setText(dFormat.format(pDate));
            }
        };

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                String[] mimetypes = new String[]{"image/*", "video/*"};
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(i, "Choose Image"), 999);
            }
        });

//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                ArrayList<String> imagePaths = new ArrayList<>();
//
//
////                imagePaths.clear();
////
////                for(Uri media : mediaPaths){
////
////                    String randomString = RandomStringUtils.randomAlphanumeric(24);
////                    StorageReference ref = SingletonFirebaseTool.getInstance().getMyStorageReference().getReference().child("images/" + randomString);
//////                    StorageTask downloadUrl = uploadImage(ref, media);
//////                    imagePaths.add(downloadUrl)
//////                    await(uploadImage(ref, media));
////                    uploadImage(ref, media);
////                }
////
////                LoadingAnimation.startLoading(UpdateScheduleActivity.this);
////
//////                while (true) if (imagePaths.size() == mediaPaths.size()) break;
////
////                Log.d("ERRR", " " + imagePaths.size());
////
////            }
//
//
//        });

    }

    private void updateData() {
        PostedSchedule updatedSchedule = new PostedSchedule();
        updatedSchedule.setId(getIntent().getStringExtra("id"));
        updatedSchedule.setDescription(detailDescription.getText().toString());
        updatedSchedule.setVideo(getIntent().getStringExtra("video"));
        updatedSchedule.setImage(imagePaths);
        updatedSchedule.setUser_id(UserSession.getCurrentUser().getId());
        updatedSchedule.setTime(detailDate.getText().toString() + " " + timeHour.getText().toString() + ":00");
        updatedSchedule.setType(role);
        String allTags = detailHashTags.getText().toString();
        ArrayList<String> arrStringTags = new ArrayList<>();
        String[] arrTags = allTags.split(" ");
        for (String tag : arrTags) {
            arrStringTags.add(tag);
        }
        updatedSchedule.setHashtags(arrStringTags);
        updatedSchedule.setSelected_id(getIntent().getParcelableArrayListExtra("selected_id"));

        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("schedules")
                .document(updatedSchedule.getId()).set(updatedSchedule)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateScheduleActivity.this, getResources().getString(R.string.meeting_updated), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });;
    }

    public synchronized void uploadImage(StorageReference ref, Uri media) {

        ref.putFile(media).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("DDD", uri.toString());
                            imagePaths.add(uri.toString());
                            if (imagePaths.size() == mediaPaths.size()) {
                                updateData();
                            }
                        }
                    });
                }
            }
        });

    }

    ArrayList<Uri> mediaPaths = new ArrayList<>();
    ArrayList<File> medias = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
//            img = File(data.data!!.toString())
            RealPathHelper objRPH = RealPathHelper.INSTANCE;
            String path = objRPH.getRealPath(this, data.getData());
//            Log.d("Data","= "+data.data)
//            Log.d("Path","= "+)
//            Log.d("PATH",path!!)
            File file = new File(path);
            if (!file.isFile()){
//                Log.d("ISFILE","NOPE "+file.absolutePath)
                Toast.makeText(this, "Its not a file", Toast.LENGTH_SHORT).show();
            }
            else{
                mediaPaths.add(data.getData());
                String fileExt = path.substring(path.lastIndexOf(".")+1);

                medias.add(file);
                AddMediaAdapter adapter = new AddMediaAdapter(this, medias, this, false);

                rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                rvImages.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                checkMediaStatus();
            }
        }
    }

    private void checkMediaStatus(){
        if (medias.isEmpty()) rvImages.setVisibility(View.GONE);
        else rvImages.setVisibility(View.VISIBLE);

        chooseImage.setEnabled(medias.size() != 4);
    }

    private void fetchData(PostedSchedule obj) {
        SimpleDateFormat dFormat = new SimpleDateFormat("dd-mm-yyyy");
        Date pDate = null;
        try {
            pDate = new SimpleDateFormat("dd-mm-yyyy").parse(obj.getTime());
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

//        detailDate.setText(dFormat.format(pDate));
        detailDescription.setText(obj.getDescription());
//        image.setText(obj.getImage());
//        video.setText(obj.getVideo());

//        String compareValue = obj.getType();
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdownType.setAdapter(adapter);
//        if (compareValue != null) {
//            int spinnerPosition = adapter.getPosition(compareValue);
//            dropdownType.setSelection(spinnerPosition);
//        }

//        Toast.makeText(this, obj.getType(), Toast.LENGTH_SHORT).show();
        int pos = -1;

        if (obj.getType().equals("Once")) {
            pos = 0;
        } else if (obj.getType().equals("Daily")) {
            pos = 1;
        } else if (obj.getType().equals("Weekly")) {
            pos = 2;
        } else if (obj.getType().equals("Monthly")) {
            pos = 3;
        }
        dropdownType.setSelection(pos);

        detailHashTags.setText(allTags);
        String[] arrSplit = obj.getTime().split(" ");
        String[] timeSplit = arrSplit[1].split(":");
        detailDate.setText(arrSplit[0]);
        timeHour.setText(timeSplit[0] + ":" + timeSplit[1]);
    }

    private void initializeItems() {
        this.detailDate = findViewById(R.id.detailDate);
        this.changeDate = findViewById(R.id.changeDate);
        this.detailDescription = findViewById(R.id.detailDescription);
        this.detailHashTags = findViewById(R.id.detailHashtags);
        this.changeDate = findViewById(R.id.changeDate);
        this.updateButton = findViewById(R.id.detailUpdateBtn);
        this.timeHour = findViewById(R.id.timeHour);
//        this.uploadImage = findViewById(R.id.uploadImage);
        this.chooseImage = findViewById(R.id.chooseImage);
        this.rvImages = findViewById(R.id.recyclerViewImages);
        this.edit_post_back_arrow = findViewById(R.id.edit_post_back_arrow);

        this.dropdownType = findViewById(R.id.dropdownType);
        this.dropdownType.setAdapter(new ArrayAdapter<>(UpdateScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.role)));

    }

    @Override
    public void removeMedia(int index) {
        medias.remove(index);
        mediaPaths.remove(index);
        checkMediaStatus();
    }
}