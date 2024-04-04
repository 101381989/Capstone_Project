package com.example.cap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoStyleMain extends AppCompatActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setTitle("Photos");

        setContentView(R.layout.activity_photostylemain);

        gridView = findViewById(R.id.grid_view);
        gridView.setAdapter(new photostylepage3(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), photostylepage1.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });
    }
}
