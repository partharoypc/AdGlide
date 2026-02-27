package com.partharoypc.adglidedemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglidedemo.BuildConfig;
import com.partharoypc.adglidedemo.R;
import com.partharoypc.adglidedemo.data.Constant;

public class ActivitySplash extends AppCompatActivity {

    public static int DELAY_PROGRESS = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        syncSettings();
        initAds();

        // High-Performance Consent Management (GDPR/UMP)
        AdGlide.requestConsent(this, () -> {
            // Once consent is gathered (or skipped if not needed), continue to Main
            new Handler(Looper.getMainLooper()).postDelayed(this::startMainActivity, DELAY_PROGRESS);
        });
    }

    private void syncSettings() {
        com.partharoypc.adglidedemo.database.SharedPref sharedPref = new com.partharoypc.adglidedemo.database.SharedPref(
                this);
        Constant.BANNER_STATUS = sharedPref.getIsBannerEnabled();
        Constant.INTERSTITIAL_STATUS = sharedPref.getIsInterstitialEnabled();
        Constant.NATIVE_STATUS = sharedPref.getIsNativeEnabled();
        Constant.REWARDED_STATUS = sharedPref.getIsRewardedEnabled();
    }

    private void initAds() {
        // Here we configure the AdGlide Facade with all parameters,
        // and tell it to automatically handle Interstitial caching and AppOpen Ads!
        com.partharoypc.adglide.AdGlideConfig config = new com.partharoypc.adglide.AdGlideConfig.Builder()
                .enableAds(Constant.AD_STATUS)
                .primaryNetwork(Constant.AD_NETWORK)
                .backupNetwork(Constant.BACKUP_AD_NETWORK)
                .startAppId(Constant.STARTAPP_APP_ID)
                .unityGameId(Constant.UNITY_GAME_ID)
                .appLovinSdkKey(getResources().getString(R.string.app_lovin_sdk_key))
                .ironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .wortiseAppId(Constant.WORTISE_APP_ID)
                .debug(BuildConfig.DEBUG)

                // Add all the Ad Unit IDs globally so we never need to provide them again!
                .adMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                .appLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .wortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)

                .adMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .appLovinRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)

                .adMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                .appLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                .wortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)

                // TRUE MAGIC: Automatic Caching and App Open Management
                .autoLoadInterstitial(true)
                .autoLoadRewarded(true)
                .enableAppOpenAd(Constant.OPEN_ADS_ON_START)
                .appOpenStatus(Constant.OPEN_ADS_ON_START)
                .bannerStatus(Constant.BANNER_STATUS)
                .interstitialStatus(Constant.INTERSTITIAL_STATUS)
                .nativeStatus(Constant.NATIVE_STATUS)
                .rewardedStatus(Constant.REWARDED_STATUS)
                .interstitialInterval(Constant.INTERSTITIAL_AD_INTERVAL)
                .excludeOpenAdFrom(ActivitySplash.class, ActivitySettings.class) // Professional exclusion API
                .enableGDPR(true) // Enable Elite GDPR Flow
                .debugGDPR(BuildConfig.DEBUG) // Debug geography for testing
                .enableDebugHUD(BuildConfig.DEBUG) // Enable secret debugger in debug builds

                // Phase 3: Advanced Monetization
                .houseAdEnabled(Constant.HOUSE_AD_ENABLE)
                .houseAdBannerImage(Constant.HOUSE_AD_BANNER_IMAGE)
                .houseAdBannerClickUrl(Constant.HOUSE_AD_BANNER_URL)
                .houseAdInterstitialImage(Constant.HOUSE_AD_INTERSTITIAL_IMAGE)
                .houseAdInterstitialClickUrl(Constant.HOUSE_AD_INTERSTITIAL_URL)

                // 1-Line Revenue Tracking (LTV)
                .onPaidEventListener((valueMicros, currencyCode, precision, network, adUnitId) -> {
                    double revenue = valueMicros / 1000000.0;
                    android.util.Log.i("AdGlide.Analytics", String.format(
                            "Revenue: %f %s | Network: %s | Unit: %s",
                            revenue, currencyCode, network, adUnitId));
                    // TODO: Send to Firebase/AppsFlyer here
                })

                .build();

        // Initialize with Application context to support global App Open Ads
        AdGlide.initialize(getApplication(), config);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
