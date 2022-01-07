package com.example.binuspostscheduler.fragments

//import com.ko.twitter.vplay.core.StatusUpdate

//import twitter4j.TwitterFactory
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.binuspostscheduler.Adapter.AddMediaAdapter
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.activities.CreatePostActivity
import com.example.binuspostscheduler.activities.MainActivity
import com.example.binuspostscheduler.helpers.RealPathHelper
import com.example.binuspostscheduler.interfaces.AddMediaInterface
import com.example.binuspostscheduler.models.Account
import com.example.binuspostscheduler.models.NewSchedule
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.apache.commons.lang3.RandomStringUtils
import twitter4j.StatusUpdate
import twitter4j.auth.AccessToken
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CreatePostFragment : BaseFragment(),CreatePostInterface,AddMediaInterface  {

    private lateinit var previewImg: ImageView
    private lateinit var scheduled_time : RadioButton
    private lateinit var db:FirebaseFirestore
    private lateinit var uid:String
    private lateinit var medias: ArrayList<File>
    private lateinit var mediaPaths : ArrayList<Uri>
    private lateinit var rv:RecyclerView
    private lateinit var addMediaAdapter: AddMediaAdapter
    private var isVideo = false
    private lateinit var ctx : Context
    private lateinit var videoPath :String
    private lateinit var laterTime: Date
    private lateinit var tags : ArrayList<String>
    private lateinit var accounts : ArrayList<Account>
    private lateinit var accTypes : ArrayList<String>
    private lateinit var storage :FirebaseStorage
    private lateinit var schedule_type: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        added_tags_container.removeAllViews()

        mediaPaths = ArrayList()
        storage = FirebaseStorage.getInstance()
//        accounts = ArrayList()
        ctx = view.context
        medias = ArrayList<File>();
        db = FirebaseFirestore.getInstance()
        rv = add_media_rv
        uid =  view.context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_userId","")!!
        laterTime = Date()
        tags = ArrayList()
        val daily = arrayOf("Once","Daily","Weekly","Monthly","Yearly")
        val times = arrayOf("1","2","3","4","5","6","7","8","9","10","Always")
        val dailyAdapter = ArrayAdapter<String>(ctx,R.layout.support_simple_spinner_dropdown_item,daily);

        val timesAdapter = ArrayAdapter<String>(ctx,R.layout.support_simple_spinner_dropdown_item,times);
        schedule_later_spinner_daily.adapter = dailyAdapter
        schedule_later_spinner_times.adapter = timesAdapter
        schedule_later_container.visibility= View.GONE
        insert_img_btn.setOnClickListener(View.OnClickListener {
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.setType("image/*")
//            val mimetypes = arrayOf("image/*", "video/*")
//            i.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
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
                val calendar = Calendar.getInstance()
                year = i
                month = i2 + 1
                day = i3

                val timePickerDialog = TimePickerDialog(view.context, TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    run {
                        hour = i
                        minute = i2
                        calendar.set(year,month-1,day,hour,minute)
                        laterTime = calendar.time
                        schedule_later_description.text = laterTime.toGMTString()
                        Log.d("Test", " Year = " + year + " , month = " + month + ", day =" + day + " , hour " + hour + " , minute = " + minute)
                    }
                }, hour, minute, true)
                timePickerDialog.show()
            }
        }, year, month, day)

        post_schedule_radio_group.setOnCheckedChangeListener { _, i ->
            run {
                if (i == post_schedule_now.id) {
                    schedule_later_container.visibility= View.GONE
                    scheduled_time = post_schedule_now
                    datePickerDialog.hide()
                }
                if (i == post_schedule_later.id) {

                    schedule_later_container.visibility= View.VISIBLE
                    scheduled_time = post_schedule_later
                    datePickerDialog.show()
                }
            }

        }

        schedule_post_btn.setOnClickListener(View.OnClickListener {
//            if(accounts.isEmpty() || scheduled_time == null){
                if( scheduled_time == null){
                Toast.makeText(ctx,"Please Choose when to post first",Toast.LENGTH_SHORT).show()
//                return;
            }
            else if(post_content.text.toString().isEmpty() && medias.isEmpty()){
                Toast.makeText(ctx,"Please Put some content to the post first",Toast.LENGTH_SHORT).show()
            }

           else{
                if (scheduled_time == post_schedule_now) {
                //anggap twitter dipilih
                db.collection("users").document(uid).collection("accounts").document("twitter").get().addOnCompleteListener{
                    res->
                    run {
                        if (res.isSuccessful) {
                            for (account in accounts){
                                if(account.type.equals("twitter"))postTwitter(account)
                                else postFacebook(account)
                            }
                        }
                    }
                }

            } else if(!mediaPaths.isEmpty()) {

//                // Post Later
                val img_paths = ArrayList<String>()
                GlobalScope.launch{
                    for(media in mediaPaths){
                        val randomString = RandomStringUtils.randomAlphanumeric(24);
                        val ref =storage.reference.child("images/$randomString")
                        val downloadUrl = uploadImage(ref,media)
                        img_paths.add(downloadUrl)
//                        Log.d("URL",downloadUrl)
//                        ref.putFile(media).addOnCompleteListener() {
//                            task ->
//                            run {
//                                if(task.isSuccessful)
//                                {
//                                    ref.downloadUrl.addOnSuccessListener { uri->Log.d("URL",uri.toString()) }
//                                }
//                            }
//
//                        }

                    }
                    val scheduled_time = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(laterTime)
                    val id = RandomStringUtils.randomAlphanumeric(20)
//                    val scheduled_post = NewSchedule(post_content.text.toString(), scheduled_time, uid, "", schedule_type,tags, img_paths, id)
                    val scheduled_post = NewSchedule(post_content.text.toString(), scheduled_time, uid, "", schedule_type,tags, img_paths,id,accTypes)
                    db.collection("schedules").document(id).set(scheduled_post).addOnCompleteListener{
                        task ->
                        run {
                            if (task.isSuccessful) {
                                Toast.makeText(ctx,"Post Scheduled!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(super.getContext()!!.applicationContext, MainActivity::class.java))
                            } else {
                                Toast.makeText(ctx,"Post Failed to schedule", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

//                val scheduled_post = NewSchedule(post_content_text.text.toString(), laterTime.toGMTString(), uid, "", tags, ArrayList<String> image, ArrayList<Account> selected_id)
//
//                db.collection("schedules").add(scheduled_post)
            }
                else{
                    Toast.makeText(ctx,"Minimum 1 image is required to schedule a post with facebook",Toast.LENGTH_SHORT).show()
                }
            }
        })

        add_tags_icon.setOnClickListener {
            if (add_tags_placeholder.text.toString().trim().isEmpty() || add_tags_placeholder.text.toString().trim().contains(" ")){
                Toast.makeText(ctx, "Tags cannot be empty or contains space",Toast.LENGTH_SHORT).show()
            }
            else{
                tags.add("#"+add_tags_placeholder.text.toString())

                val tv = TextView(ctx)
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tv.layoutParams = params
                tv.setText("#"+add_tags_placeholder.text.toString())
                tv.setTextColor(R.color.blue)
                tv.textSize = 14F
                added_tags_container.addView(tv)
                add_tags_placeholder.setText("")
            }
        }
        schedule_later_spinner_daily.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    schedule_later_spinner_times.visibility = View.GONE
                }else{
                    schedule_later_spinner_times.visibility = View.VISIBLE
                }
                schedule_type = schedule_later_spinner_daily.selectedItem as String
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("Inserting Image","Inserting..")
        if(data == null)Log.d("Inserting Image","Null Image")

        if(requestCode == 999 && resultCode == Activity.RESULT_OK && data != null){
//            img = File(data.data!!.toString())
            var path = RealPathHelper.getRealPath(this.context!!,data.data!!)
//            Log.d("Data","= "+data.data)
//            Log.d("Path","= "+)

            Log.d("data",Uri.parse(data.data!!.toString()).toString())
//            Log.d("PATH",path!!)
//            path = data.data!!.toString()
            val file = File(path)
            if (!file.isFile){
                Log.d("ISFILE","NOPE "+file.absolutePath)
            }
            else{
                Log.d("Inserting Image","0")
                mediaPaths.add(data.data!!)
                val fileExt = path?.substring(path.lastIndexOf(".")+1)
                var cancel = false

                if(fileExt.equals("mp4"))
                {
                    Log.d("Inserting Image","3")
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
                            videoPath = path!!
                        }
                    }
                    alertDialogBuilder.setNegativeButton("No"){
                        dialog, which -> cancel = true
                    }
                    alertDialogBuilder.show()
                }
                else{
                    if(isVideo){
                        Log.d("Inserting Image","2")
                        val alertDialogBuilder = AlertDialog.Builder(ctx)
                        alertDialogBuilder.setTitle("Uploading Image")
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

                        checkMediaStatus()
                        Log.d("Inserting Image","1")
                    }

                }



            }
        }
    }

    fun checkMediaStatus(){
        if (medias.isEmpty())rv.visibility = View.GONE
        else rv.visibility = View.VISIBLE

        insert_img_btn.isEnabled = medias.size != 4
    }

    override fun removeMedia(idx: Int){
        medias.removeAt(idx)
        checkMediaStatus()
    }

    override fun sendData( b: CreatePostInterface?) {
//        TODO("Not yet implemented")
    }

    override fun updateData(map: HashMap<String, Account>?) {
        Log.d("Updating","...")
        if (map != null) {
            accounts = ArrayList()
            accTypes = ArrayList()
            if(map.get("facebook") != null){


                add_tags_placeholder_container.getChildAt(0).isEnabled = true
                add_tags_placeholder_container.getChildAt(1).isEnabled = true
            }else{

//                add_tags_placeholder_container.getChildAt(0).isEnabled = false
//                add_tags_placeholder_container.getChildAt(1).isEnabled = false
            }
            for ((k, v) in map) {
                accounts.add(v)
                accTypes.add(k)
                Log.d("MAP","$k , ${v.username}")
            }
//            Log.d("ACcount",)
//            schedule_post_btn.isEnabled = true
        }
        else{
//            schedule_post_btn.isEnabled = false
        }
    }

    fun postTwitter( account: Account){
        GlobalScope.launch {
            val access_token = account.access_token
            val access_secret = account.access_secret
//                                val access_token = "610590036-1LgDbleMW2H5jViN84Ej4BN1RgwKy2cfxgylibPW"
//                            val access_secret = "qPBbrdDlcoA9s0NLZrPgUZZjbhseKVs4KqsyQVryBX6gG"

            val cb = twitter4j.conf.ConfigurationBuilder()

            cb.setDebugEnabled(true)
            cb.setOAuthConsumerKey(getString(R.string.twitter_CONSUMER_KEY))
            cb.setOAuthConsumerSecret(getString(R.string.twitter_CONSUMER_SECRET))
            val twToken = AccessToken(access_token,access_secret)
            val twitterFactory = twitter4j.TwitterFactory(cb.build())
            val twitter = twitterFactory.getInstance()

//                                twitter.setOAuthConsumer(getString(R.string.twitter_CONSUMER_KEY),getString(R.string.twitter_CONSUMER_SECRET))
//                                val accessToken = AccessToken(access_token,access_secret)
            twitter.setOAuthAccessToken(twToken)
            val statusUpdate = StatusUpdate(post_content.text.toString())
            val mediaIds = LongArray(medias.size)

            if(!isVideo && !medias.isEmpty()){
                for(idx in 0..medias.size-1){
                    val upload = twitter.uploadMedia(medias[idx])
                    mediaIds[idx] = upload.mediaId
                }

                statusUpdate.setMediaIds(*mediaIds)

            }else if(!medias.isEmpty()){
                val chunk_size = 3 *1024 * 1024 // 3 MB
//                                    var byteArrayInputStream = ByteArrayInputStream(FileInputStream(videoPath),chunk_size)
                var bufferedReader = BufferedInputStream(FileInputStream(videoPath),chunk_size)
                var segment = ByteArray(chunk_size)
                val segmentIndex = 0;
                do{
                    var bytesRead = bufferedReader.readBytes()
                    if(bytesRead.size <=0)break
                    Log.d("bytes",""+bytesRead.toString())
//                                        val byteArrayInputStream = ByteArrayInputStream(segment, 0, bytesRead)

                }while(true)
                var length =videoPath.split("/").size
                var name = videoPath.split("/")[length-1].split(".")[0]
                Log.d("vpath",name)
                val result = twitter.uploadMediaChunked(name,FileInputStream(videoPath))


//                                    val mediaChunked = twitter.uploadMediaChunked(videoPath,File(videoPath).inputStream())
                statusUpdate.setMediaIds(result.mediaId)

            }

//                                Log.d("image",img.path)
            val status=twitter.updateStatus(statusUpdate)

            val statusUrl =  "https://twitter.com/" + status.user.screenName
                    .toString() + "/status/" + status.id
            Log.d("Status URl",statusUrl)
            val alertDialogBuilder = AlertDialog.Builder(ctx)
            alertDialogBuilder.setTitle("Upload Successful")
            alertDialogBuilder.setMessage("Upload Succesful")
            alertDialogBuilder.setPositiveButton("View Tweets"){
                dialog, which ->
                run {
                    val url = statusUrl
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            }
            alertDialogBuilder.setNegativeButton("Ok"){
                dialog, which -> {activity!!.finish()}
            }
            activity!!.runOnUiThread(Runnable {
                alertDialogBuilder.show()
            })

        }
    }
    fun postFacebook(account: Account){

    }

    suspend fun uploadImage(ref: StorageReference, media:Uri): String {
        val res = ref.putFile(media).await()
        val uri = ref.downloadUrl.await()
        return uri.toString()
//        ref.putFile(media).addOnCompleteListener() {
//                            task ->
//                            run {
//                                if(task.isSuccessful)
//                                {
//                                    ref.downloadUrl.addOnSuccessListener { uri->Log.d("URL",uri.toString()) }
//                                }
//                            }
//
//                        }
    }
}