package com.example.binuspostscheduler.authentications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class SingletonFirebaseTool {

    private FirebaseAuth myFirebaseAuth;
    private FirebaseFirestore myFireStoreReference;
    private FirebaseStorage myStorageReference;
    private static SingletonFirebaseTool instance = null;

    private SingletonFirebaseTool() {
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFireStoreReference = FirebaseFirestore.getInstance();
        myStorageReference = FirebaseStorage.getInstance();
    }

    public static SingletonFirebaseTool getInstance() {
        if (instance == null) return instance = new SingletonFirebaseTool();
        return instance;
    }

    public FirebaseAuth getMyFirebaseAuth() {
        return myFirebaseAuth;
    }

    public FirebaseFirestore getMyFireStoreReference() {
        return myFireStoreReference;
    }

    public FirebaseStorage getMyStorageReference() {
        return myStorageReference;
    }

}
