package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.R;

public class ActivityRewarded extends AppCompatActivity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        setupToolbar();
        initViews();

        // AdGlide automatically caches ads via AdGlideConfig,
        // but if we want to manually trigger preloading we can use the facade:
        AdGlide.preloadRewarded(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rewarded Ad");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void initViews() {
        logTextView = findViewById(R.id.log_text_view);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnLoad = findViewById(R.id.btn_load);

        btnShow.setOnClickListener(v -> showRewardedAd());
        btnLoad.setOnClickListener(v -> {
            appendLog("Manually Loading Rewarded Ad...");
            AdGlide.preloadRewarded(this);
        });
    }

    private void showRewardedAd() {
        appendLog("Triggering Rewarded Ad Show...");
        AdGlide.showRewarded(this, () -> {
            appendLog("User Earned Reward!");
            Toast.makeText(getApplicationContext(), "Reward Earned!", Toast.LENGTH_SHORT).show();
        }, () -> {
            appendLog("Ad Dismissed");
        });
    }

    private void appendLog(String text) {
        logTextView.append(text + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // AdGlide manages the cache, no need to manually destroy.
    }
}
