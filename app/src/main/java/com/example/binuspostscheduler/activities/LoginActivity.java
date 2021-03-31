package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.animations.LoadingAnimation;
import com.example.binuspostscheduler.authentications.SingletonFirebaseTool;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.helpers.ValidationHelper;
import com.example.binuspostscheduler.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView registerText;
    private Button signInButton;
    private EditText email_text, password_text;
    private ValidationHelper validationHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        doInitializeItems();
        doButtonListener();
    }

    private void doButtonListener() {

        this.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckCredential();
            }
        });

        this.registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void doCheckCredential() {
        String email = this.email_text.getText().toString();
        String pass = this.password_text.getText().toString();
        if (email.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.emailFilled), Toast.LENGTH_SHORT).show();
        } else if (!validationHelper.doEmailValidation(email.trim())) {
            Toast.makeText(this, getResources().getString(R.string.emailFormat), Toast.LENGTH_SHORT).show();
        } else if (pass.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.passFilled), Toast.LENGTH_SHORT).show();
        } else {
            LoadingAnimation.startLoading(LoginActivity.this);
            SingletonFirebaseTool.getInstance().getMyFirebaseAuth()
                    .signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.invalidCredential), Toast.LENGTH_SHORT).show();
                        LoadingAnimation.getDialog().dismiss();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_userId", Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                        editor.apply();
                        doFeedUserSession(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                    }
                }
            });
        }
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
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
//                            Toast.makeText(LoginActivity.this, documentSnapshot.getString("username"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d("EROR DJOHAN", e.toString());
                    }
                });

    }

    private void doInitializeItems() {
        this.registerText = findViewById(R.id.sign_up_button);
        this.signInButton = findViewById(R.id.sign_in_button);
        this.email_text = findViewById(R.id.email_text);
        this.password_text = findViewById(R.id.password_text);
        this.validationHelper = new ValidationHelper();
        this.sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
    }
}