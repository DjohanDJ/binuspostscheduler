package com.example.binuspostscheduler.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.example.binuspostscheduler.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_create_post.*
import java.text.DateFormat
import java.util.*


class CreatePostFragment : Fragment() {

    private lateinit var previewImg: ImageView
    private lateinit var scheduled_time : RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewImg = img_preview

        insert_img_btn.setOnClickListener(View.OnClickListener {
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.setType("image/*")
            startActivityForResult(Intent.createChooser(i, "Choose Image"), 999)
        })
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)
        var hour = calendar.get(Calendar.HOUR);
        var minute = calendar.get(Calendar.MINUTE);
        val datePickerDialog = DatePickerDialog(view.context, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
            run {
                year = i
                month = i2+1
                day = i3

                val timePickerDialog = TimePickerDialog(view.context, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    run {
                        hour = i
                        minute = i2
                        Log.d("Test", " Year = " + year + " , month = " + month + ", day =" + day + " , hour "+hour+" , minute = "+minute)
                    }
                }, hour, minute, true)
                timePickerDialog.show()
            }
        }, year, month, day)

        post_schedule_radio_group.setOnCheckedChangeListener { _, i ->
            run {
                if (i == post_schedule_now.id) {
                    scheduled_time = post_schedule_now
                    datePickerDialog.hide()
                }
                if (i == post_schedule_later.id) {
                    scheduled_time = post_schedule_later
                    datePickerDialog.show()
                }
            }

        }

        schedule_post_btn.setOnClickListener(View.OnClickListener {
            if (scheduled_time == post_schedule_now){
                // Post Now
            }
            else {
                // Post Later
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
            Picasso.get().load(data.data!!).into(previewImg)
            previewImg.visibility = View.VISIBLE
        }
    }

}