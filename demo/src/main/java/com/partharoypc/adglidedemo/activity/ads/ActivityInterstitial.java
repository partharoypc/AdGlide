package com.partharoypc.adglidedemo.activity.ads;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.partharoypc.adglide.format.InterstitialAd;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;

public class ActivityInterstitial extends AppCompatActivity {

    private static final String TAG = "ActivityInterstitial";
    private InterstitialAd.Builder interstitialAd;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        setupToolbar();
        initViews();
        loadInterstitialAd();
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
        btnLoad.setOnClickListener(v -> loadInterstitialAd());
    }

    private void loadInterstitialAd() {
        appendLog("Loading Interstitial Ad...");
        interstitialAd = new InterstitialAd.Builder(this)
                .status(Constant.AD_STATUS)
                .network(Constant.AD_NETWORK)
                .backup(Constant.BACKUP_AD_NETWORK)
                .adMobId(Constant.ADMOB_INTERSTITIAL_ID)
                .metaId(Constant.META_INTERSTITIAL_ID)
                .unityId(Constant.UNITY_GAME_ID)
                .appLovinId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .zoneId(Constant.APPLOVIN_INTERSTITIAL_ZONE_ID)
                .ironSourceId(Constant.IRONSOURCE_INTERSTITIAL_ID)
                .wortiseId(Constant.WORTISE_INTERSTITIAL_ID)
                .interval(Constant.INTERSTITIAL_AD_INTERVAL)
                .build().load(() -> {
                    appendLog("Interstitial Ad Dismissed");
                    Log.d(TAG, "onAdDismissed");
                    loadInterstitialAd(); // Auto reload
                });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.show(() -> {
                appendLog("Interstitial Ad Shown");
                Log.d(TAG, "onAdShowed");
            }, () -> {
                appendLog("Interstitial Ad Dismissed");
                Log.d(TAG, "onAdDismissed");
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
        if (interstitialAd != null) {
            interstitialAd.destroyInterstitialAd();
        }
    }
}
