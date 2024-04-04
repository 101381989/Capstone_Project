package com.example.cap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class photostylepage1 extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photostyle_full);


        setTitle("Full Screen");

        imageView = findViewById(R.id.image_view);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent i = getIntent();
        int position = i.getExtras().getInt("id");

        photostylepage3 imageAdapter = new photostylepage3(this);

        imageView.setImageResource(imageAdapter.imageArray[position]);
    }
}
