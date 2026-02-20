package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.format.RewardedAd;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;

public class ActivityRewarded extends AppCompatActivity {

    private RewardedAd.Builder rewardedAd;
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
        appendLog("Loading Rewarded Ad...");
        rewardedAd = new RewardedAd.Builder(this)
                .setAdStatus(Constant.AD_STATUS)
                .setMainAds(Constant.AD_NETWORK)
                .setBackupAds(Constant.BACKUP_AD_NETWORK)
                .setAdMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .setMetaRewardedId(Constant.META_REWARDED_ID)
                .setUnityRewardedId(Constant.UNITY_REWARDED_ID)
                .setApplovinMaxRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                .setApplovinDiscRewardedZoneId(Constant.APPLOVIN_DISC_REWARDED_ZONE_ID)
                .setironSourceRewardedId(Constant.IRONSOURCE_REWARDED_ID)
                .setWortiseRewardedId(Constant.WORTISE_REWARDED_ID)
                .build(() -> appendLog("Rewarded Ad Loaded"),
                        () -> appendLog("Rewarded Ad Error"),
                        () -> appendLog("Rewarded Ad Dismissed"),
                        () -> appendLog("Rewarded Ad Complete - GIVE REWARD"));
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(new OnRewardedAdCompleteListener() {
                @Override
                public void onRewardedAdComplete() {
                    appendLog("User Earned Reward!");
                    Toast.makeText(getApplicationContext(), "Reward Earned!", Toast.LENGTH_SHORT).show();
                }
            }, new OnRewardedAdDismissedListener() {
                @Override
                public void onRewardedAdDismissed() {
                    appendLog("Ad Dismissed");
                }
            }, new OnRewardedAdErrorListener() {
                @Override
                public void onRewardedAdError() {
                    appendLog("Ad Error");
                }
            });
        } else {
            appendLog("Ad not initialized");
        }
    }

    private void appendLog(String text) {
        logTextView.append(text + "\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rewardedAd != null) {
            rewardedAd.destroyRewardedAd();
        }
    }
}
