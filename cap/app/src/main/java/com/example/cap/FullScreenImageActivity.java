package com.example.cap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FullScreenImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        ImageView imageView = findViewById(R.id.fullscreen_image);
        TextView userNameTextView = findViewById(R.id.user_name);
        TextView bioTextView = findViewById(R.id.bio);
        TextView emailTextView = findViewById(R.id.email);
        ImageView userProfileImageView = findViewById(R.id.user_profile_image);

        // Set click listener for the user profile image
        userProfileImageView.setOnClickListener(view -> {
            // Get the user ID associated with the clicked image
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .whereArrayContains("imageUrls", imageUrl)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String userId = document.getId();

                                // Open UserDetailsActivity with the user ID
                                Intent intent = new Intent(FullScreenImageActivity.this, UserDetailsActivity.class);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                            } else {
                                Toast.makeText(FullScreenImageActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FullScreenImageActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        Glide.with(this).load(imageUrl).into(imageView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereArrayContains("imageUrls", imageUrl)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String userName = document.getString("username");
                                String bio = document.getString("bio");
                                String email = document.getString("email");

                                userNameTextView.setText(userName != null ? userName : "");
                                bioTextView.setText(bio != null ? bio : "");
                                emailTextView.setText(email != null ? email : "");

                                String profilePicUrl = document.getString("profilePicUrl");
                                if (profilePicUrl != null) {
                                    Glide.with(FullScreenImageActivity.this)
                                            .load(profilePicUrl)
                                            .into(userProfileImageView);
                                }
                            } else {
                                Toast.makeText(FullScreenImageActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(FullScreenImageActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
