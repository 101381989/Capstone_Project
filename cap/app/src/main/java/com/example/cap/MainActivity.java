package com.example.cap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;

    FirebaseUser user;
    Button  tutorialButton;
    Button guideButton;
    Button dronesButton;
    Button portfolioButton;
    CardView photoStyles;
    private TextView feedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        photoStyles = findViewById(R.id.main_photos);
        button =findViewById(R.id.logout_btn);
        textView = findViewById(R.id.user_details);
        feedText = findViewById(R.id.feed_button);
        user = auth.getCurrentUser();
        if (user==null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        tutorialButton = findViewById(R.id.main_tutorial);
        guideButton = findViewById(R.id.main_Guide);
        dronesButton = findViewById(R.id.main_drones);
        portfolioButton = findViewById(R.id.main_portfolio);

        photoStyles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PhotoStyleMain.class);
                startActivity(intent);

            }
        });

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Tutorial.class);
                startActivity(i);


            }
        });

        guideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GuideCamera.class);
                startActivity(intent);


            }
        });

        dronesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DronesPage.class);
                startActivity(intent);



            }
        });

        portfolioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
                startActivity(intent);
            }
        });
        feedText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the FeedActivity when the feed TextView is clicked
                startActivity(new Intent(MainActivity.this, FeedActivity.class));
            }
        });

        }
}