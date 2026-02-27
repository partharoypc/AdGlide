package com.partharoypc.adglidedemo.activity;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;

import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
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
import com.partharoypc.adglide.gdpr.GDPR;
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
        primaryNetworks.add(new AdapterNetwork.NetworkItem("Unity Ads", UNITY));
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
            Constant.AD_NETWORK = item.adNetworkId;
            initAds();
        });
        recyclerViewPrimary.setAdapter(adapterPrimary);

        // Backup Network
        RecyclerView recyclerViewBackup = findViewById(R.id.recycler_view_backup);
        recyclerViewBackup.setLayoutManager(new LinearLayoutManager(this));
        adapterBackup = new AdapterNetwork(this, backupNetworks, sharedPref.getBackupAdNetwork(), item -> {
            sharedPref.setBackupAdNetwork(item.adNetworkId);
            Constant.BACKUP_AD_NETWORK = item.adNetworkId;
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

        // App Open Ad Switch
        SwitchMaterial switchAppOpenAd = findViewById(R.id.switch_app_open_ad);
        switchAppOpenAd.setChecked(sharedPref.getIsAppOpenAdEnabled());
        switchAppOpenAd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsAppOpenAdEnabled(isChecked);
            Constant.OPEN_ADS_ON_START = isChecked;
            initAds();
        });

        // Banner Ad Switch
        SwitchMaterial switchBanner = findViewById(R.id.switch_banner_ad);
        switchBanner.setChecked(sharedPref.getIsBannerEnabled());
        switchBanner.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsBannerEnabled(isChecked);
            Constant.BANNER_STATUS = isChecked;
            initAds();
        });

        // Interstitial Ad Switch
        SwitchMaterial switchInterstitial = findViewById(R.id.switch_interstitial_ad);
        switchInterstitial.setChecked(sharedPref.getIsInterstitialEnabled());
        switchInterstitial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsInterstitialEnabled(isChecked);
            Constant.INTERSTITIAL_STATUS = isChecked;
            initAds();
        });

        // Native Ad Switch
        SwitchMaterial switchNative = findViewById(R.id.switch_native_ad);
        switchNative.setChecked(sharedPref.getIsNativeEnabled());
        switchNative.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsNativeEnabled(isChecked);
            Constant.NATIVE_STATUS = isChecked;
            initAds();
        });

        // Rewarded Ad Switch
        SwitchMaterial switchRewarded = findViewById(R.id.switch_rewarded_ad);
        switchRewarded.setChecked(sharedPref.getIsRewardedEnabled());
        switchRewarded.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPref.setIsRewardedEnabled(isChecked);
            Constant.REWARDED_STATUS = isChecked;
            initAds();
        });

        Button btnGdpr = findViewById(R.id.btn_gdpr);
        btnGdpr.setOnClickListener(v -> {
            new GDPR(this).resetConsent();
        });
    }

    private void initAds() {
        com.partharoypc.adglide.AdGlideConfig config = new com.partharoypc.adglide.AdGlideConfig.Builder()
                .enableAds(Constant.AD_STATUS)
                .primaryNetwork(Constant.AD_NETWORK)
                .backupNetwork(Constant.BACKUP_AD_NETWORK)
                .startAppId(Constant.STARTAPP_APP_ID)
                .unityGameId(Constant.UNITY_GAME_ID)
                .appLovinSdkKey(getResources().getString(R.string.app_lovin_sdk_key))
                .ironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .wortiseAppId(Constant.WORTISE_APP_ID)
                .debug(com.partharoypc.adglidedemo.BuildConfig.DEBUG)

                .adMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                .appLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .wortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)
                .adMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .appLovinRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                .adMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                .appLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                .wortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)

                .autoLoadInterstitial(true)
                .autoLoadRewarded(true)
                .enableAppOpenAd(Constant.OPEN_ADS_ON_START)
                .appOpenStatus(Constant.OPEN_ADS_ON_START)
                .bannerStatus(Constant.BANNER_STATUS)
                .interstitialStatus(Constant.INTERSTITIAL_STATUS)
                .nativeStatus(Constant.NATIVE_STATUS)
                .rewardedStatus(Constant.REWARDED_STATUS)
                .interstitialInterval(Constant.INTERSTITIAL_AD_INTERVAL)
                .excludeOpenAdFrom(ActivitySplash.class, ActivitySettings.class)
                .enableGDPR(true)
                .debugGDPR(com.partharoypc.adglidedemo.BuildConfig.DEBUG)
                .houseAdEnabled(Constant.HOUSE_AD_ENABLE)
                .houseAdBannerImage(Constant.HOUSE_AD_BANNER_IMAGE)
                .houseAdBannerClickUrl(Constant.HOUSE_AD_BANNER_URL)
                .houseAdInterstitialImage(Constant.HOUSE_AD_INTERSTITIAL_IMAGE)
                .houseAdInterstitialClickUrl(Constant.HOUSE_AD_INTERSTITIAL_URL)
                .build();

        com.partharoypc.adglide.AdGlide.initialize(getApplication(), config);
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
