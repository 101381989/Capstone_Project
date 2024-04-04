package com.example.cap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DronesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drones_main);

        // Banner
        ImageView bannerImageView = findViewById(R.id.bannerImageView);
        bannerImageView.setImageResource(R.drawable.banner_image);


        // Set product details for Product 1
        setProductDetails(R.id.product1ImageView, R.id.productName1TextView,
                R.id.buyButton1, R.id.productLink1TextView, "Product 1", "https://geprc.com/product/geprc-cinelog20-hd-o3-fpv-drone/", R.drawable.product1_image);

        // Set product details for Product 2
        setProductDetails(R.id.product2ImageView, R.id.productName2TextView,
                R.id.buyButton2, R.id.productLink2TextView, "Product 2", "https://geprc.com/product/geprc-domain3-6-hd-o3-freestyle-fpv-drone/", R.drawable.product2_image);

        // Set product details for Product 3
        setProductDetails(R.id.product3ImageView, R.id.productName3TextView,
                R.id.buyButton3, R.id.productLink3TextView, "Product 3", "https://geprc.com/product/geprc-cinebot30-hd-o3-fpv-drone/", R.drawable.product3_image);

        // Set product details for Product 4
        setProductDetails(R.id.product4ImageView, R.id.productName4TextView,
                R.id.buyButton4, R.id.productLink4TextView, "Product 4", "https://geprc.com/product/geprc-moz7-hd-o3-long-range-fpv/", R.drawable.product4_image);
    }

    private void setProductDetails(int imageViewId, int nameTextViewId, int buyButtonId, int linkTextViewId, final String productName, final String buyUrl, int productImageId) {
        ImageView productImageView = findViewById(imageViewId);
        TextView productNameTextView = findViewById(nameTextViewId);
        Button buyButton = findViewById(buyButtonId);
        TextView productLinkTextView = findViewById(linkTextViewId);

        // Set product details
        productImageView.setImageResource(productImageId);
        productNameTextView.setText(productName);
        productLinkTextView.setText("Learn more about " + productName);

        // Set up click listener for Buy button
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent with ACTION_VIEW and the buyUrl
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(buyUrl));

                // Check if there's an app to handle the Intent before starting it
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // If there's no app to handle the Intent, show an error message
                    Toast.makeText(DronesPage.this, "No app found to handle this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up click listener for Product Link
        productLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace "https://example.com" with your actual product link
                String productUrl = "https://store.dji.com/ca/product/dji-mini-4-pro-combo-rc-2?vid=148641";

                // Create an Intent with ACTION_VIEW and the productUrl
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(productUrl));

                // Check if there's an app to handle the Intent before starting it
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // If there's no app to handle the Intent, show an error message
                    Toast.makeText(DronesPage.this, "No app found to handle this action", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
