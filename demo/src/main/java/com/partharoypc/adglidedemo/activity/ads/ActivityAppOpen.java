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

public class ActivityAppOpen extends AppCompatActivity {

    private static final String TAG = "ActivityAppOpen";
    private TextView logTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_open);

        setupToolbar();
        initViews();
        
        appendLog("🚀 Preloading App Open pool...");
        AdGlide.preload(this, AdFormat.APP_OPEN);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("App Open Ad");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        logTextView = findViewById(R.id.log_text_view);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnLoad = findViewById(R.id.btn_load);

        btnShow.setOnClickListener(v -> showAppOpenAd());
        btnLoad.setOnClickListener(v -> {
            appendLog("⏳ Manually Loading/Replenishing App Open Pool...");
            AdGlide.preload(this, AdFormat.APP_OPEN);
        });
    }

    private void showAppOpenAd() {
        appendLog("💎 Triggering App Open Ad (Builder API)...");
        
        // Professional one-liner: Handles Cooldown, Loading, and Showing automatically.
        new com.partharoypc.adglide.format.AppOpenAd.Builder(this)
                .loadAndShow(this, new AdGlideCallback() {
                    @Override
                    public void onAdFailedToLoad(@Nullable String error) {
                        appendLog("🛑 Ad Failed: " + (error != null ? error : "No Fill"));
                    }

                    @Override
                    public void onAdShowed() {
                        appendLog("👁️ App Open Ad Showed Successfully");
                    }

                    @Override
                    public void onAdDismissed() {
                        appendLog("👋 App Open Ad Dismissed — Pool replenishing...");
                        Log.d(TAG, "onAdDismissed");
                    }
                });
    }

    private void appendLog(@NonNull String text) {
        logTextView.append("• " + text + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
