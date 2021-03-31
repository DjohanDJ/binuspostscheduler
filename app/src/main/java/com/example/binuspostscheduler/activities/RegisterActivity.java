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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private TextView loginText;
    private Button signUpButton;
    private EditText email_text, password_text, re_password_text, username_text;
    private ValidationHelper validationHelper;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        doInitializeItems();
        doButtonListener();
    }

    private void doButtonListener() {

        this.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckValidation();
            }
        });

        this.loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void doCheckValidation() {
        final String username = this.username_text.getText().toString();
        final String email = this.email_text.getText().toString();
        final String password = this.password_text.getText().toString();
        String rePassword = this.re_password_text.getText().toString();

        if (email.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.emailFilled), Toast.LENGTH_SHORT).show();
        } else if (!validationHelper.doEmailValidation(email.trim())) {
            Toast.makeText(this, getResources().getString(R.string.emailFormat), Toast.LENGTH_SHORT).show();
        } else if (username.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.username_filled), Toast.LENGTH_SHORT).show();
        } else if (username.trim().length() > 18) {
            Toast.makeText(this, getResources().getString(R.string.username_failed), Toast.LENGTH_SHORT).show();
        } else if (password.trim().isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.passFilled), Toast.LENGTH_SHORT).show();
        } else if (password.trim().length() < 5) {
            Toast.makeText(this, getResources().getString(R.string.password_failed), Toast.LENGTH_SHORT).show();
        } else if (!rePassword.trim().equals(password.trim())) {
            Toast.makeText(this, getResources().getString(R.string.re_pass_failed), Toast.LENGTH_SHORT).show();
        } else {
            LoadingAnimation.startLoading(RegisterActivity.this);
            SingletonFirebaseTool.getInstance().getMyFirebaseAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getResources().getString(R.string.sign_up_error), Toast.LENGTH_SHORT).show();
                    } else {
                        String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                        User newUser = new User(username, userId, email, password, "Guest");
                        UserSession.setCurrentUser(newUser);
                        SingletonFirebaseTool.getInstance().getMyFireStoreReference().collection("users").document(userId).set(newUser);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_userId", userId);
                        editor.apply();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

    private void doInitializeItems() {
        this.loginText = findViewById(R.id.sign_in_text);
        this.signUpButton = findViewById(R.id.sign_up_button);
        this.email_text = findViewById(R.id.email_text);
        this.password_text = findViewById(R.id.password_text);
        this.re_password_text = findViewById(R.id.repassword_text);
        this.username_text = findViewById(R.id.username_text);
        this.validationHelper = new ValidationHelper();
        this.sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
    }
}