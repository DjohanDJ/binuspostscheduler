package com.example.binuspostscheduler.authentications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SingletonFirebaseTool {

    private FirebaseAuth myFirebaseAuth;
    private FirebaseFirestore myFireStoreReference;
    private StorageReference myStorageReference;
    private static SingletonFirebaseTool instance = null;

    private SingletonFirebaseTool() {
        myFirebaseAuth = FirebaseAuth.getInstance();
        myFireStoreReference = FirebaseFirestore.getInstance();
        myStorageReference = FirebaseStorage.getInstance().getReference("materials");
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

    public StorageReference getMyStorageReference() {
        return myStorageReference;
    }

}
