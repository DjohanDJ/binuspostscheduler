package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class LandingActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        final String currentUserId = sharedPreferences.getString("user_userId", "");
        assert currentUserId != null;

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!currentUserId.equals("")) {
                    doFeedUserSession(currentUserId);
                } else {
                    startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    public void doFeedUserSession(final String loggedId) {
        DocumentReference noteRef = SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("users").document(loggedId);

        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User newUser = new User(documentSnapshot.getString("username"), loggedId, documentSnapshot.getString("email"), documentSnapshot.getString("password"), documentSnapshot.getString("role"));
                            UserSession.setCurrentUser(newUser);
                            startActivity(new Intent(LandingActivity.this, MainActivity.class));
                            finish();
//                            Toast.makeText(LoginActivity.this, documentSnapshot.getString("username"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LandingActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LandingActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("EROR DJOHAN", e.toString());
                    }
                });

    }
}