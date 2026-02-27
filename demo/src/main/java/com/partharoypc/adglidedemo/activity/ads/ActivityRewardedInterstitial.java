package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;

public class ActivityRewardedInterstitial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_interstitial);

        setupToolbar();
        initViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rewarded Interstitial");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        Button btnShow = findViewById(R.id.btn_show);

        // Hide load btn or repurpose it
        Button btnLoad = findViewById(R.id.btn_load);
        btnLoad.setVisibility(android.view.View.GONE);

        btnShow.setOnClickListener(v -> {
            Toast.makeText(this, "Loading & Showing Rewarded Interstitial...", Toast.LENGTH_SHORT).show();
            // Using the actual Builder from the library for Rewarded Interstitial
            new com.partharoypc.adglide.format.RewardedInterstitialAd.Builder(this)
                    .build(
                            () -> {
                                Toast.makeText(ActivityRewardedInterstitial.this, "Reward Granted!", Toast.LENGTH_SHORT)
                                        .show();
                            },
                            () -> {
                                // On dismiss
                                Toast.makeText(ActivityRewardedInterstitial.this, "Ad Closed", Toast.LENGTH_SHORT)
                                        .show();
                            });
        });
    }
}
