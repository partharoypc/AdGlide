package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglidedemo.R;

public class ActivityInterstitial extends AppCompatActivity {

    private static final String TAG = "ActivityInterstitial";
    private TextView logTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        setupToolbar();
        initViews();
        appendLog("🚀 Preloading Interstitial Ad...");
        AdGlide.preload(this, AdFormat.INTERSTITIAL);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Interstitial Ad");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        logTextView = findViewById(R.id.log_text_view);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnLoad = findViewById(R.id.btn_load);

        btnShow.setOnClickListener(v -> showInterstitialAd());
        btnLoad.setOnClickListener(v -> {
            appendLog("⏳ Manually Loading Interstitial Ad...");
            AdGlide.preload(this, AdFormat.INTERSTITIAL);
        });
    }

    private void showInterstitialAd() {
        appendLog("💎 Triggering Interstitial Ad Show...");
        AdGlide.showInterstitial(this, new AdGlideCallback() {
            @Override
            public void onAdShowed() {
                appendLog("✅ Interstitial Ad Showed Successfully");
            }

            @Override
            public void onAdFailedToLoad(@Nullable String error) {
                appendLog("🛑 Ad Failed: " + (error != null ? error : "Unknown Error"));
            }

            @Override
            public void onAdDismissed() {
                appendLog("👋 Interstitial Ad Dismissed");
                Log.d(TAG, "onAdDismissed");
            }
        });
    }

    private void appendLog(@NonNull String text) {
        logTextView.append(text + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // AdGlide manages the cache, no need to manually destroy.
    }
}
