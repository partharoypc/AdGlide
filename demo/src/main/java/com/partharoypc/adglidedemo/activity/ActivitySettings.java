package com.partharoypc.adglidedemo.activity;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;

import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.adapter.AdapterNetwork;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;
import com.partharoypc.adglidedemo.application.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class ActivitySettings extends AppCompatActivity {

    private SharedPref sharedPref;
    private AdapterNetwork adapterPrimary;
    private AdapterNetwork adapterBackup;
    private List<AdapterNetwork.NetworkItem> primaryNetworks;
    private List<AdapterNetwork.NetworkItem> backupNetworks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(this);
        // Apply theme before setting content view
        if (sharedPref.getIsDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_settings);

        setupToolbar();
        setupData();
        setupViews();

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }
    }

    private void setupData() {
        primaryNetworks = new ArrayList<>();
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AdMob", ADMOB));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Meta Audience Network", META));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Meta Bidding (AdMob)", META_BIDDING_ADMOB));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Meta Bidding (AppLovin MAX)", META_BIDDING_APPLOVIN_MAX));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Meta Bidding (ironSource)", META_BIDDING_IRONSOURCE));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Unity Ads", UNITY));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AppLovin", APPLOVIN));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("AppLovin MAX", APPLOVIN_MAX));

        primaryNetworks.add(new AdapterNetwork.NetworkItem("ironSource", IRONSOURCE));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Start.io", STARTAPP));
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Wortise", WORTISE));

        backupNetworks = new ArrayList<>();
        backupNetworks.add(new AdapterNetwork.NetworkItem("None", NONE));
        backupNetworks.addAll(primaryNetworks);
    }

    private void setupViews() {
        // Primary Network
        RecyclerView recyclerViewPrimary = findViewById(R.id.recycler_view_primary);
        recyclerViewPrimary.setLayoutManager(new LinearLayoutManager(this));
        adapterPrimary = new AdapterNetwork(this, primaryNetworks, sharedPref.getAdNetwork(), item -> {
            sharedPref.setAdNetwork(item.adNetworkId);
            initAds();
        });
        recyclerViewPrimary.setAdapter(adapterPrimary);

        // Backup Network
        RecyclerView recyclerViewBackup = findViewById(R.id.recycler_view_backup);
        recyclerViewBackup.setLayoutManager(new LinearLayoutManager(this));
        adapterBackup = new AdapterNetwork(this, backupNetworks, sharedPref.getBackupAdNetwork(), item -> {
            sharedPref.setBackupAdNetwork(item.adNetworkId);
            initAds(); // Re-initialize immediately
        });
        recyclerViewBackup.setAdapter(adapterBackup);

        // Dark Mode Switch
        SwitchMaterial switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchDarkMode.setChecked(sharedPref.getIsDarkTheme());
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsDarkTheme(isChecked);
            recreate(); // Recreate activity to apply theme
        });

        // Master Ad Switch
        SwitchMaterial switchAllAds = findViewById(R.id.switch_all_ads);
        switchAllAds.setChecked(sharedPref.getAdStatus());
        switchAllAds.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setAdStatus(isChecked);
            initAds();
        });

        // App Open Ad Switch
        SwitchMaterial switchAppOpenAd = findViewById(R.id.switch_app_open_ad);
        switchAppOpenAd.setChecked(sharedPref.getIsAppOpenAdEnabled());
        switchAppOpenAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsAppOpenAdEnabled(isChecked);
            initAds();
        });

        // Banner Ad Switch
        SwitchMaterial switchBanner = findViewById(R.id.switch_banner_ad);
        switchBanner.setChecked(sharedPref.getIsBannerEnabled());
        switchBanner.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsBannerEnabled(isChecked);
            initAds();
        });

        // Interstitial Ad Switch
        SwitchMaterial switchInterstitial = findViewById(R.id.switch_interstitial_ad);
        switchInterstitial.setChecked(sharedPref.getIsInterstitialEnabled());
        switchInterstitial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsInterstitialEnabled(isChecked);
            initAds();
        });

        // Native Ad Switch
        SwitchMaterial switchNative = findViewById(R.id.switch_native_ad);
        switchNative.setChecked(sharedPref.getIsNativeEnabled());
        switchNative.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsNativeEnabled(isChecked);
            initAds();
        });

        // Rewarded Ad Switch
        SwitchMaterial switchRewarded = findViewById(R.id.switch_rewarded_ad);
        switchRewarded.setChecked(sharedPref.getIsRewardedEnabled());
        switchRewarded.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsRewardedEnabled(isChecked);
            initAds();
        });

        // Rewarded Interstitial Ad Switch
        SwitchMaterial switchRewardedInt = findViewById(R.id.switch_rewarded_interstitial_ad);
        switchRewardedInt.setChecked(sharedPref.getIsRewardedInterstitialEnabled());
        switchRewardedInt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsRewardedInterstitialEnabled(isChecked);
            initAds();
        });

        // Interstitial Interval
        android.widget.TextView tvIntervalValue = findViewById(R.id.tv_interval_value);
        tvIntervalValue.setText(String.valueOf(sharedPref.getInterstitialInterval()));
        findViewById(R.id.btn_interval_minus).setOnClickListener(v -> {
            int current = sharedPref.getInterstitialInterval();
            if (current > 1) {
                current--;
                sharedPref.setInterstitialInterval(current);
                tvIntervalValue.setText(String.valueOf(current));
                initAds();
            }
        });
        findViewById(R.id.btn_interval_plus).setOnClickListener(v -> {
            int current = sharedPref.getInterstitialInterval();
            if (current < 10) {
                current++;
                sharedPref.setInterstitialInterval(current);
                tvIntervalValue.setText(String.valueOf(current));
                initAds();
            }
        });

        // House Ad Switch
        SwitchMaterial switchHouseAd = findViewById(R.id.switch_house_ad);
        switchHouseAd.setChecked(sharedPref.getIsHouseAdEnabled());
        switchHouseAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsHouseAdEnabled(isChecked);
            initAds();
        });

        // Test Mode Switch
        SwitchMaterial switchTestMode = findViewById(R.id.switch_test_mode);
        switchTestMode.setChecked(sharedPref.getTestMode());
        switchTestMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setTestMode(isChecked);
            initAds();
        });

        // Debug HUD Switch
        SwitchMaterial switchDebugHud = findViewById(R.id.switch_debug_hud);
        switchDebugHud.setChecked(sharedPref.getEnableDebugHud());
        switchDebugHud.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setEnableDebugHud(isChecked);
            initAds();
        });


        // Rewarded Interval
        android.widget.TextView tvRewardedIntervalValue = findViewById(R.id.tv_rewarded_interval_value);
        tvRewardedIntervalValue.setText(String.valueOf(sharedPref.getRewardedInterval()));
        findViewById(R.id.btn_rewarded_interval_minus).setOnClickListener(v -> {
            int current = sharedPref.getRewardedInterval();
            if (current > 1) {
                current--;
                sharedPref.setRewardedInterval(current);
                tvRewardedIntervalValue.setText(String.valueOf(current));
                initAds();
            }
        });
        findViewById(R.id.btn_rewarded_interval_plus).setOnClickListener(v -> {
            int current = sharedPref.getRewardedInterval();
            if (current < 10) {
                current++;
                sharedPref.setRewardedInterval(current);
                tvRewardedIntervalValue.setText(String.valueOf(current));
                initAds();
            }
        });

        // Ad Response Timeout
        android.widget.TextView tvAdTimeoutValue = findViewById(R.id.tv_ad_timeout_value);
        tvAdTimeoutValue.setText(String.valueOf(sharedPref.getAdResponseTimeoutMs()));
        findViewById(R.id.btn_ad_timeout_minus).setOnClickListener(v -> {
            int current = sharedPref.getAdResponseTimeoutMs();
            if (current > 1000) {
                current -= 500;
                sharedPref.setAdResponseTimeoutMs(current);
                tvAdTimeoutValue.setText(String.valueOf(current));
                initAds();
            }
        });
        findViewById(R.id.btn_ad_timeout_plus).setOnClickListener(v -> {
            int current = sharedPref.getAdResponseTimeoutMs();
            if (current < 10000) {
                current += 500;
                sharedPref.setAdResponseTimeoutMs(current);
                tvAdTimeoutValue.setText(String.valueOf(current));
                initAds();
            }
        });

        Button btnGdpr = findViewById(R.id.btn_gdpr);
        btnGdpr.setOnClickListener(v -> {
            AdGlide.requestConsent(this, () -> {
                AdGlideLog.d("Settings", "Consent reset requested and updated");
            });
        });
    }

    private void initAds() {
        MyApplication.initializeAdGlide(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
