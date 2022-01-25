package com.example.binuspostscheduler.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.binuspostscheduler.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.twitter.sdk.android.core.*

import kotlinx.android.synthetic.main.activity_add_twitter.*

class AddTwitterActivity : AppCompatActivity() {

    private val ctx = this
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_twitter)
        val uid = getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_userId", "")
        db.collection("users").document(uid!!).collection("accounts").document("twitter").get().addOnCompleteListener {
            x ->
                if(x.result!!.exists()){
                    twitter_profile_name.visibility = View.VISIBLE
                    twitter_profile_name.text = x.result!!.get("username").toString()
                    twitter_login_button.visibility = View.GONE
                    twitter_logout_btn.visibility = View.VISIBLE
                }
        }
        twitter_logout_btn.setOnClickListener(){
            Log.d("Logout","Logout")
            val dbref = db.collection("users").document(uid!!).collection("accounts").document("twitter")
            dbref.delete()
            finish()
        }
        twitter_login_button.setCallback(object : Callback<TwitterSession?>() {


                    override fun success(result: Result<TwitterSession?>?) {

                        Log.d("Logging in", "")
                        // Do something with result, which provides a TwitterSession for making API calls
//                return
                        val session = TwitterCore.getInstance().sessionManager.activeSession
                        val authToken = session.authToken
                        val token = authToken.token
                        val secret = authToken.secret

                        val oauth = HashMap<String, String>()
                        oauth.put("uid", "" + session.userId)
                        oauth.put("username", session.userName)
                        oauth.put("access_token", token)
                        oauth.put("access_secret", secret)
                        val uid = getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_userId", "")
                        val db = FirebaseFirestore.getInstance()
                        Log.d("Ref","uid = "+uid!!)
                        val dbref = db.collection("users").document(uid!!).collection("accounts").document("twitter")
//                val dbref = db.collection("test").document("Test")
                        dbref.set(oauth).addOnCompleteListener { e ->
                            if (e.isSuccessful) {
                                twitter_login_button.visibility = View.GONE
                                finish()
//                        twitter_connected_container.visibility = View.VISIBLE
//                        twitter_connected_username.text = session.userName
//                        Toast.makeText(view.context, "Successful to connect to twitter", Toast.LENGTH_SHORT)
                            } else {
//                        Toast.makeText(view.context, "Failed to connect to twitter", Toast.LENGTH_SHORT)
                            }
                        }


                    }

                    override fun failure(exception: TwitterException?) {
                        // Do something on failure
                        Log.d("Logging in", "Failed")
                    }



                })



//        twitter_login_button.performClick()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the login button.
        twitter_login_button.onActivityResult(requestCode, resultCode, data)
    }
}