package com.example.binuspostscheduler.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.binuspostscheduler.Adapter.CreatePostAdapter
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.fragments.BaseFragment
import com.example.binuspostscheduler.fragments.CreatePostFragment
import com.example.binuspostscheduler.fragments.SelectAccountFragment


class CreatePostActivity : AppCompatActivity() {
    val fragments = ArrayList<BaseFragment>()
    val fm = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

//        fragments.add(SelectAccountFragment())
        fragments.add(CreatePostFragment())
        val adapter = CreatePostAdapter(supportFragmentManager)
        adapter.fragments = fragments

        fm.beginTransaction().replace(R.id.create_post_frame_layout,fragments[0]).commit()

        Log.d("Fragment Count",""+fm.backStackEntryCount)
//        create_post_view_pager.adapter = adapter
//
//        create_post_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
//
//
//
//            override fun onPageSelected(position: Int) {
//                if (position == 0){
//                    fragments[1].sendData(fragments[0]);
//                }else{
//                    fragments[0].sendData(fragments[1]);
//                }
//            }
//        })
//
//        create_post_view_pager.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//                create_post_scroll_view.requestDisallowInterceptTouchEvent(true)
//                return false
//            }
//        })
//        fragments[0].select_account_btn.setOnClickListener{
//            supportFragmentManager.beginTransaction().replace(R.id.create_post_frame_layout,fragments[1]).commit()
//        }

    }
    fun changeFragment(src: Int) {
        if(src == 0){
            fm.beginTransaction().replace(R.id.create_post_frame_layout,fragments[1]).addToBackStack(null).commit()

            Log.d("Fragment Count",""+supportFragmentManager.backStackEntryCount)
        }
    }

    fun send(src:Int){
        if(src == 1)
        {
            fragments[1].sendData(fragments[0]);
        }else{
            fragments[0].sendData(fragments[1])
        }
    }

    override fun onBackPressed() {
        if (fm.backStackEntryCount > 0 ){
            fm.popBackStack()
        }else{
            super.onBackPressed()
        }
    }
}


