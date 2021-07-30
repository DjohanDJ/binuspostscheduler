package com.example.binuspostscheduler.activities;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.binuspostscheduler.R;
import com.example.binuspostscheduler.authentications.UserSession;
import com.example.binuspostscheduler.notification.NotificationBroadcast;
import com.example.binuspostscheduler.ui.account.AccountFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doInitiateVariables();

        createNotifChannel();
        process();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.super.startActivity(new Intent(MainActivity.this,CreatePostActivity.class));
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_account,R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TwitterConfig config =new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_CONSUMER_KEY), getString(R.string.twitter_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    private void doInitiateVariables() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
//        navUsername.setText(UserSession.getCurrentUser().getUsername());
        TextView navEmail = (TextView) headerView.findViewById(R.id.navEmail);
//        navEmail.setText(UserSession.getCurrentUser().getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Fragment","");
        Fragment fragment = getFragmentManager().findFragmentById(R.id.nav_account);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    void process(){
        Intent intent =  new Intent(MainActivity.this, NotificationBroadcast.class);
        intent.putExtra("user_id", UserSession.getCurrentUser().getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE),calendar.get(calendar.HOUR_OF_DAY),  calendar.get(calendar.MINUTE), 0);

        calendar.add(Calendar.MINUTE, 1);

        String now = "" + calendar.get(calendar.DATE) + "-" +  (calendar.get(calendar.MONTH) + 1)+"-"+calendar.get(calendar.YEAR) + " "
                + calendar.get(calendar.HOUR_OF_DAY) + ":" + calendar.get(calendar.MINUTE)  + ":00";

        Date date = null;

        try {
            date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int repeatEvery = 1000 * 60 * 1; //1 menit

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,  date.getTime(), repeatEvery, pendingIntent);
    }

    void createNotifChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "BinusReminderChannel";
            String desc =  "It's time to post";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel =  new NotificationChannel("notifChannel", name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}