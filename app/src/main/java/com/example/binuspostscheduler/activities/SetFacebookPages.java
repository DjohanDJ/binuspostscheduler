package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.binuspostscheduler.Adapter.FacebookPagesAdapter;
import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SetFacebookPages extends AppCompatActivity {

    private RecyclerView setPagesRecycle;
    private Button backBtn;
//    ArrayList<FacebookPages> fp = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_facebook_pages);

//        init();

    }

//    private void init(){
//        setPagesRecycle = findViewById(R.id.all_facebook_pages);
//        backBtn = findViewById(R.id.set_pages_back);
//        getFacebookPages();
//        setBackButton();
//    }
//
//    private void setPageRecycles(){
//        FacebookPagesAdapter comAdapter = new FacebookPagesAdapter(this, fp);
//        setPagesRecycle.setAdapter(comAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        setPagesRecycle.setLayoutManager(layoutManager);
//    }
//
//    private void setBackButton(){
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    private void getFacebookPages(){
//        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
//                .document("facebook").collection("pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(DocumentSnapshot ds : task.getResult()){
//                        String accT = ds.get("access_token").toString();
//                        String id = ds.get("id").toString();
//                        String name = ds.get("name").toString();
//                        String uid = ds.get("uid").toString();
//                        String status = ds.get("status").toString();
//
//                        FacebookPages facebookPages = new FacebookPages(accT,id,name,uid);
//                        facebookPages.setStatus(status);
//                        fp.add(facebookPages);
//                    }
//                    setPageRecycles();
//                }
//            }
//        });
//    }
}