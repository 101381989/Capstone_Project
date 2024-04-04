package com.example.cap;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GuideCamera extends AppCompatActivity {

    private GestureDetector gestureDetector1;
    private GestureDetector gestureDetector2;
    private GestureDetector gestureDetector3;
    private GestureDetector gestureDetector4;
    private GestureDetector gestureDetector5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_main);

        initializeWebView(findViewById(R.id.webView1), "ncOVwaOoAXo");
        initializeWebView(findViewById(R.id.webView2), "zSxIbZFsn-g");
        initializeWebView(findViewById(R.id.webView3), "3gCKcq4THNQ");
        initializeWebView(findViewById(R.id.webView4), "tU8BuomMd-4");
        initializeWebView(findViewById(R.id.webView5), "gJnIT0WHD44");
    }

    private void initializeWebView(WebView webView, String videoId) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load video into WebView
        webView.loadDataWithBaseURL("https://www.youtube.com", getYouTubeHTML(videoId), "text/html", "utf-8", null);

        // Initialize gesture detector for double-tap
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Handle double-tap event here
                toggleVideoPlayback(webView);
                return true;
            }
        };

        // Create a gesture detector with the listener
        GestureDetector detector = new GestureDetector(this, gestureListener);

        // Attach the gesture detector to the WebView
        webView.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
    }

    private String getYouTubeHTML(String videoId) {
        return "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId +
                "?controls=0&showinfo=0&modestbranding=1\" frameborder=\"0\" allowfullscreen></iframe>";
    }

    private void toggleVideoPlayback(WebView webView) {
        // Implement logic to toggle video playback (e.g., play/pause)
        // For example, you can use the YouTube API to control video playback programmatically
        Toast.makeText(this, "Double-tap detected. Implement your logic here.", Toast.LENGTH_SHORT).show();
    }
}
