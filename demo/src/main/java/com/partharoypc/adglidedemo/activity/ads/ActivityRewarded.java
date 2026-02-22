package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;

public class ActivityRewarded extends AppCompatActivity {

    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        setupToolbar();
        initViews();
        loadRewardedAd();
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
        btnLoad.setOnClickListener(v -> loadRewardedAd());
    }

    private void loadRewardedAd() {
        appendLog("Rewarded Ad preloader is handled by AdGlide");
    }

    private void showRewardedAd() {
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
    }
}
