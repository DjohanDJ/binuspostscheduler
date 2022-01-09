package com.example.binuspostscheduler.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.models.FacebookAccount;
import com.facebook.FacebookSdk;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;

import com.example.binuspostscheduler.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddFacebookActivity extends AppCompatActivity {

    private TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facebook);

        facebookSetting();
    }

    private void facebookSetting(){
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        textView = findViewById(R.id.FB_name);

        loginButton.setPermissions(Arrays.asList("email, public_profile, pages_manage_posts, pages_manage_metadata, pages_read_engagement"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accToken = loginResult.getAccessToken().toString();
                FacebookAccount facebookAccount = new FacebookAccount(accToken);

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        checkLogin();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        checkLogin();

    }

    private void checkLogin(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn) {
            setProfile();
            setApiDatabase();
        }

    }

    private void setProfile(){


        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("Demo", object.toString());

                try {
                    String name = object.getString("name");
                    textView.setText(name);
                    Log.d("Demo", "set text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name");

        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    private void setApiDatabase(){


        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String name = object.getString("name").toString();
                    String uid = object.getString("id").toString();
//                    Log.d("HAIHAI", object.getJSONObject("accounts").toString());
//                    Log.d("HAIHAI", object.getJSONObject("accounts").getJSONArray("data").toString());
//                    Log.d("HAIHAI", String.valueOf(object.getJSONObject("accounts").getJSONArray("data").length()));
//                    Log.d("HAIHAI", object.getJSONObject("accounts").getJSONArray("data").getJSONObject(0).toString());

                    Map<String, Object> map = new HashMap<>();
                    map.put("uid", uid);
                    map.put("name", name);

                    db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts").document("facebook")
                            .set(map);

                    int len = object.getJSONObject("accounts").getJSONArray("data").length();
                    for(int i = 0; i < len; i++) {
                        String a = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("access_token");
                        String b = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("id");
                        String c = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i).getString("name");

                        Map<String, Object> pages = new HashMap<>();
                        map.put("access_token", a);
                        map.put("id", b);
                        map.put("name", c);

                        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                                .document("facebook").collection("pages").document(b)
                                .set(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "gender, name, id, first_name, last_name, accounts");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();


    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken == null){
                LoginManager.getInstance().logOut();
                textView.setText("not connected");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.startTracking();
    }

}