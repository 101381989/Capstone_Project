package com.example.cap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView bioTextView;
    private TextView emailTextView;
    private ImageView userProfileImageView;
    private GridView imagesGridView;
    private FirebaseFirestore db;
    private List<String> userImageUrls;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Initialize views
        userNameTextView = findViewById(R.id.user_name);
        bioTextView = findViewById(R.id.bio);
        emailTextView = findViewById(R.id.email);
        userProfileImageView = findViewById(R.id.user_profile_image);
        imagesGridView = findViewById(R.id.images_grid_view);

        // Get user ID from intent
        String userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            db = FirebaseFirestore.getInstance();
            getUserDetailsFromFirestore(userId);
        } else {
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if user ID is not provided
        }
        imagesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get clicked image URL
                String imageUrl = userImageUrls.get(position);
                // Launch FullImageActivity to display the full-size image
                Intent intent = new Intent(UserDetailsActivity.this, FullImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            }
        });
    }

    // Method to retrieve user details from Firestore
    private void getUserDetailsFromFirestore(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieving user details
                        String userName = documentSnapshot.getString("username");
                        String bio = documentSnapshot.getString("bio");
                        String email = documentSnapshot.getString("email");
                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                        userImageUrls = (List<String>) documentSnapshot.get("imageUrls");

                        // Display user details
                        if (userName != null) userNameTextView.setText(userName);
                        if (bio != null) bioTextView.setText(bio);
                        if (email != null) emailTextView.setText(email);
                        if (profilePicUrl != null) {
                            Glide.with(this).load(profilePicUrl).into(userProfileImageView);
                        }

                        // Display user images
                        if (userImageUrls != null && !userImageUrls.isEmpty()) {
                            ImageAdapter adapter = new ImageAdapter();
                            imagesGridView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(UserDetailsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity if user not found
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserDetailsActivity.this, "Failed to fetch user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish(); // Close activity on failure
                });
    }

    // ImageAdapter to populate images in the GridView
    private class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userImageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return userImageUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // Create a new ImageView
                imageView = new ImageView(UserDetailsActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0); // Remove padding
            } else {
                imageView = (ImageView) convertView;
            }

            String imageUrl = userImageUrls.get(position);
            Glide.with(UserDetailsActivity.this)
                    .load(imageUrl)
                    .override(500, 500) // Adjust the image size
                    .into(imageView);

            // Click listener to open images in full screen

            return imageView;
        }
    }
}
