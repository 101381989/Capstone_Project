package com.example.cap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private EditText editTextBio;
    private ImageView profileImageView;
    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextBio = findViewById(R.id.edit_bio);
        profileImageView = findViewById(R.id.edit_profile_image);

        Button saveButton = findViewById(R.id.save_button);
        Button selectImageButton = findViewById(R.id.select_image_button);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image gallery to select a new profile picture
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileChanges();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    if (imageBitmap != null) {
                        profileImageView.setImageBitmap(imageBitmap);
                        profileImageUri = selectedImage;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveProfileChanges() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            String newBio = editTextBio.getText().toString().trim();
            if (!TextUtils.isEmpty(newBio) || profileImageUri != null) {
                if (profileImageUri != null) {
                    uploadProfileImageToStorage();
                } else {
                    updateProfileInFirestore(null);
                }
            } else {
                Toast.makeText(EditProfileActivity.this, "No changes to save", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfileImageToStorage() {
        if (profileImageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference profileImagesRef = storageRef.child("profile_images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), profileImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = profileImagesRef.putBytes(data);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    profileImagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateProfileInFirestore(uri.toString());
                    }).addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateProfileInFirestore(String profileImageUrl) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            Map<String, Object> updates = new HashMap<>();
            String newBio = editTextBio.getText().toString().trim();
            if (!TextUtils.isEmpty(newBio)) {
                updates.put("bio", newBio);
            }
            if (profileImageUrl != null) {
                updates.put("profilePicUrl", profileImageUrl);
            }

            userRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
