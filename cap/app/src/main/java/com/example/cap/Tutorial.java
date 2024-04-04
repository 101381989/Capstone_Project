package com.example.cap;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class Tutorial extends AppCompatActivity {

    // Placeholder URLs for presets
    private static final String[] PRESET_URLS = {
            "https://thepresetsroom.com/download.php?token=63377597c3da4c5fa4c5b29f8eedba7b14dacd5d834f4ddaa9d67b1d5b212ecf&file=film-airy;",
            "https://thepresetsroom.com/download.php?token=bffb5292f18fae2ee8f7e93c11a88478d32a59eb2fbc30394090b309c99d7b0a&file=focus-contrast;",
            "https://thepresetsroom.com/download.php?token=5ff37c55068f468a21ef045e0487b461d14484dd392755624c8987eae074e545&file=hdr-gopro",
            "https://thepresetsroom.com/download.php?token=afcc5d5a681a943b6f3012edd3cb47dfa6024a2c2fc114223aa5a4533b3af35c&file=old-times"
    };

    // Descriptions for each preset
    private static final String[] PRESET_DESCRIPTIONS = {
            "Add a touch of vintage charm and achieve a pure and simple effect with this preset",
            "Preset for product close-ups that accentuates all the details and textures",
            "An HDR preset specifically designed for GoPro and action camera photos",
            "Highlight the intricate details and expressions in your photos with this black and white preset"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // Set onClickListeners for preset download buttons
        for (int i = 0; i < PRESET_URLS.length; i++) {
            int presetIndex = i + 1; // Preset index starts from 1
            Button downloadPresetButton = findViewById(getResources().getIdentifier("download_preset" + presetIndex + "_button", "id", getPackageName()));
            int finalI = i;
            downloadPresetButton.setOnClickListener(view -> downloadPreset(PRESET_URLS[finalI], PRESET_DESCRIPTIONS[finalI]));
        }

        // Embedding the five YouTube videos into WebViews
        initializeWebView(findViewById(R.id.webView1), "V7z7BAZdt2M");
        initializeWebView(findViewById(R.id.webView2), "LxO-6rlihSg");
        initializeWebView(findViewById(R.id.webView3), "lHcA7pPwYZY");
        initializeWebView(findViewById(R.id.webView4), "mPvJkhnB4xg");
        initializeWebView(findViewById(R.id.webView5), "rSq3gD-aiU4");
    }

    private void initializeWebView(WebView webView, String videoId) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Load video into WebView
        webView.loadData(getYouTubeHTML(videoId), "text/html", "utf-8");
    }

    private String getYouTubeHTML(String videoId) {
        return "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";
    }

    private void downloadPreset(String presetUrl, String presetDescription) {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(presetUrl)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {

                    Toast.makeText(Tutorial.this, "Failed to download preset", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                byte[] bytes = response.body().bytes();

                runOnUiThread(() -> {

                    Toast.makeText(Tutorial.this, "Preset downloaded successfully", Toast.LENGTH_SHORT).show();


                });
            }
        });
    }
}
