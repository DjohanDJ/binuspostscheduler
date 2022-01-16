package com.example.binuspostscheduler.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddFacebookActivity extends AppCompatActivity {

    private TextView textView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private Button setPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_facebook);

        setPages = findViewById(R.id.set_pages_btn);
        setPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(AddFacebookActivity.this, SetFacebookPages.class);
                startActivity(it);
            }
        });

        setPages.setVisibility(View.GONE);

        facebookSetting();
    }

    private void facebookSetting() {
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        textView = findViewById(R.id.FB_name);

        loginButton.setPermissions(Arrays
                .asList("email, public_profile, pages_manage_posts, pages_manage_metadata, pages_read_engagement," +
                        "instagram_basic, instagram_manage_comments, instagram_manage_insights, instagram_content_publish," +
                        "instagram_manage_messages, pages_read_user_content"));

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

    private void checkLogin() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            setProfile();
            deleteAllPages();
            setPages.setVisibility(View.VISIBLE);
        }

    }

    private void deleteAllPages(){
        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                .document("facebook").collection("pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot doc : task.getResult()){
                        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                                .document("facebook").collection("pages").document(doc.getId()).delete();
                    }
                }
                setApiDatabase();
            }
        });


    }

    private void setProfile() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
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

    private void setApiDatabase() {

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            String name = object.getString("name").toString();
                            String uid = object.getString("id").toString();

                            Map<String, Object> map = new HashMap<>();
                            map.put("uid", uid);
                            map.put("name", name);

                            db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                                    .document("facebook")
                                    .set(map);

                            int len = object.getJSONObject("accounts").getJSONArray("data").length();
                            for (int i = 0; i < len; i++) {
                                String a = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i)
                                        .getString("access_token");
                                String b = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i)
                                        .getString("id");
                                String c = object.getJSONObject("accounts").getJSONArray("data").getJSONObject(i)
                                        .getString("name");

                                Map<String, Object> pages = new HashMap<>();
                                map.put("access_token", a);
                                map.put("id", b);
                                map.put("name", c);
                                map.put("status", "active");

                                db.collection("users").document(UserSession.getCurrentUser().getId())
                                        .collection("accounts")
                                        .document("facebook").collection("pages").document(b)
                                        .set(map);
                            }
                            getInstagramBusinessAccount();
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

    public void getInstagramBusinessAccount() {
        // for each smua dptin id ig
        db.collection("users").document(UserSession.getCurrentUser().getId()).collection("accounts")
                .document("facebook").collection("pages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<DocumentSnapshot> pagesList = (ArrayList<DocumentSnapshot>) queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot page : pagesList) {
//                    Toast.makeText(AddFacebookActivity.this, page.get("id").toString(), Toast.LENGTH_SHORT).show();
                    String pageId = Objects.requireNonNull(page.get("id")).toString();
                    String pageAccTokenString = page.get("access_token").toString();
                    AccessToken pageAccToken = new AccessToken(pageAccTokenString, "472685857272246", page.get("uid").toString(),
                            null, null, null,null, null, null, null, null);

                    GraphRequest request = GraphRequest.newGraphPathRequest(
                            pageAccToken,
                            "/" + pageId,
                            new GraphRequest.Callback() {
                                @Override
                                public void onCompleted(GraphResponse response) {
//                                    Toast.makeText(AddFacebookActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                    JSONObject jsonObject = response.getJSONObject();
                                    try {
//                                        Log.d("Djohan", jsonObject.get("instagram_business_account").toString());
                                        JSONObject instagramBAObj = new JSONObject(jsonObject.get("instagram_business_account").toString());
//                                        Log.d("djohan2", instagramBAObj.getString("id"));
                                        String instagramId = instagramBAObj.getString("id");

                                        GraphRequest.newGraphPathRequest(
                                                pageAccToken,
                                                "/" + instagramId + "?fields=name",
                                                new GraphRequest.Callback() {
                                                    @Override
                                                    public void onCompleted(GraphResponse response) {
                                                        JSONObject objName = response.getJSONObject();
                                                        try {
                                                            String nameInstagram = objName.getString("name");

                                                            // insert to db
                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("id", instagramId);
                                                            map.put("name", nameInstagram);

                                                            db.collection("users").document(UserSession.getCurrentUser().getId())
                                                                    .collection("accounts")
                                                                    .document("instagram").
                                                                    set(map);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                        ).executeAsync();

                                        // insert to db
//                                        Map<String, Object> map = new HashMap<>();
//                                        map.put("id", instagramId);
////                                        map.put("name", nameInstagram);
//
//                                        db.collection("users").document(UserSession.getCurrentUser().getId())
//                                                .collection("accounts")
//                                                .document("instagram").
//                                                set(map);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "instagram_business_account");
                    request.setParameters(parameters);
                    request.executeAsync();

                }
            }
        });
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut();
                textView.setText("not connected");
                setPages.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.startTracking();
    }

}