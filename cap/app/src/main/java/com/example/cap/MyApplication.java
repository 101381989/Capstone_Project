package com.example.cap;
import android.app.Application;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
    }
}