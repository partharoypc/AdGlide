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
        Button btnLoad = findViewById(R.id.btn_load);

        btnLoad.setOnClickListener(v -> {
            Toast.makeText(this, "Loading Rewarded Interstitial...", Toast.LENGTH_SHORT).show();
            AdGlide.preloadRewardedInterstitial(this);
        });

        btnShow.setOnClickListener(v -> {
            AdGlide.showRewardedInterstitial(this, new com.partharoypc.adglide.util.AdGlideCallback() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    Toast.makeText(ActivityRewardedInterstitial.this, "Not Ready: " + error,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdShowed() {
                }

                @Override
                public void onAdDismissed() {
                    Toast.makeText(ActivityRewardedInterstitial.this, "Ad Closed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdCompleted() {
                    Toast.makeText(ActivityRewardedInterstitial.this, "Reward Granted!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
