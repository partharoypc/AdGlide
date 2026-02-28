package com.partharoypc.adglidedemo.application;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglidedemo.activity.ActivitySplash;
import com.partharoypc.adglidedemo.activity.ActivitySettings;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Fetch settings from SharedPref natively
        SharedPref sharedPref = new SharedPref(this);
        Constant.AD_NETWORK = sharedPref.getAdNetwork();
        Constant.BACKUP_AD_NETWORK = sharedPref.getBackupAdNetwork();
        Constant.OPEN_ADS_ON_START = sharedPref.getIsAppOpenAdEnabled();
        Constant.BANNER_STATUS = sharedPref.getIsBannerEnabled();
        Constant.INTERSTITIAL_STATUS = sharedPref.getIsInterstitialEnabled();
        Constant.NATIVE_STATUS = sharedPref.getIsNativeEnabled();
        Constant.REWARDED_STATUS = sharedPref.getIsRewardedEnabled();

        // Build AdGlide configuration using v1.4.0 Builder
        AdGlideConfig config = new AdGlideConfig.Builder()
                .enableAds(Constant.AD_STATUS)
                .primaryNetwork(Constant.AD_NETWORK)
                .backupNetwork(Constant.BACKUP_AD_NETWORK)
                .startAppId(Constant.STARTAPP_APP_ID)
                .unityGameId(Constant.UNITY_GAME_ID)
                .appLovinSdkKey(getResources().getString(com.partharoypc.adglidedemo.R.string.app_lovin_sdk_key))
                .ironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .wortiseAppId(Constant.WORTISE_APP_ID)
                .debug(com.partharoypc.adglidedemo.BuildConfig.DEBUG) // Use BuildConfig for debug mode

                // Banner
                .adMobBannerId(Constant.ADMOB_BANNER_ID)
                .metaBannerId(Constant.META_BANNER_ID)
                .unityBannerId(Constant.UNITY_BANNER_ID)
                .appLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .ironSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .wortiseBannerId(Constant.WORTISE_BANNER_ID)

                // Interstitial
                .adMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                .metaInterstitialId(Constant.META_INTERSTITIAL_ID)
                .unityInterstitialId(Constant.UNITY_INTERSTITIAL_ID)
                .appLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .ironSourceInterstitialId(Constant.IRONSOURCE_INTERSTITIAL_ID)
                .wortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)

                // Rewarded
                .adMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .metaRewardedId(Constant.META_REWARDED_ID)
                .unityRewardedId(Constant.UNITY_REWARDED_ID)
                .appLovinRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                .ironSourceRewardedId(Constant.IRONSOURCE_REWARDED_ID)
                .wortiseRewardedId(Constant.WORTISE_REWARDED_ID)

                // Rewarded Interstitial
                .adMobRewardedIntId(Constant.ADMOB_REWARDED_INTERSTITIAL_ID)

                // App Open
                .adMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                .appLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                .wortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)

                // Native
                .adMobNativeId(Constant.ADMOB_NATIVE_ID)
                .metaNativeId(Constant.META_NATIVE_ID)
                .appLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .ironSourceNativeId(Constant.IRONSOURCE_NATIVE_ID)
                .wortiseNativeId(Constant.WORTISE_NATIVE_ID)

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
                .enableDebugHUD(com.partharoypc.adglidedemo.BuildConfig.DEBUG)
                .houseAdEnabled(Constant.HOUSE_AD_ENABLE)
                .houseAdBannerImage(Constant.HOUSE_AD_BANNER_IMAGE)
                .houseAdBannerClickUrl(Constant.HOUSE_AD_BANNER_URL)
                .houseAdInterstitialImage(Constant.HOUSE_AD_INTERSTITIAL_IMAGE)
                .houseAdInterstitialClickUrl(Constant.HOUSE_AD_INTERSTITIAL_URL)

                // 1-Line Revenue Tracking (LTV)
                .onPaidEventListener((valueMicros, currencyCode, precision, network, adUnitId) -> {
                    double revenue = valueMicros / 1000000.0;
                    android.util.Log.i("AdGlideRevenue", String.format(
                            "Earned: %f %s from %s", revenue, currencyCode, network));
                })
                .build();

        // Initialize globally
        AdGlide.initialize(this, config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
