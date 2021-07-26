package com.example.binuspostscheduler.fragments;

import androidx.fragment.app.Fragment;

import com.example.binuspostscheduler.models.Account;

import java.util.HashMap;

public interface CreatePostInterface {
    public void sendData(CreatePostInterface b);
    public void updateData(HashMap<String, Account> map);
}
