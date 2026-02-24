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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

/**
 * Manages app open ads with primary and backup network support.
 * Supports AdMob, AppLovin, and Wortise networks.
 */
@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";

    public static boolean isAppOpenAdLoaded = false;

    // Configuration fields
    private boolean adStatus = true;
    private String adNetwork = "";
    private String backupAdNetwork = "";
    private WaterfallManager waterfallManager;
    private String adMobAppOpenId = "";
    private String appLovinAppOpenId = "";
    private String wortiseAppOpenId = "";
    private Activity currentActivity;
    private int placementStatus = 1;

    // Helper instances (static so the inner Builder class can access them)
    private static AdMobAppOpenAd adMobAppOpenAd = new AdMobAppOpenAd();
    private static AppLovinAppOpenAd appLovinAppOpenAd = new AppLovinAppOpenAd();
    private static WortiseAppOpenAd appOpenAdWortise = new WortiseAppOpenAd();

    public AppOpenAd() {
    }

    private AppOpenAd(Builder builder) {
        this.adStatus = builder.adStatus;
        this.adNetwork = builder.adNetwork;
        this.backupAdNetwork = builder.backupAdNetwork;
        this.waterfallManager = builder.waterfallManager;
        this.adMobAppOpenId = builder.adMobAppOpenId;
        this.appLovinAppOpenId = builder.appLovinAppOpenId;
        this.wortiseAppOpenId = builder.wortiseAppOpenId;
    }

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
        this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
        return this;
    }

    @NonNull
    public AppOpenAd network(AdGlideNetwork network) {
        this.adNetwork = network.getValue();
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
            if (placementStatus != 0 && adStatus && currentActivity != null) {
                if (currentActivity.getIntent().hasExtra("unique_id")) {
                    return;
                }
                showAdIfAvailable(currentActivity, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
        }
    }

    public void onStartActivityLifecycleCallbacks(Activity activity) {
        try {
            if (placementStatus != 0 && adStatus) {
                boolean isShowing = false;
                switch (AdGlideNetwork.fromString(adNetwork)) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        isShowing = adMobAppOpenAd.isShowingAd();
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        isShowing = appLovinAppOpenAd.isShowingAd();
                        break;
                    case WORTISE:
                        isShowing = appOpenAdWortise.isShowingAd();
                        break;
                }
                if (!isShowing) {
                    currentActivity = activity;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartActivityLifecycleCallbacks: " + e.getMessage());
        }
    }

    public AppOpenAd showAppOpenAdIfAvailable(Activity activity,
            OnShowAdCompleteListener onShowAdCompleteListener) {
        showAdIfAvailable(activity, onShowAdCompleteListener);
        return this;
    }

    public void showAdIfAvailable(@NonNull Activity activity,
            @Nullable OnShowAdCompleteListener onShowAdCompleteListener) {
        try {
            if (placementStatus != 0 && adStatus) {
                switch (AdGlideNetwork.fromString(adNetwork)) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (!adMobAppOpenId.equals("0")) {
                            adMobAppOpenAd.showAdIfAvailable(activity, adMobAppOpenId,
                                    onShowAdCompleteListener);
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
            } else if (onShowAdCompleteListener != null) {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showAdIfAvailable: " + e.getMessage());
            if (onShowAdCompleteListener != null) {
                onShowAdCompleteListener.onShowAdComplete();
            }
        }
    }

    // ── Builder ──────────────────────────────────────────────────────────

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

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            this.adNetwork = network.getValue();
            return this;
        }

        @NonNull
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @NonNull
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobAppOpenId) {
            this.adMobAppOpenId = adMobAppOpenId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinAppOpenId) {
            this.appLovinAppOpenId = appLovinAppOpenId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseAppOpenId) {
            this.wortiseAppOpenId = wortiseAppOpenId;
            return this;
        }

        @NonNull
        public Builder load() {
            loadAppOpenAd(null);
            return this;
        }

        @NonNull
        public Builder load(OnShowAdCompleteListener onShowAdCompleteListener) {
            loadAppOpenAd(onShowAdCompleteListener);
            return this;
        }

        public void loadAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                if (!adStatus) {
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "No internet. Skipping App Open load.");
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                if (waterfallManager != null)
                    waterfallManager.reset();

                loadAdFromNetwork(adNetwork, onShowAdCompleteListener);
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAppOpenAd: " + e.getMessage());
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        private void loadAdFromNetwork(String network,
                OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                switch (AdGlideNetwork.fromString(network)) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        if (!com.partharoypc.adglide.util.AdMobRateLimiter
                                .isRequestAllowed(adMobAppOpenId)) {
                            loadBackupAppOpenAd(onShowAdCompleteListener);
                            return;
                        }
                        adMobAppOpenAd.loadAd(activity, adMobAppOpenId);
                        showAppOpenAd(onShowAdCompleteListener);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        appLovinAppOpenAd.loadAd(activity, appLovinAppOpenId);
                        showAppOpenAd(onShowAdCompleteListener);
                        break;
                    case WORTISE:
                        appOpenAdWortise.loadAd(activity, wortiseAppOpenId);
                        showAppOpenAd(onShowAdCompleteListener);
                        break;
                    default:
                        loadBackupAppOpenAd(onShowAdCompleteListener);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed loading AppOpen from " + network + ": "
                        + e.getMessage());
                loadBackupAppOpenAd(onShowAdCompleteListener);
            }
        }

        private void loadBackupAppOpenAd(
                OnShowAdCompleteListener onShowAdCompleteListener) {
            if (waterfallManager != null && waterfallManager.hasNext()) {
                String nextNetwork = waterfallManager.getNext();
                Log.d(TAG, "Loading backup AppOpen from: " + nextNetwork);
                loadAdFromNetwork(nextNetwork, onShowAdCompleteListener);
            } else {
                Log.d(TAG, "All AppOpen backups exhausted.");
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void showAppOpenAd() {
            showAppOpenAd(null);
        }

        public void showAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                switch (AdGlideNetwork.fromString(adNetwork)) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
                        adMobAppOpenAd.showAdIfAvailable(activity, adMobAppOpenId,
                                onShowAdCompleteListener);
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX:
                        appLovinAppOpenAd.showAdIfAvailable(activity, appLovinAppOpenId,
                                onShowAdCompleteListener);
                        break;
                    case WORTISE:
                        appOpenAdWortise.showAdIfAvailable(activity, wortiseAppOpenId,
                                onShowAdCompleteListener);
                        break;
                    default:
                        if (onShowAdCompleteListener != null)
                            onShowAdCompleteListener.onShowAdComplete();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showAppOpenAd: " + e.getMessage());
                if (onShowAdCompleteListener != null)
                    onShowAdCompleteListener.onShowAdComplete();
            }
        }

        public void destroyOpenAd() {
            AppOpenAd.isAppOpenAdLoaded = false;
        }

        public AppOpenAd build() {
            return new AppOpenAd(this);
        }
    }

}
