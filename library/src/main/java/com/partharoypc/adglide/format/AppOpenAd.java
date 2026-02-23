package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import com.partharoypc.adglide.AdGlideNetwork;
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
import com.partharoypc.adglide.util.WaterfallManager;

/**
 * Manages app open ads with primary and backup network support.
 * Supports AdMob, AppLovin, and Wortise networks.
 */
@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";

    public static com.google.android.gms.ads.appopen.AppOpenAd appOpenAd = null;
    public static boolean isAppOpenAdLoaded = false;
    // Configuration fields (Standardized to match other ad formats)
    private boolean adStatus = true;
    private String adNetwork = "";
    private String backupAdNetwork = "";
    private WaterfallManager waterfallManager;
    private String adMobAppOpenId = "";
    private String appLovinAppOpenId = "";
    private String wortiseAppOpenId = "";
    private Activity currentActivity;
    private int placementStatus = 1;

    // Helper instances
    private AdMobAppOpenAd adMobAppOpenAd = new AdMobAppOpenAd();
    private AppLovinAppOpenAd appLovinAppOpenAd = new AppLovinAppOpenAd();
    private WortiseAppOpenAd appOpenAdWortise = new WortiseAppOpenAd();

    @NonNull
    public AppOpenAd status(boolean adStatus) {
        this.adStatus = adStatus;
        return this;
    }

    @NonNull
    public AppOpenAd placement(int placementStatus) {
        this.placementStatus = placementStatus;
        return this;
    }

    @NonNull
    public AppOpenAd network(@NonNull String adNetwork) {
        this.adNetwork = adNetwork;
        return this;
    }

    @NonNull
    public AppOpenAd backup(@Nullable String backupAdNetwork) {
        this.backupAdNetwork = backupAdNetwork;
        this.waterfallManager = new WaterfallManager(backupAdNetwork);
        return this;
    }

    @NonNull
    public AppOpenAd backups(@Nullable String... backupAdNetworks) {
        this.waterfallManager = new WaterfallManager(backupAdNetworks);
        if (backupAdNetworks != null && backupAdNetworks.length > 0) {
            this.backupAdNetwork = backupAdNetworks[0];
        }
        return this;
    }

    @NonNull
    public AppOpenAd adMobId(@NonNull String adMobAppOpenId) {
        this.adMobAppOpenId = adMobAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd appLovinId(@NonNull String appLovinAppOpenId) {
        this.appLovinAppOpenId = appLovinAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd wortiseId(@NonNull String wortiseAppOpenId) {
        this.wortiseAppOpenId = wortiseAppOpenId;
        return this;
    }

    /**
     * Registers the observer for app start events.
     * 
     * @return The AppOpenAd instance.
     */
    @NonNull
    public AppOpenAd setLifecycleObserver() {
        onStartLifecycleObserver();
        return this;
    }

    @NonNull
    public AppOpenAd setActivityLifecycleCallbacks(@NonNull Activity activity) {
        onStartActivityLifecycleCallbacks(activity);
        return this;
    }

    public void onStartLifecycleObserver() {
        try {
            if (placementStatus != 0) {
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
                                    appOpenAdWortise.showAdIfAvailable(currentActivity, wortiseAppOpenId);
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
            if (placementStatus != 0) {
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
                                if (!appOpenAdWortise.isShowingAd()) {
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
            if (adStatus && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (!adMobAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        if (!appLovinAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                        }
                        break;
                    case WORTISE:
                        if (!wortiseAppOpenId.equals("0")) {
                            showAdIfAvailable(activity, onShowAdCompleteListener);
                        } else {
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                        }
                        break;
                    default:
                        if (onShowAdCompleteListener != null) {
                            onShowAdCompleteListener.onShowAdComplete();
                        }
                        break;
                }
            } else {
                if (onShowAdCompleteListener != null) {
                    onShowAdCompleteListener.onShowAdComplete();
                }
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
            if (placementStatus != 0) {
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
                                appOpenAdWortise.showAdIfAvailable(activity, wortiseAppOpenId,
                                        onShowAdCompleteListener);
                            }
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (onShowAdCompleteListener != null) {
                onShowAdCompleteListener.onShowAdComplete();
            }
        }
    }

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String wortiseAppOpenId = "";

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnShowAdCompleteListener onShowAdCompleteListener) {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadAppOpenAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load(OnShowAdCompleteListener onShowAdCompleteListener) {
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
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(@androidx.annotation.NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backup(@androidx.annotation.Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @androidx.annotation.NonNull
        public Builder adMobId(@androidx.annotation.NonNull String adMobAppOpenId) {
            this.adMobAppOpenId = adMobAppOpenId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder appLovinId(@androidx.annotation.NonNull String appLovinAppOpenId) {
            this.appLovinAppOpenId = appLovinAppOpenId;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder wortiseId(@androidx.annotation.NonNull String wortiseAppOpenId) {
            this.wortiseAppOpenId = wortiseAppOpenId;
            return this;
        }

        public void destroyOpenAd() {
            AppOpenAd.isAppOpenAdLoaded = false;
            if (adStatus) {
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
                if (adStatus) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobAppOpenId)) {
                                    loadBackupAppOpenAd(onShowAdCompleteListener);
                                    return;
                                }
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity.getApplicationContext(),
                                        adMobAppOpenId, adRequest,
                                        new com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.appopen.AppOpenAd ad) {
                                                com.partharoypc.adglide.util.AdMobRateLimiter
                                                        .resetCooldown(adMobAppOpenId);
                                                appOpenAd = ad;
                                                showAppOpenAd(onShowAdCompleteListener);
                                                Log.d(TAG, "[" + adNetwork + "] " + "[on start] app open ad loaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                if (loadAdError
                                                        .getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                                    com.partharoypc.adglide.util.AdMobRateLimiter
                                                            .recordFailure(adMobAppOpenId);
                                                }
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
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
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
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
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
                if (adStatus) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity.getApplicationContext(),
                                        adMobAppOpenId, adRequest,
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
                if (adStatus) {
                    if (waterfallManager == null) {
                        if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            onShowAdCompleteListener.onShowAdComplete();
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup app open ads failed to load");
                        onShowAdCompleteListener.onShowAdComplete();
                        return;
                    }

                    backupAdNetwork = networkToLoad;
                    Log.d(TAG,
                            "Loading Backup App Open Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity.getApplicationContext(),
                                        adMobAppOpenId, adRequest,
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
                                loadBackupAppOpenAd(onShowAdCompleteListener);
                            }
                            break;

                        default:
                            loadBackupAppOpenAd(onShowAdCompleteListener);
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
                            if (onShowAdCompleteListener != null) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
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
                if (adStatus) {
                    if (waterfallManager == null) {
                        if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup app open ads failed to load");
                        return;
                    }

                    backupAdNetwork = networkToLoad;
                    Log.d(TAG,
                            "Loading Backup App Open Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB:
                            try {
                                AdRequest adRequest = new AdRequest.Builder().build();
                                com.google.android.gms.ads.appopen.AppOpenAd.load(activity.getApplicationContext(),
                                        adMobAppOpenId, adRequest,
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
                                loadBackupAppOpenAd();
                            }
                            break;

                        default:
                            loadBackupAppOpenAd();
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
