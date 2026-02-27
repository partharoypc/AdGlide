package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.annotation.SuppressLint;
import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.partharoypc.adglide.provider.AppOpenProviderFactory;
import com.partharoypc.adglide.util.OnShowAdCompleteListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class AppOpenAd {
    private static final String TAG = "AdGlide";

    public static boolean isAppOpenAdLoaded = false;

    /**
     * Timestamp (ms) of the last time an App Open Ad was shown. 0 = never shown.
     */
    private static long lastShownTimeMs = 0;

    /** Cooldown between App Open Ad impressions: default 30 minutes. */
    private static long cooldownMs = 30 * 60 * 1000L;

    /**
     * Sets the cooldown between App Open Ad impressions.
     * 
     * @param cooldownMinutes Cooldown in minutes.
     */
    public static void setCooldown(int cooldownMinutes) {
        cooldownMs = (long) cooldownMinutes * 60 * 1000L;
    }

    /** Returns {@code true} if enough time has passed since the last impression. */
    public static boolean isCooldownElapsed() {
        return lastShownTimeMs == 0 || (System.currentTimeMillis() - lastShownTimeMs) >= cooldownMs;
    }

    private boolean adStatus = false;
    private String adNetwork = "";
    private String backupAdNetwork = "";
    private WaterfallManager waterfallManager;
    private String adMobAppOpenId = "";
    private String metaAppOpenId = "";
    private String appLovinAppOpenId = "";
    private String wortiseAppOpenId = "";
    private java.lang.ref.WeakReference<Activity> activityRef;
    private int placementStatus = 0;

    // Provider management
    private static final Map<String, AppOpenProvider> providers = new HashMap<>();

    public AppOpenAd() {
    }

    private AppOpenAd(Builder builder) {
        this.adStatus = builder.adStatus;
        this.adNetwork = builder.adNetwork;
        this.backupAdNetwork = builder.backupAdNetwork;
        this.waterfallManager = builder.waterfallManager;
        this.adMobAppOpenId = builder.adMobAppOpenId;
        this.metaAppOpenId = builder.metaAppOpenId;
        this.appLovinAppOpenId = builder.appLovinAppOpenId;
        this.wortiseAppOpenId = builder.wortiseAppOpenId;
        if (builder.cooldownMinutes > 0) {
            setCooldown(builder.cooldownMinutes);
        }
    }

    private static synchronized AppOpenProvider getProvider(String network) {
        AppOpenProvider provider = providers.get(network);
        if (provider == null) {
            provider = AppOpenProviderFactory.getProvider(network);
            if (provider != null) {
                providers.put(network, provider);
            }
        }
        return provider;
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
        this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
        this.waterfallManager = new WaterfallManager(backupAdNetwork);
        return this;
    }

    @NonNull
    public AppOpenAd backups(@Nullable String... backupAdNetworks) {
        this.waterfallManager = new WaterfallManager(backupAdNetworks);
        if (backupAdNetworks != null && backupAdNetworks.length > 0) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
        }
        return this;
    }

    @NonNull
    public AppOpenAd adMobId(@NonNull String adMobAppOpenId) {
        this.adMobAppOpenId = adMobAppOpenId;
        return this;
    }

    @NonNull
    public AppOpenAd metaId(@NonNull String metaAppOpenId) {
        this.metaAppOpenId = metaAppOpenId;
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
            Activity activity = activityRef != null ? activityRef.get() : null;
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            boolean isAppOpenEnabled = config != null && config.isAppOpenEnabled();
            if (placementStatus != 0 && adStatus && isAppOpenEnabled && activity != null) {
                if (activity.getIntent().hasExtra("unique_id")) {
                    return;
                }
                showAdIfAvailable(activity, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartLifecycleObserver: " + e.getMessage());
        }
    }

    public void onStartActivityLifecycleCallbacks(Activity activity) {
        try {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            boolean isAppOpenEnabled = config != null && config.isAppOpenEnabled();
            if (placementStatus != 0 && adStatus && isAppOpenEnabled) {
                AppOpenProvider provider = getProvider(adNetwork);
                boolean isShowing = provider != null && provider.isShowingAd();
                if (!isShowing) {
                    activityRef = new java.lang.ref.WeakReference<>(activity);
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
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            boolean isAppOpenEnabled = config != null && config.isAppOpenEnabled();
            if (placementStatus != 0 && adStatus && isAppOpenEnabled) {
                // ── 30-minute cooldown check ──────────────────────────────
                if (!isCooldownElapsed()) {
                    Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                AppOpenProvider provider = getProvider(adNetwork);
                String adUnitId = getAdUnitIdForNetwork(adNetwork);

                if (provider != null && !adUnitId.equals("0")) {
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdDismissed() {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                            // Record the time the ad was displayed
                            lastShownTimeMs = System.currentTimeMillis();
                        }
                    });
                } else if (onShowAdCompleteListener != null) {
                    onShowAdCompleteListener.onShowAdComplete();
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

    private String getAdUnitIdForNetwork(String network) {
        return Builder.getAdUnitIdForNetwork(this, network);
    }

    // ── Builder ──────────────────────────────────────────────────────────

    public static class Builder {
        private static final String TAG = "AdGlide";
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private boolean adStatus = false;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobAppOpenId = "";
        private String metaAppOpenId = "";
        private String appLovinAppOpenId = "";
        private String wortiseAppOpenId = "";
        private int placementStatus = 0;
        private int cooldownMinutes = -1;

        public Builder(Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            if (com.partharoypc.adglide.AdGlide.getConfig() != null) {
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                this.adStatus = config.getAdStatus();
                this.adNetwork = config.getPrimaryNetwork();
                if (!config.getBackupNetworks().isEmpty()) {
                    this.backupAdNetwork = config.getBackupNetworks().get(0);
                    this.waterfallManager = new com.partharoypc.adglide.util.WaterfallManager(
                            config.getBackupNetworks().toArray(new String[0]));
                }
                this.adMobAppOpenId = config.getAdMobAppOpenId();
                this.metaAppOpenId = config.getMetaAppOpenId();
                this.appLovinAppOpenId = config.getAppLovinAppOpenId();
                this.wortiseAppOpenId = config.getWortiseAppOpenId();
            }
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
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
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
        public Builder metaId(@NonNull String metaAppOpenId) {
            this.metaAppOpenId = metaAppOpenId;
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
        public Builder cooldown(int minutes) {
            this.cooldownMinutes = minutes;
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
                AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                boolean isAppOpenEnabled = config != null && config.isAppOpenEnabled();

                if (!adStatus || !isAppOpenEnabled) {
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }
                if (placementStatus == 0) {
                    Log.d(TAG, "App Open Ad is disabled via placementStatus");
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load App Open.");
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

                com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Loading started: " + adNetwork);
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
                String adUnitId = getAdUnitIdForNetwork(this, network);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !network.equals("startapp"))) {
                    Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                    loadBackupAppOpenAd(onShowAdCompleteListener);
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load App Open from network.");
                    return;
                }
                AppOpenProvider provider = getProvider(network);
                if (provider != null) {
                    provider.loadAppOpenAd(activity, adUnitId, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                            isAppOpenAdLoaded = true;
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Loaded: " + network);
                            Log.d(TAG, "AppOpen ad loaded from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]. Showing now.");
                            // ── 30-minute cooldown check (splash path) ────
                            if (!isCooldownElapsed()) {
                                Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                                if (onShowAdCompleteListener != null)
                                    onShowAdCompleteListener.onShowAdComplete();
                                return;
                            }
                            // Show the ad immediately — onAdDismissed / onAdShowFailed
                            // will call onShowAdCompleteListener to continue the splash flow.
                            Activity activity = activityRef.get();
                            if (activity != null) {
                                provider.showAppOpenAd(activity, this);
                            } else {
                                if (onShowAdCompleteListener != null)
                                    onShowAdCompleteListener.onShowAdComplete();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("AppOpen",
                                    "Failed [" + network + "]: " + error);
                            Log.e(TAG, "AppOpen failed to load from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]: " + error);
                            loadBackupAppOpenAd(onShowAdCompleteListener);
                        }

                        @Override
                        public void onAdDismissed() {
                            isAppOpenAdLoaded = false;
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            Log.e(TAG, "AppOpen failed to show from [" + network.toUpperCase(java.util.Locale.ROOT)
                                    + "]: " + error);
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                            com.partharoypc.adglide.util.PerformanceLogger.log("AppOpen", "Showed: " + network);
                            Log.d(TAG, "AppOpen ad showed from [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
                            // Record the time the ad was displayed
                            lastShownTimeMs = System.currentTimeMillis();
                        }
                    });
                } else {
                    loadBackupAppOpenAd(onShowAdCompleteListener);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed loading AppOpen from " + network + ": " + e.getMessage());
                loadBackupAppOpenAd(onShowAdCompleteListener);
            }
        }

        private void loadBackupAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
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

        private static String getAdUnitIdForNetwork(AppOpenAd ad, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> ad.adMobAppOpenId;
                case META -> ad.metaAppOpenId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> ad.appLovinAppOpenId;
                case WORTISE -> ad.wortiseAppOpenId;
                default -> "0";
            };
        }

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobAppOpenId;
                case META -> builder.metaAppOpenId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinAppOpenId;
                case WORTISE -> builder.wortiseAppOpenId;
                default -> "0";
            };
        }

        public void showAppOpenAd() {
            showAppOpenAd(null);
        }

        public void showAppOpenAd(OnShowAdCompleteListener onShowAdCompleteListener) {
            try {
                // ── 30-minute cooldown check ──────────────────────────────
                if (!isCooldownElapsed()) {
                    Log.d(TAG, "App Open Ad skipped — cooldown not elapsed yet.");
                    if (onShowAdCompleteListener != null)
                        onShowAdCompleteListener.onShowAdComplete();
                    return;
                }

                AppOpenProvider provider = getProvider(adNetwork);
                if (provider != null && provider.isAdAvailable()) {
                    Activity activity = activityRef.get();
                    provider.showAppOpenAd(activity, new AppOpenProvider.AppOpenListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                        }

                        @Override
                        public void onAdDismissed() {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowFailed(String error) {
                            if (onShowAdCompleteListener != null)
                                onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowed() {
                            // Record the time the ad was displayed
                            lastShownTimeMs = System.currentTimeMillis();
                        }
                    });
                } else if (onShowAdCompleteListener != null) {
                    onShowAdCompleteListener.onShowAdComplete();
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

        public boolean isAdAvailable() {
            AppOpenProvider provider = getProvider(adNetwork);
            return provider != null && provider.isAdAvailable();
        }

        public AppOpenAd build() {
            return new AppOpenAd(this);
        }
    }
}
