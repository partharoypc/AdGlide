package com.partharoypc.adglidedemo.application;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglidedemo.activity.ActivitySplash;
import com.partharoypc.adglidedemo.activity.ActivitySettings;
import com.partharoypc.adglidedemo.data.Constant;
import com.partharoypc.adglidedemo.database.SharedPref;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeAdGlide(this);
    }

    public static void initializeAdGlide(android.content.Context context) {
        SharedPref sharedPref = new SharedPref(context);

        AdGlideConfig config = new AdGlideConfig.Builder()
                .enableAds(sharedPref.getAdStatus())
                .adMobAppId(Constant.ADMOB_APP_ID)
                .primaryNetwork(sharedPref.getAdNetwork())
                .backupNetworks(sharedPref.getBackupAdNetwork())
                .startAppId(Constant.STARTAPP_APP_ID)
                .unityGameId(Constant.UNITY_GAME_ID)
                .appLovinSdkKey(context.getResources().getString(com.partharoypc.adglidedemo.R.string.app_lovin_sdk_key))
                .ironSourceAppKey(Constant.IRONSOURCE_APP_KEY)
                .wortiseAppId(Constant.WORTISE_APP_ID)
                .testMode(sharedPref.getTestMode())

                // ── Banner ────────────────────────────────────────────────
                .adMobBannerId(Constant.ADMOB_BANNER_ID)
                .metaBannerId(Constant.META_BANNER_ID)
                .unityBannerId(Constant.UNITY_BANNER_ID)
                .appLovinBannerId(Constant.APPLOVIN_BANNER_ID)
                .ironSourceBannerId(Constant.IRONSOURCE_BANNER_ID)
                .wortiseBannerId(Constant.WORTISE_BANNER_ID)

                // ── Interstitial ──────────────────────────────────────────
                .adMobInterstitialId(Constant.ADMOB_INTERSTITIAL_ID)
                .metaInterstitialId(Constant.META_INTERSTITIAL_ID)
                .unityInterstitialId(Constant.UNITY_INTERSTITIAL_ID)
                .appLovinInterstitialId(Constant.APPLOVIN_INTERSTITIAL_ID)
                .ironSourceInterstitialId(Constant.IRONSOURCE_INTERSTITIAL_ID)
                .wortiseInterstitialId(Constant.WORTISE_INTERSTITIAL_ID)

                // ── Rewarded ──────────────────────────────────────────────
                .adMobRewardedId(Constant.ADMOB_REWARDED_ID)
                .metaRewardedId(Constant.META_REWARDED_ID)
                .unityRewardedId(Constant.UNITY_REWARDED_ID)
                .appLovinRewardedId(Constant.APPLOVIN_MAX_REWARDED_ID)
                .ironSourceRewardedId(Constant.IRONSOURCE_REWARDED_ID)
                .wortiseRewardedId(Constant.WORTISE_REWARDED_ID)

                // ── Rewarded Interstitial ─────────────────────────────────
                .adMobRewardedIntId(Constant.ADMOB_REWARDED_INTERSTITIAL_ID)
                .appLovinRewardedIntId(Constant.APPLOVIN_REWARDED_INT_ID)
                .unityRewardedIntId(Constant.UNITY_REWARDED_INT_ID)
                .ironSourceRewardedIntId(Constant.IRONSOURCE_REWARDED_INT_ID)
                .wortiseRewardedIntId(Constant.WORTISE_REWARDED_INTERSTITIAL_ID)

                // ── App Open ──────────────────────────────────────────────
                .adMobAppOpenId(Constant.ADMOB_APP_OPEN_AD_ID)
                .metaAppOpenId(Constant.META_APP_OPEN_ID)
                .appLovinAppOpenId(Constant.APPLOVIN_APP_OPEN_AP_ID)
                .startAppAppOpenId(Constant.STARTAPP_APP_OPEN_ID)
                .ironSourceAppOpenId(Constant.IRONSOURCE_APP_OPEN_ID)
                .wortiseAppOpenId(Constant.WORTISE_APP_OPEN_AD_ID)

                // ── Native ────────────────────────────────────────────────
                .adMobNativeId(Constant.ADMOB_NATIVE_ID)
                .metaNativeId(Constant.META_NATIVE_ID)
                .appLovinNativeId(Constant.APPLOVIN_NATIVE_MANUAL_ID)
                .ironSourceNativeId(Constant.IRONSOURCE_NATIVE_ID)
                .wortiseNativeId(Constant.WORTISE_NATIVE_ID)

                // ── Format Toggles ────────────────────────────────────────
                .bannerEnabled(sharedPref.getIsBannerEnabled())
                .interstitialEnabled(sharedPref.getIsInterstitialEnabled())
                .nativeEnabled(sharedPref.getIsNativeEnabled())
                .rewardedEnabled(sharedPref.getIsRewardedEnabled())
                .rewardedInterstitialEnabled(sharedPref.getIsRewardedInterstitialEnabled())
                .appOpenEnabled(sharedPref.getIsAppOpenAdEnabled())

                // ── Smart Loading & Intervals ─────────────────────────────
                .autoLoad(true)
                .interstitialInterval(sharedPref.getInterstitialInterval())
                .rewardedInterval(sharedPref.getRewardedInterval())
                .appOpenCooldown(sharedPref.getAppOpenCooldownMinutes())
                .adResponseTimeout(sharedPref.getAdResponseTimeoutMs())

                // ── Privacy & Debug ───────────────────────────────────────
                .excludeOpenAdFrom(ActivitySplash.class, ActivitySettings.class)
                .enableGDPR(true)

                // ── House Ads Fallback ────────────────────────────────────
                .houseAdEnabled(sharedPref.getIsHouseAdEnabled())
                .houseAdBannerImage(Constant.HOUSE_AD_BANNER_IMAGE)
                .houseAdBannerClickUrl(Constant.HOUSE_AD_BANNER_URL)
                .houseAdInterstitialImage(Constant.HOUSE_AD_INTERSTITIAL_IMAGE)
                .houseAdInterstitialClickUrl(Constant.HOUSE_AD_INTERSTITIAL_URL)
                .houseAdNativeTitle(Constant.HOUSE_AD_NATIVE_TITLE)
                .houseAdNativeDescription(Constant.HOUSE_AD_NATIVE_DESC)
                .houseAdNativeCTA(Constant.HOUSE_AD_NATIVE_CTA)
                .houseAdNativeImage(Constant.HOUSE_AD_NATIVE_IMAGE)
                .houseAdNativeIcon(Constant.HOUSE_AD_NATIVE_ICON)
                .houseAdNativeClickUrl(Constant.HOUSE_AD_NATIVE_URL)

                .build();

        // Initialize globally
        android.app.Application application = (android.app.Application) context.getApplicationContext();
        AdGlide.initialize(application, config);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
