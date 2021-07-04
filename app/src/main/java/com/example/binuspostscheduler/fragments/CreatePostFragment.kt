package com.example.binuspostscheduler.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binuspostscheduler.Adapter.AddMediaAdapter
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.helpers.RealPathHelper
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import twitter4j.StatusUpdate
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class CreatePostFragment : Fragment() {

    private lateinit var previewImg: ImageView
    private lateinit var scheduled_time : RadioButton
    private lateinit var db:FirebaseFirestore
    private lateinit var uid:String
    private lateinit var medias: ArrayList<File>
    private lateinit var rv:RecyclerView
    private lateinit var addMediaAdapter: AddMediaAdapter
    private var isVideo = false
    private lateinit var ctx : Context
    private lateinit var videoPath :String
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
        ctx = view.context
        medias = ArrayList<File>();
        db = FirebaseFirestore.getInstance()
        rv = add_media_rv
        uid =  view.context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_userId","")!!
        insert_img_btn.setOnClickListener(View.OnClickListener {
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.setType("*/*")
            val mimetypes = arrayOf("image/*", "video/*")
            i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
//            i.setType(
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
                month = i2 + 1
                day = i3

                val timePickerDialog = TimePickerDialog(view.context, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    run {
                        hour = i
                        minute = i2
                        Log.d("Test", " Year = " + year + " , month = " + month + ", day =" + day + " , hour " + hour + " , minute = " + minute)
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

            if (scheduled_time == post_schedule_now) {
                //anggap twitter dipilih
                db.collection("users").document(uid).collection("accounts").document("twitter").get().addOnCompleteListener{
                    res->
                    run {
                        if (res.isSuccessful) {
                            val access_token = res.getResult()!!.get("access_token") as String
                            val access_secret = res.getResult()!!.get("access_secret") as String
                            GlobalScope.launch {
                                val twitter: Twitter = TwitterFactory().instance

                                twitter.setOAuthConsumer(getString(R.string.twitter_CONSUMER_KEY),getString(R.string.twitter_CONSUMER_SECRET))
                                val accessToken = AccessToken(access_token,access_secret)
                                twitter.setOAuthAccessToken(accessToken)
                                val statusUpdate = StatusUpdate(post_content.text.toString())
                                val mediaIds = LongArray(medias.size)

                                if(!isVideo){
                                    for(idx in 0..medias.size-1){
                                        val upload = twitter.uploadMedia(medias[idx])
                                        mediaIds[idx] = upload.mediaId
                                    }
                                    statusUpdate.setMediaIds(*mediaIds)
                                }else{
                                    val mediaChunked = twitter.uploadMediaChunked(videoPath,File(videoPath).inputStream())
                                    statusUpdate.setMediaIds(mediaChunked.mediaId)
                                }

//                                Log.d("image",img.path)
                                twitter.updateStatus(statusUpdate)

                            }
                        }
                    }
                }

            } else {
                // Post Later
            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
//            img = File(data.data!!.toString())
            val path = RealPathHelper.getRealPath(this.context!!,data.data!!)
//            Log.d("Data","= "+data.data)
//            Log.d("Path","= "+)
            Log.d("PATH",path!!)
            val file = File(path)
            if (!file.isFile){
                Log.d("ISFILE","NOPE "+file.absolutePath)
            }
            else{
                val fileExt = path.substring(path.lastIndexOf(".")+1)
                var cancel = false

                if(fileExt.equals("mp4"))
                {
                    Log.d("Fileext","Video")

                    val alertDialogBuilder = AlertDialog.Builder(ctx)
                    alertDialogBuilder.setTitle("Uploading Video")
                    alertDialogBuilder.setMessage("You can only upload up to 4 images or a video. This will remove all existing images, do you understand?")
                    alertDialogBuilder.setPositiveButton("Yes"){
                        dialog, which ->
                        run {
                            isVideo = true;
                            medias.clear()
                            medias.add(file)
                            val adapter = AddMediaAdapter(context, medias, this, isVideo)

                            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            rv.adapter = adapter
                            adapter.notifyDataSetChanged()
                            if (medias.size == 4) // max for twitter
                            {
                                insert_img_btn.isEnabled = false
                            }
                            checkMediaStatus()
                            videoPath = path
                        }
                    }
                    alertDialogBuilder.setNegativeButton("No"){
                        dialog, which -> cancel = true
                    }
                    alertDialogBuilder.show()
                }
                else{
                    if(isVideo){
                        val alertDialogBuilder = AlertDialog.Builder(ctx)
                        alertDialogBuilder.setTitle("Uploading Video")
                        alertDialogBuilder.setMessage("You can only upload up to 4 images or a video. This will remove existing video, do you understand?")
                        alertDialogBuilder.setPositiveButton("Yes"){
                            dialog, which ->
                            run {
                                isVideo = false;
                                medias.clear()
                                medias.clear()
                                medias.add(file)
                                val adapter = AddMediaAdapter(context, medias, this, isVideo)

                                rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                rv.adapter = adapter
                                adapter.notifyDataSetChanged()
                                if (medias.size == 4) // max for twitter
                                {
                                    insert_img_btn.isEnabled = false
                                }
                                checkMediaStatus()

                            }
                        }
                        alertDialogBuilder.setNegativeButton("No"){
                            dialog, which -> cancel = true
                        }
                        alertDialogBuilder.show()
                    }
                    else{

                        medias.add(file)
                        val adapter = AddMediaAdapter(context, medias, this, isVideo)

                        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        rv.adapter = adapter
                        adapter.notifyDataSetChanged()
                        if (medias.size == 4) // max for twitter
                        {
                            insert_img_btn.isEnabled = false
                        }
                        checkMediaStatus()
                    }

                }



            }
        }
    }

    fun checkMediaStatus(){
        if (medias.isEmpty())rv.visibility = View.GONE
        else rv.visibility = View.VISIBLE
    }

    fun removeMedia(idx: Int){
        medias.removeAt(idx)
        checkMediaStatus()
    }
}