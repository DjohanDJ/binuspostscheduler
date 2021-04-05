package com.example.binuspostscheduler.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.fragments.CreatePostFragment
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        supportFragmentManager.beginTransaction().apply {
            replace(frame_test.id, CreatePostFragment())
            commit()
        }
    }

}