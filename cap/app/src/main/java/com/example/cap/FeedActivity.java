package com.example.cap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends Activity {
    private GridView imagesGridView;
    private List<String> imageUrls;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        imagesGridView = findViewById(R.id.feed_images_grid);
        imageUrls = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Fetch image URLs from Firestore
        getAllImageUrlsFromFirestore();
        imagesGridView.setOnItemClickListener((parent, view, position, id) -> {
            String imageUrl = imageUrls.get(position);
            // Open full-screen image view with details
            Intent intent = new Intent(FeedActivity.this, FullScreenImageActivity.class);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        });
    }

    // Method to retrieve all image URLs from Firestore
    private void getAllImageUrlsFromFirestore() {
        db.collection("users")  // Assuming 'users' is the collection containing user documents
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        List<String> userImageUrls = (List<String>) documentSnapshot.get("imageUrls");
                        if (userImageUrls != null && !userImageUrls.isEmpty()) {
                            imageUrls.addAll(userImageUrls);
                        }
                    }

                    ImageAdapter adapter = new ImageAdapter();
                    imagesGridView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FeedActivity.this, "Failed to fetch image URLs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
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
                imageView = new ImageView(FeedActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0); // Remove padding
            } else {
                imageView = (ImageView) convertView;
            }

            String imageUrl = imageUrls.get(position);
            Glide.with(FeedActivity.this)
                    .load(imageUrl)
                    .override(500, 500) // Adjust the image size
                    .into(imageView);

            return imageView;
        }
    }
}
