package com.example.binuspostscheduler.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.activities.AddTwitterActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.account_fragment.*

class AccountFragment : Fragment() {


    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var viewModel: AccountViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        val root = inflater.inflate(R.layout.account_fragment, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val config = TwitterConfig.Builder(view.context)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig(getString(R.string.twitter_CONSUMER_KEY), getString(R.string.twitter_CONSUMER_SECRET)))
                .debug(true)
                .build()
        Twitter.initialize(config)

//        twitter_login_button.setOnClickListener{
//            Log.d("Clicked", "")
//            val activity = activity
////            val intent = Intent(LoginActivity.class, RegisterActivity.class)
////            startActivityForResult(Intent(), 111);
//        }
        add_twitter_btn.setOnClickListener({
            val intent = Intent(this.context,AddTwitterActivity::class.java)
            startActivity(intent)
        })


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Logging in", "Clicked")
        // Pass the activity result to the login button.
//        twitter_login_button.onActivityResult(requestCode, resultCode, data)
    }
}