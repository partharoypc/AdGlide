package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglidedemo.R;

public class ActivityRewardedInterstitial extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
            Toast.makeText(this, "⏳ Loading Rewarded Interstitial...", Toast.LENGTH_SHORT).show();
            AdGlide.preload(this, AdFormat.REWARDED_INTERSTITIAL);
        });

        btnShow.setOnClickListener(v -> {
            Toast.makeText(this, "💎 Showing Rewarded Interstitial...", Toast.LENGTH_SHORT).show();
            AdGlide.showRewardedInterstitial(this, new AdGlideCallback() {
                @Override
                public void onAdShowed() {
                    Toast.makeText(ActivityRewardedInterstitial.this, "✅ Ad Showed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(@Nullable String error) {
                    Toast.makeText(ActivityRewardedInterstitial.this, "🛑 Not Ready: " + (error != null ? error : "Unknown Error"),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdDismissed() {
                    Toast.makeText(ActivityRewardedInterstitial.this, "👋 Ad Closed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdCompleted() {
                    Toast.makeText(ActivityRewardedInterstitial.this, "🏆 Reward Granted!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
