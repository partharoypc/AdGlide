package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;

/**
 * Manages app open ads with primary and backup network support.
 * Supports AdMob, AppLovin, and Wortise networks.
 */
@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";

    public static com.google.android.gms.ads.appopen.AppOpenAd appOpenAd = null;
    public static boolean isAppOpenAdLoaded = false;
    private AdMobAppOpenAd adMobAppOpenAd;
    private AppLovinAppOpenAd appLovinAppOpenAd;
    private WortiseAppOpenAd wortiseAppOpenAd;
    private boolean adStatus;
    private boolean placementStatus;
    private String adNetwork = "";
    private String backupAdNetwork = "";
    private String adMobAppOpenId = "";
    private String appLovinAppOpenId = "";
    private String wortiseAppOpenId = "";
    private Activity currentActivity;

    /**
     * Initializes AdMob for App Open Ads.
     * 
     * @param adMobAppOpenAd The helper instance.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd initAdMobAppOpenAd(@NonNull AdMobAppOpenAd adMobAppOpenAd) {
        this.adMobAppOpenAd = adMobAppOpenAd;
        return this;
    }

    /**
     * Initializes AppLovin for App Open Ads.
     * 
     * @param appLovinAppOpenAd The helper instance.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd initAppLovinAppOpenAd(@NonNull AppLovinAppOpenAd appLovinAppOpenAd) {
        this.appLovinAppOpenAd = appLovinAppOpenAd;
        return this;
    }

    /**
     * Initializes Wortise for App Open Ads.
     * 
     * @param wortiseAppOpenAd The helper instance.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd initWortiseAppOpenAd(@NonNull WortiseAppOpenAd wortiseAppOpenAd) {
        this.wortiseAppOpenAd = wortiseAppOpenAd;
        return this;
    }

    /**
     * Sets the ad status.
     * 
     * @param adStatus True to enable.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setAdStatus(boolean adStatus) {
        this.adStatus = adStatus;
        return this;
    }

    /**
     * Sets the placement status.
     * 
     * @param placementStatus True to enable.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setPlacementStatus(boolean placementStatus) {
        this.placementStatus = placementStatus;
        return this;
    }

    /**
     * Sets the primary ad network.
     * 
     * @param adNetwork The primary network key.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setAdNetwork(@NonNull String adNetwork) {
        this.adNetwork = adNetwork;
        return this;
    }

    /**
     * Sets the backup ad network.
     * 
     * @param backupAdNetwork The backup network key.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setBackupAdNetwork(@Nullable String backupAdNetwork) {
        this.backupAdNetwork = backupAdNetwork;
        return this;
    }

    /**
     * Sets AdMob App Open ID.
     * 
     * @param adMobAppOpenId The ad unit ID.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setAdMobAppOpenId(@NonNull String adMobAppOpenId) {
        this.adMobAppOpenId = adMobAppOpenId;
        return this;
    }

    /**
     * Sets AppLovin App Open ID.
     * 
     * @param appLovinAppOpenId The ad unit ID.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setAppLovinAppOpenId(@NonNull String appLovinAppOpenId) {
        this.appLovinAppOpenId = appLovinAppOpenId;
        return this;
    }

    /**
     * Sets Wortise App Open ID.
     * 
     * @param wortiseAppOpenId The ad unit ID.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setWortiseAppOpenId(@NonNull String wortiseAppOpenId) {
        this.wortiseAppOpenId = wortiseAppOpenId;
        return this;
    }

    /**
     * Registers the observer for app start events.
     * 
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setOnStartLifecycleObserver() {
        onStartLifecycleObserver();
        return this;
    }

    /**
     * Registers activity lifecycle callbacks.
     * 
     * @param activity The Activity.
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setOnStartActivityLifecycleCallbacks(@NonNull Activity activity) {
        onStartActivityLifecycleCallbacks(activity);
        return this;
    }

    public void onStartLifecycleObserver() {
        try {
            if (placementStatus) {
                if (adStatus) {
                    if (currentActivity == null)
                        return;
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (!adMobAppOpenId.equals("0")) {
                                if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                    adMobAppOpenAd.showAdIfAvailable(currentActivity, adMobAppOpenId);
                                }
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            if (!appLovinAppOpenId.equals("0")) {
                                if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                    appLovinAppOpenAd.showAdIfAvailable(currentActivity, appLovinAppOpenId);
                                }
                            }
                            break;

                        case WORTISE:
                            if (!wortiseAppOpenId.equals("0")) {
                                if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                    wortiseAppOpenAd.showAdIfAvailable(currentActivity, wortiseAppOpenId);
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
        }
    }

    public void onStartActivityLifecycleCallbacks(Activity activity) {
        try {
            if (placementStatus) {
                if (adStatus) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (!adMobAppOpenId.equals("0")) {
                                if (!adMobAppOpenAd.isShowingAd()) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            if (!appLovinAppOpenId.equals("0")) {
                                if (!appLovinAppOpenAd.isShowingAd()) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case WORTISE:
                            if (!wortiseAppOpenId.equals("0")) {
                                if (!wortiseAppOpenAd.isShowingAd()) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartActivityLifecycleCallbacks: " + e.getMessage());
        }
    }

    public AppOpenAd showAppOpenAdIfAvailable(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        try {
            if (adStatus && placementStatus) {
                switch (adNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (!adMobAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        if (!appLovinAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;
                    case WORTISE:
                        if (!wortiseAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;
                    default:
                        onShowAdCompleteListener.onShowAdComplete();
                        break;
                }
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showAppOpenAdIfAvailable: " + e.getMessage());
            onShowAdCompleteListener.onShowAdComplete();
        }
        return this;
    }

    public void showAdIfAvailable(@NonNull Activity activity,
            @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        try {
            if (placementStatus) {
                if (adStatus) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            if (!adMobAppOpenId.equals("0")) {
                                adMobAppOpenAd.showAdIfAvailable(activity, adMobAppOpenId, onShowAdCompleteListener);
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX:
                            if (!appLovinAppOpenId.equals("0")) {
                                appLovinAppOpenAd.showAdIfAvailable(activity, appLovinAppOpenId,
                                        onShowAdCompleteListener);
                            }
                            break;
                        case WORTISE:
                            if (!wortiseAppOpenId.equals("0")) {
                                wortiseAppOpenAd.showAdIfAvailable(activity, wortiseAppOpenId,
                                        onShowAdCompleteListener);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String wortiseAppOpenId = "";

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            loadAppOpenAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnShowAdCompleteListener onShowAdCompleteListener) {
            loadAppOpenAd(onShowAdCompleteListener);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show() {
            showAppOpenAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show(OnShowAdCompleteListener onShowAdCompleteListener) {
            showAppOpenAd(onShowAdCompleteListener);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdStatus(@androidx.annotation.NonNull String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdNetwork(@androidx.annotation.NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @androidx.annotation.Nullable
        public Builder setBackupAdNetwork(@androidx.annotation.Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAdMobAppOpenId(@androidx.annotation.NonNull String adMobAppOpenId) {
            this.adMobAppOpenId = adMobAppOpenId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setAppLovinAppOpenId(@androidx.annotation.NonNull String appLovinAppOpenId) {
            this.appLovinAppOpenId = appLovinAppOpenId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder setWortiseAppOpenId(@androidx.annotation.NonNull String wortiseAppOpenId) {
            this.wortiseAppOpenId = wortiseAppOpenId;
            return this;
        }

        public void destroyOpenAd() {
            AppOpenAd.isAppOpenAdLoaded = false;
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (adNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (appOpenAd != null) {
                            appOpenAd = null;
                        }
                        break;

                    default:
                        break;
                }
            }
        }

        // main ads
        public void loadAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                if (adStatus.equals(AD_STATUS_ON)) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity, adMobAppOpenId, adRequest,
                                        new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                                                appOpenAd = ad;
                                                showAppOpenAd(onShowAdCompleteListener);
                                                Log.d(TAG, "[" + adNetwork + "] " + "[on start] app open ad loaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                appOpenAd = null;
                                                loadBackupAppOpenAd(onShowAdCompleteListener);
                                                Log.d(TAG,
                                                        "[" + adNetwork + "] "
                                                                + "[on start] failed to load app open ad: "
                                                                + loadAdError.getMessage());
                                            }
                                        });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load AdMob app open ad. Error: " + e.getMessage());
                                loadBackupAppOpenAd(onShowAdCompleteListener);
                            }
                            break;

                        default:
                            onShowAdCompleteListener.onShowAdComplete();
                            break;
                    }
                } else {
                    onShowAdCompleteListener.onShowAdComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAppOpenAd: " + e.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void showAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                switch (adNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (appOpenAd != null) {
                            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    appOpenAd = null;
                                    onShowAdCompleteListener.onShowAdComplete();
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on start] close app open ad");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    appOpenAd = null;
                                    onShowAdCompleteListener.onShowAdComplete();
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on start] failed to show app open ad: "
                                            + adError.getMessage());
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on start] show app open ad");
                                }
                            });
                            appOpenAd.show(activity);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;

                    default:
                        onShowAdCompleteListener.onShowAdComplete();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void loadAppOpenAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON)) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity, adMobAppOpenId, adRequest,
                                        new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                                                appOpenAd = ad;
                                                isAppOpenAdLoaded = true;
                                                Log.d(TAG, "[" + adNetwork + "] " + "[on resume] app open ad loaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                appOpenAd = null;
                                                isAppOpenAdLoaded = false;
                                                loadBackupAppOpenAd();
                                                Log.d(TAG,
                                                        "[" + adNetwork + "] "
                                                                + "[on resume] failed to load app open ad : "
                                                                + loadAdError.getMessage());
                                            }
                                        });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load AdMob app open ad on resume. Error: " + e.getMessage());
                                loadBackupAppOpenAd();
                            }
                            break;

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAppOpenAd: " + e.getMessage());
            }
        }

        public void showAppOpenAd() {
            try {
                switch (adNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (appOpenAd != null) {
                            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    appOpenAd = null;
                                    loadAppOpenAd();
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on resume] close app open ad");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    appOpenAd = null;
                                    loadAppOpenAd();
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on resume] failed to show app open ad: "
                                            + adError.getMessage());
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    Log.d(TAG, "[" + adNetwork + "] " + "[on resume] show app open ad");
                                }
                            });
                            appOpenAd.show(activity);
                        } else {
                            showBackupAppOpenAd();
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                showBackupAppOpenAd();
            }
        }

        // backup ads
        public void loadBackupAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                if (adStatus.equals(AD_STATUS_ON)) {
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity, adMobAppOpenId, adRequest,
                                        new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                                                appOpenAd = ad;
                                                showBackupAppOpenAd(onShowAdCompleteListener);
                                                Log.d(TAG, "[" + backupAdNetwork + "] "
                                                        + "[on start] [backup] app open ad loaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                appOpenAd = null;
                                                onShowAdCompleteListener.onShowAdComplete();
                                                Log.d(TAG,
                                                        "[" + backupAdNetwork + "] "
                                                                + "[on start] [backup] failed to load app open ad: "
                                                                + loadAdError.getMessage());
                                            }
                                        });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup AdMob app open ad. Error: " + e.getMessage());
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                            break;

                        default:
                            onShowAdCompleteListener.onShowAdComplete();
                            break;
                    }
                } else {
                    onShowAdCompleteListener.onShowAdComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupAppOpenAd: " + e.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void showBackupAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (appOpenAd != null) {
                            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    appOpenAd = null;
                                    onShowAdCompleteListener.onShowAdComplete();
                                    Log.d(TAG, "[" + backupAdNetwork + "] " + "[on start] [backup] close app open ad");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    appOpenAd = null;
                                    onShowAdCompleteListener.onShowAdComplete();
                                    Log.d(TAG, "[" + backupAdNetwork + "] "
                                            + "[on start] [backup] failed to show app open ad: "
                                            + adError.getMessage());
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    Log.d(TAG, "[" + backupAdNetwork + "] " + "[on start] [backup] show app open ad");
                                }
                            });
                            appOpenAd.show(activity);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;

                    default:
                        onShowAdCompleteListener.onShowAdComplete();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showBackupAppOpenAd: " + e.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void loadBackupAppOpenAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON)) {
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity, adMobAppOpenId, adRequest,
                                        new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                                                appOpenAd = ad;
                                                isAppOpenAdLoaded = true;
                                                Log.d(TAG, "[" + backupAdNetwork + "] "
                                                        + "[on resume] [backup] app open ad loaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                appOpenAd = null;
                                                isAppOpenAdLoaded = false;
                                                Log.d(TAG,
                                                        "[" + backupAdNetwork + "] "
                                                                + "[on resume] [backup] failed to load app open ad : "
                                                                + loadAdError.getMessage());
                                            }
                                        });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG,
                                        "Failed to load backup AdMob app open ad on resume. Error: " + e.getMessage());
                            }
                            break;

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupAppOpenAd: " + e.getMessage());
            }
        }

        public void showBackupAppOpenAd() {
            try {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (appOpenAd != null) {
                            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    appOpenAd = null;
                                    loadBackupAppOpenAd();
                                    Log.d(TAG, "[" + backupAdNetwork + "] " + "[on resume] [backup] close app open ad");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                    appOpenAd = null;
                                    loadBackupAppOpenAd();
                                    Log.d(TAG, "[" + backupAdNetwork + "] "
                                            + "[on resume] [backup] failed to show app open ad: "
                                            + adError.getMessage());
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    Log.d(TAG, "[" + backupAdNetwork + "] " + "[on resume] [backup] show app open ad");
                                }
                            });
                            appOpenAd.show(activity);
                        }
                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showBackupAppOpenAd: " + e.getMessage());
                loadBackupAppOpenAd();
            }
        }

    }

}
