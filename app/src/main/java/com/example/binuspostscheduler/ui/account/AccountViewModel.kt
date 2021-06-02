package com.example.binuspostscheduler.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccountViewModel() : ViewModel() {

    private var mText: MutableLiveData<String>? = null


    init {
        this.mText = MutableLiveData()
        this.mText!!.value = "Account View Model"
    }


    fun getText(): LiveData<String>? {
        return mText
    }
}