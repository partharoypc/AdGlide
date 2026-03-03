package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.partharoypc.adglide.provider.RewardedProviderFactory;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying rewarded ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class RewardedAd {

    private static final String TAG = "AdGlide";

    public static class Builder {
        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private RewardedProvider currentProvider;
        private boolean showOnLoad = false;
        private AdGlideCallback callback;

        public Builder(@NonNull Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                    com.partharoypc.adglide.util.AdFormat.REWARDED);
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder build(AdGlideCallback callback) {
            loadRewardedAd(callback);
            return this;
        }

        @NonNull
        public Builder build(AdGlideCallback callback, boolean unused) {
            loadRewardedAd(callback);
            return this;
        }

        @NonNull
        public Builder show(AdGlideCallback callback) {
            Activity activity = activityRef.get();
            showRewardedAd(activity, callback);
            return this;
        }

        @NonNull
        public Builder show(@NonNull Activity displayActivity, AdGlideCallback callback) {
            showRewardedAd(displayActivity, callback);
            return this;
        }

        @NonNull
        public Builder load() {
            loadRewardedAd(null);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback) {
            loadRewardedAd(callback);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback, boolean unused) {
            loadRewardedAd(callback);
            return this;
        }

        /**
         * Used internally by AdGlide to request an ad on the fly and show it
         * immediately.
         */
        @NonNull
        public Builder loadAndShow(Activity displayActivity, AdGlideCallback callback) {
            this.showOnLoad = true;
            this.callback = callback;
            loadRewardedAd(callback);
            return this;
        }

        // --- Deprecated chained methods. Keeping for broad API compatibility where
        // possible, but they do nothing useful now as config is global ---

        @NonNull
        public Builder status(boolean adStatus) {
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return this;
        }

        @NonNull
        public Builder backup(@Nullable String backupAdNetwork) {
            return this;
        }

        @NonNull
        public Builder backups(@Nullable String... backupAdNetworks) {
            return this;
        }

        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return this;
        }

        @NonNull
        public Builder adMobId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            return this;
        }

        public void loadRewardedAd(AdGlideCallback callback) {
            loadRewardedAdMain(false, callback);
        }

        public void loadRewardedBackupAd(AdGlideCallback callback) {
            loadRewardedAdMain(true, callback);
        }

        private void loadRewardedAdMain(boolean isBackup, AdGlideCallback callback) {
            if (adLoader == null)
                return;
            if (!isBackup) {
                adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Rewarded load failed: " + error);
                        if (callback != null) {
                            callback.onAdFailedToLoad(error);
                        }
                        if (showOnLoad && callback != null) {
                            showOnLoad = false;
                            callback.onAdDismissed();
                        }
                    }
                });
            } else {
                adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Rewarded backup load failed: " + error);
                        if (callback != null) {
                            callback.onAdFailedToLoad(error);
                        }
                        if (showOnLoad && callback != null) {
                            showOnLoad = false;
                            callback.onAdDismissed();
                        }
                    }
                });
            }
        }

        private void loadAdFromNetwork(String network, AdGlideCallback callback) {
            Activity activity = activityRef.get();
            if (activity == null) {
                Log.e(TAG, "Activity is null. Cannot load Rewarded from network.");
                return;
            }

            destroy();
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadRewardedBackupAd(callback);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            Log.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Rewarded Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                loadRewardedBackupAd(callback);
                return;
            }

            final boolean legacyGDPR = com.partharoypc.adglide.AdGlide.getConfig() != null &&
                    com.partharoypc.adglide.AdGlide.getConfig().isLegacyGDPR();

            RewardedProvider.RewardedConfig config = new RewardedProvider.RewardedConfig() {
                @Override
                public boolean isLegacyGDPR() {
                    return legacyGDPR;
                }

                @Override
                public boolean isInterstitial() {
                    return false;
                }
            };

            provider.loadRewardedAd(activity, adUnitId, config, new RewardedProvider.RewardedListener() {
                @Override
                public void onAdLoaded() {
                    com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded", "Loaded: " + network);
                    Log.d(TAG, network + " Rewarded ad loaded");
                    if (callback != null) {
                        callback.onAdLoaded();
                    }
                    if (showOnLoad) {
                        showOnLoad = false;
                        showRewardedAd(activity, callback);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    com.partharoypc.adglide.util.PerformanceLogger.error("Rewarded",
                            "Failed [" + network + "]: " + error);
                    Log.e(TAG, network + " Rewarded ad failed to load: " + error);
                    loadRewardedBackupAd(callback);
                }

                @Override
                public void onAdDismissed() {
                    if (callback != null)
                        callback.onAdDismissed();
                    loadRewardedAd(callback);
                }

                @Override
                public void onAdCompleted() {
                    if (callback != null)
                        callback.onAdCompleted();
                }
            });
        }

        public void showRewardedAd(AdGlideCallback callback) {
            Activity activity = activityRef.get();
            showRewardedAd(activity, callback);
        }

        public void showRewardedAd(Activity displayActivity, AdGlideCallback callback) {
            if (currentProvider != null && currentProvider.isAdAvailable()) {
                Activity activity = activityRef.get();
                currentProvider.showRewardedAd(displayActivity != null ? displayActivity : activity,
                        new RewardedProvider.RewardedListener() {
                            @Override
                            public void onAdLoaded() {
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                if (callback != null)
                                    callback.onAdFailedToLoad(error);
                            }

                            @Override
                            public void onAdDismissed() {
                                if (callback != null)
                                    callback.onAdDismissed();
                            }

                            @Override
                            public void onAdCompleted() {
                                com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded",
                                        "Completed: "
                                                + (currentProvider != null ? currentProvider.getClass().getSimpleName()
                                                        : "Unknown"));
                                if (callback != null)
                                    callback.onAdCompleted();
                            }
                        });
            } else {
                Log.w(TAG, "No ad available to show.");
                if (callback != null)
                    callback.onAdFailedToLoad("No ad available");
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "0";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobRewardedId();
                case META -> config.getMetaRewardedId();
                case UNITY -> config.getUnityRewardedId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinRewardedId();
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> config.getIronSourceRewardedId();
                case STARTAPP -> !config.getStartAppId().isEmpty() ? config.getStartAppId() : "startapp_id";
                case WORTISE -> config.getWortiseRewardedId();
                default -> "0";
            };
        }

        public void destroy() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }

        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
