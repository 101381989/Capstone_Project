package com.example.cap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);


        String imageUrl = getIntent().getStringExtra("image_url");


        ImageView fullImageView = findViewById(R.id.fullImageView);
        Glide.with(this)
                .load(imageUrl)
                .into(fullImageView);
    }
}
