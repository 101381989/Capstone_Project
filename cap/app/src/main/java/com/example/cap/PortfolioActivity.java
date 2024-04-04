package com.example.cap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PortfolioActivity extends Activity {
    private static final int REQUEST_IMAGE_PICK = 102;
    private GridView uploadedImagesGrid;
    private ArrayList<String> imageUrls;
    private TextView uploadCountText;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid();
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            TextView currentUserInfo = findViewById(R.id.current_user_info);
            currentUserInfo.setText("Current User: " + userName + " (" + userEmail + ")");


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String profilePicUrl = documentSnapshot.getString("profilePicUrl");
                            String bio = documentSnapshot.getString("bio");

                            // Set profile picture
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(this)
                                        .load(profilePicUrl)
                                        .placeholder(R.drawable.default_user_image)
                                        .error(R.drawable.default_user_image)
                                        .into((ImageView) findViewById(R.id.user_bio_image));
                            }

                            TextView bioTextView = findViewById(R.id.user_bio);
                            if (bio != null && !bio.isEmpty()) {
                                bioTextView.setText(bio);
                            } else {
                                bioTextView.setText("");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PortfolioActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    });

            Button editProfileButton = findViewById(R.id.edit_user_button);
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PortfolioActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            });

            Button uploadImageButton = findViewById(R.id.upload_button);
            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickImageFromGallery();
                }
            });
        }
        uploadedImagesGrid = findViewById(R.id.uploaded_images_grid);
        imageUrls = new ArrayList<>();
        uploadCountText = findViewById(R.id.user_image_count);
        db = FirebaseFirestore.getInstance();
        ImageAdapter adapter = new ImageAdapter();
        uploadedImagesGrid.setAdapter(adapter);
        uploadedImagesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get clicked image URL
                String imageUrl = imageUrls.get(position);
                // Launch FullImageActivity to display the full-size image
                Intent intent = new Intent(PortfolioActivity.this, FullImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            }
        });

        loadImagesFromFirestore();

        Button uploadImageButton = findViewById(R.id.upload_button);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }
    @SuppressLint("QueryPermissionsNeeded")
    private void pickImageFromGallery() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebaseStorage(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");
        StorageReference imageRef = storageRef.child(userId + "_" + System.currentTimeMillis() + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Update Firestore with the image URL
                        updateFirestoreWithImageUrl(imageUrl);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(PortfolioActivity.this, "Failed to retrieve download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadError", "Failed to upload image", e);
                });
    }

    private void updateFirestoreWithImageUrl(String imageUrl) {
        // Get user document reference
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> userImageUrls = (List<String>) documentSnapshot.get("imageUrls");
                        if (userImageUrls == null) {
                            userImageUrls = new ArrayList<>();
                        }
                        // Add the new image URL to the list
                        userImageUrls.add(imageUrl);
                        // Update the user document with the updated image URLs list
                        db.collection("users").document(userId)
                                .update("imageUrls", userImageUrls)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PortfolioActivity.this, "Image uploaded and Firestore updated successfully", Toast.LENGTH_SHORT).show();
                                    // Load images from Firestore again to refresh the grid
                                    loadImagesFromFirestore();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PortfolioActivity.this, "Failed to update Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PortfolioActivity.this, "Failed to retrieve user data from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadImagesFromFirestore() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> userImageUrls = (List<String>) documentSnapshot.get("imageUrls");
                        if (userImageUrls != null) {
                            imageUrls.clear();
                            imageUrls.addAll(userImageUrls);
                            ImageAdapter adapter = new ImageAdapter();
                            uploadedImagesGrid.setAdapter(adapter);
                            uploadCountText.setText("Uploads: " + imageUrls.size());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PortfolioActivity.this, "Failed to load images from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // Create a new ImageView
                imageView = new ImageView(PortfolioActivity.this);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0); // Remove padding
            } else {
                imageView = (ImageView) convertView;
            }

            String imageUrl = imageUrls.get(position);
            Glide.with(PortfolioActivity.this)
                    .load(imageUrl)
                    .override(500, 500) // Adjust the image size
                    .into(imageView);

            return imageView;
        }
    }
}
