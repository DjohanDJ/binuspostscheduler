package com.example.binuspostscheduler.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.binuspostscheduler.Adapter.SelectAccountAdapter
import com.example.binuspostscheduler.R
import com.example.binuspostscheduler.activities.CreatePostActivity
import com.example.binuspostscheduler.models.Account
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_select_account.*
import java.util.HashMap


class SelectAccountFragment : BaseFragment(),CreatePostInterface {

    private lateinit var db: FirebaseFirestore
    private lateinit var uid:String
    private lateinit var ctx : Context
    private lateinit var accounts:ArrayList<Account>
    private lateinit var adapter:SelectAccountAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        select_account_btn.isEnabled = false
        accounts = ArrayList()
        uid =  view.context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user_userId","")!!
        ctx = view.context
        db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).collection("accounts").get().addOnSuccessListener {
            documents -> for(document in documents){

            if (document.id.equals("twitter")){
                val acc = Account()
                acc.access_secret = document.get("access_secret") as String
                acc.username = document.get("username") as String
                acc.access_token = document.get("access_token") as String
                acc.uid = document.get("uid") as String
                acc.type = document.id
                Log.d("Done", "Done")
                accounts.add(acc)
            }

        }
            adapter = SelectAccountAdapter(accounts,ctx,this)
            select_account_recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            select_account_recycler_view.adapter =adapter
            adapter.notifyDataSetChanged()

        }
        select_account_btn.setOnClickListener{
            (activity as CreatePostActivity).send(0)
            (activity as CreatePostActivity).changeFragment(0)

        }
    }

    override fun sendData( b: CreatePostInterface?) {
        b!!.updateData(adapter.map)
    }

    override fun updateData(map: HashMap<String, Account>?) {
//        TODO("Not yet implemented")
    }
    fun changeButton(changed: Boolean){
        select_account_btn.isEnabled = changed
    }
}