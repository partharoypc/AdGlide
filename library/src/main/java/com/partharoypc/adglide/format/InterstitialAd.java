package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.HOUSE_AD;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.partharoypc.adglide.provider.InterstitialProviderFactory;
import com.partharoypc.adglide.util.OnInterstitialAdDismissedListener;
import com.partharoypc.adglide.util.OnInterstitialAdShowedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

public class InterstitialAd {

    public static class Builder implements InterstitialProvider.InterstitialConfig {

        private static final String TAG = "AdGlide";
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private InterstitialProvider currentProvider;

        private int counter = 1;

        private boolean adStatus = false;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobInterstitialId = "";
        private String metaInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String ironSourceInterstitialId = "";
        private String wortiseInterstitialId = "";
        private String startAppId = "";
        private int interval = 1;
        private boolean testMode = false;
        private boolean debug = true;

        // Internal flag to show the ad immediately when loaded (used for on-the-fly
        // calls)
        private boolean showOnLoad = false;
        private OnInterstitialAdShowedListener showedListener;
        private OnInterstitialAdDismissedListener dismissedListener;

        public Builder(@NonNull Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adStatus = com.partharoypc.adglide.AdGlide.isInterstitialEnabled();
            if (com.partharoypc.adglide.AdGlide.getConfig() != null) {
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                this.adNetwork = config.getPrimaryNetwork();
                if (!config.getBackupNetworks().isEmpty()) {
                    this.backupAdNetwork = config.getBackupNetworks().get(0);
                    this.waterfallManager = new com.partharoypc.adglide.util.WaterfallManager(
                            config.getBackupNetworks().toArray(new String[0]));
                }
                this.adMobInterstitialId = config.getAdMobInterstitialId();
                this.metaInterstitialId = config.getMetaInterstitialId();
                this.unityInterstitialId = config.getUnityInterstitialId();
                this.appLovinInterstitialId = config.getAppLovinInterstitialId();
                this.ironSourceInterstitialId = config.getIronSourceInterstitialId();
                this.wortiseInterstitialId = config.getWortiseInterstitialId();
                this.startAppId = config.getStartAppId();
                this.testMode = config.isTestMode();
                this.debug = config.isDebug();
            }
        }

        @Override
        public boolean isDebug() {
            return debug;
        }

        @Override
        public boolean isTestMode() {
            return testMode;
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder build(OnInterstitialAdDismissedListener listener) {
            return this;
        }

        @NonNull
        public Builder load() {
            loadInterstitialAd(null);
            return this;
        }

        @NonNull
        public Builder load(OnInterstitialAdDismissedListener listener) {
            loadInterstitialAd(listener);
            return this;
        }

        /**
         * Used internally by AdGlide to request an ad on the fly and show it
         * immediately.
         */
        @NonNull
        public Builder loadAndShow(Activity displayActivity,
                OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            this.showOnLoad = true;
            this.showedListener = showedListener;
            this.dismissedListener = dismissedListener;
            loadInterstitialAd(dismissedListener);
            return this;
        }

        public void show() {
            Activity activity = activityRef.get();
            showInterstitialAd(activity, null, null);
        }

        public void show(@NonNull Activity displayActivity) {
            showInterstitialAd(displayActivity, null, null);
        }

        public void show(OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            Activity activity = activityRef.get();
            showInterstitialAd(activity, showedListener, dismissedListener);
        }

        public void show(@NonNull Activity displayActivity,
                OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            showInterstitialAd(displayActivity, showedListener, dismissedListener);
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
            return network(network.getValue());
        }

        @NonNull
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            this.waterfallManager = new WaterfallManager(this.backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @NonNull
        public Builder backups(@Nullable String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks != null && backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
            }
            return this;
        }

        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobInterstitialId) {
            this.adMobInterstitialId = adMobInterstitialId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaInterstitialId) {
            this.metaInterstitialId = metaInterstitialId;
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String unityInterstitialId) {
            this.unityInterstitialId = unityInterstitialId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinInterstitialId) {
            this.appLovinInterstitialId = appLovinInterstitialId;
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String appLovinInterstitialZoneId) {
            // MAX uses Ad Unit IDs, not Zone IDs.
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String ironSourceInterstitialId) {
            this.ironSourceInterstitialId = ironSourceInterstitialId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseInterstitialId) {
            this.wortiseInterstitialId = wortiseInterstitialId;
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String startAppId) {
            this.startAppId = startAppId;
            return this;
        }

        @NonNull
        public Builder interval(int interval) {
            this.interval = interval;
            return this;
        }

        @NonNull
        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        @NonNull
        public Builder testMode(boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            // Deprecated/Handled by SDK
            return this;
        }

        private void loadInterstitialAd(OnInterstitialAdDismissedListener listener) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isInterstitialEnabled() || !adStatus) {
                    Log.d(TAG, "Interstitial Ad is disabled globally or locally.");
                    if (showOnLoad && listener != null) {
                        showOnLoad = false;
                        listener.onInterstitialAdDismissed();
                    }
                    return;
                }
                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Interstitial.");
                    return;
                }

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    if (com.partharoypc.adglide.AdGlide.getConfig() != null
                            && com.partharoypc.adglide.AdGlide.getConfig().isHouseAdEnabled()) {
                        Log.d(TAG, "Falling back to House Ad due to offline status.");
                        loadAdFromNetwork(HOUSE_AD, listener);
                    } else {
                        if (showOnLoad && listener != null) {
                            showOnLoad = false;
                            listener.onInterstitialAdDismissed();
                        }
                    }
                    return;
                }
                if (waterfallManager != null) {
                    waterfallManager.reset();
                }
                Log.d(TAG, "Interstitial Ad is enabled: " + adNetwork);
                com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                        "Loading started: " + adNetwork);
                loadAdFromNetwork(adNetwork, listener);
            } catch (Exception e) {
                Log.e(TAG, "Error in loadInterstitialAd: " + e.getMessage());
            }
        }

        private void loadBackupInterstitialAd(OnInterstitialAdDismissedListener listener) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isInterstitialEnabled() || !adStatus) {
                    Log.d(TAG, "Interstitial Ad is disabled globally or locally. Skipping backup.");
                    if (showOnLoad && listener != null) {
                        showOnLoad = false;
                        listener.onInterstitialAdDismissed();
                    }
                    return;
                }
                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Interstitial backup.");
                    return;
                }

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    if (com.partharoypc.adglide.AdGlide.getConfig() != null
                            && com.partharoypc.adglide.AdGlide.getConfig().isHouseAdEnabled()) {
                        loadAdFromNetwork(HOUSE_AD, listener);
                    } else {
                        if (showOnLoad && listener != null) {
                            showOnLoad = false;
                            listener.onInterstitialAdDismissed();
                        }
                    }
                    return;
                }
                if (waterfallManager == null) {
                    if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                        waterfallManager = new WaterfallManager(backupAdNetwork);
                    } else {
                        if (showOnLoad && listener != null) {
                            showOnLoad = false;
                            listener.onInterstitialAdDismissed();
                        }
                        return;
                    }
                }

                String networkToLoad = waterfallManager.getNext();
                if (networkToLoad == null) {
                    Log.d(TAG, "All backup interstitial ads failed to load");
                    if (showOnLoad && listener != null) {
                        showOnLoad = false;
                        listener.onInterstitialAdDismissed();
                    }
                    return;
                }
                Log.d(TAG, "Loading Backup Interstitial Ad [" + networkToLoad.toUpperCase(java.util.Locale.ROOT)
                        + "]");
                loadAdFromNetwork(networkToLoad, listener);
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupInterstitialAd: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad, OnInterstitialAdDismissedListener listener) {
            try {
                destroyInterstitialAd();
                String adUnitId = getAdUnitIdForNetwork(this, networkToLoad);
                Log.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT)
                        + "] Interstitial Ad with ID: " + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    Log.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    loadBackupInterstitialAd(listener);
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Interstitial from network.");
                    return;
                }

                InterstitialProvider provider = InterstitialProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    currentProvider = provider;
                    provider.loadInterstitial(activity, adUnitId, this,
                            new InterstitialProvider.InterstitialListener() {
                                @Override
                                public void onAdLoaded() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Loaded: " + networkToLoad);
                                    Log.d(TAG, networkToLoad + " Interstitial Ad loaded");

                                    if (showOnLoad) {
                                        showOnLoad = false;
                                        showInterstitialAd(activity, showedListener, dismissedListener);
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(String error) {
                                    com.partharoypc.adglide.util.PerformanceLogger.error("Interstitial",
                                            "Failed [" + networkToLoad + "]: " + error);
                                    Log.e(TAG, networkToLoad + " Interstitial Ad failed to load: " + error);
                                    loadBackupInterstitialAd(listener);
                                }

                                @Override
                                public void onAdDismissed() {
                                    if (listener != null) {
                                        listener.onInterstitialAdDismissed();
                                    }
                                    loadInterstitialAd(listener); // Load next ad after dismissal
                                }

                                @Override
                                public void onAdShowFailed(String error) {
                                    Log.e(TAG, networkToLoad + " Interstitial Ad failed to show: " + error);
                                    if (listener != null) {
                                        listener.onInterstitialAdDismissed();
                                    }
                                    loadInterstitialAd(listener); // Load next ad after show failure
                                }

                                @Override
                                public void onAdShowed() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Showed: " + networkToLoad);
                                    Log.d(TAG, networkToLoad + " Interstitial Ad showed");
                                }
                            });
                } else {
                    Log.d(TAG, "No provider found for network: " + networkToLoad + ". Trying backup.");
                    loadBackupInterstitialAd(listener);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load interstitial for " + networkToLoad + ". Error: " + e.getMessage());
                loadBackupInterstitialAd(listener);
            }
        }

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobInterstitialId;
                case META -> builder.metaInterstitialId;
                case UNITY -> builder.unityInterstitialId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinInterstitialId;
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> builder.ironSourceInterstitialId;
                case STARTAPP -> !builder.startAppId.isEmpty() ? builder.startAppId : "startapp";
                case WORTISE -> builder.wortiseInterstitialId;
                case HOUSE_AD -> "house_ad";
                default -> "";
            };
        }

        public void showInterstitialAd(Activity displayActivity,
                OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isInterstitialEnabled() || !adStatus) {
                    Log.d(TAG, "Interstitial Ad is disabled globally or locally. Calling dismissed listener.");
                    if (dismissedListener != null) {
                        dismissedListener.onInterstitialAdDismissed();
                    }
                    return;
                }

                if (counter >= interval) {
                    if (currentProvider != null && currentProvider.isAdLoaded()) {
                        Activity activity = activityRef.get();
                        currentProvider.showInterstitial(displayActivity != null ? displayActivity : activity,
                                new InterstitialProvider.InterstitialListener() {
                                    @Override
                                    public void onAdLoaded() {
                                    }

                                    @Override
                                    public void onAdFailedToLoad(String error) {
                                    }

                                    @Override
                                    public void onAdDismissed() {
                                        if (dismissedListener != null) {
                                            dismissedListener.onInterstitialAdDismissed();
                                        }
                                        loadInterstitialAd(dismissedListener); // Load next ad after dismissal
                                    }

                                    @Override
                                    public void onAdShowFailed(String error) {
                                        Log.e(TAG, "Interstitial Ad failed to show: " + error);
                                        if (dismissedListener != null) {
                                            dismissedListener.onInterstitialAdDismissed();
                                        }
                                        loadInterstitialAd(dismissedListener); // Load next ad after show failure
                                    }

                                    @Override
                                    public void onAdShowed() {
                                        if (showedListener != null) {
                                            showedListener.onInterstitialAdShowed();
                                        }
                                    }
                                });
                        counter = 1;
                    } else {
                        Log.d(TAG,
                                "Primary interstitial ad not loaded. Skipping show and calling dismissed listener.");
                        // If primary ad is not loaded, we don't try to show backup immediately.
                        // The backup logic is handled during the load phase.
                        if (dismissedListener != null) {
                            dismissedListener.onInterstitialAdDismissed();
                        }
                        loadInterstitialAd(dismissedListener); // Ensure a new ad is loaded for next time
                        counter = 1; // Reset counter as if an ad was shown (or attempted)
                    }
                } else {
                    counter++;
                    Log.d(TAG, "Interstitial interval not met. Current counter: " + counter);
                    if (dismissedListener != null) {
                        dismissedListener.onInterstitialAdDismissed();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showInterstitialAd: " + e.getMessage());
                if (dismissedListener != null) {
                    dismissedListener.onInterstitialAdDismissed();
                }
            }
        }

        public void showBackupInterstitialAd(Activity displayActivity,
                OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            // Simplified: Backup is handled by waterfall during load.
            // If primary show fails or is not ready, we just ensure the dismissed listener
            // is called
            // and a new ad is loaded for the next impression.
            Log.d(TAG,
                    "showBackupInterstitialAd called. Primary ad was not ready or failed to show. Triggering dismissed listener and reloading.");
            if (dismissedListener != null) {
                dismissedListener.onInterstitialAdDismissed();
            }
            loadInterstitialAd(dismissedListener); // Load a new ad for the next cycle
        }

        public void destroyInterstitialAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }

        public boolean isAdLoaded() {
            return currentProvider != null && currentProvider.isAdLoaded();
        }
    }
}
