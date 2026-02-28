package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
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
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
import com.partharoypc.adglide.util.OnRewardedAdLoadedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying rewarded ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class RewardedAd {

    public static class Builder {
        private static final String TAG = "AdGlide.Rewarded";
        private final java.lang.ref.WeakReference<Activity> activityRef;

        private boolean adStatus = false;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobRewardedId = "";
        private String metaRewardedId = "";
        private String unityRewardedId = "";
        private String appLovinRewardedId = "";
        private String appLovinDiscRewardedZoneId = "";
        private String ironSourceRewardedId = "";
        private String startAppId = "";
        private String wortiseRewardedId = "";
        private boolean legacyGDPR = false;

        private RewardedProvider currentProvider;

        // Internal flag to show the ad immediately when loaded (used for on-the-fly
        // calls)
        private boolean showOnLoad = false;
        private OnRewardedAdCompleteListener onCompleteListener;
        private OnRewardedAdDismissedListener onDismissListener;
        private OnRewardedAdErrorListener onErrorListener;

        public Builder(@NonNull Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adStatus = com.partharoypc.adglide.AdGlide.isRewardedEnabled();
            if (com.partharoypc.adglide.AdGlide.getConfig() != null) {
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                this.adNetwork = config.getPrimaryNetwork();
                if (!config.getBackupNetworks().isEmpty()) {
                    this.backupAdNetwork = config.getBackupNetworks().get(0);
                    this.waterfallManager = new com.partharoypc.adglide.util.WaterfallManager(
                            config.getBackupNetworks().toArray(new String[0]));
                }
                this.adMobRewardedId = config.getAdMobRewardedId();
                this.metaRewardedId = config.getMetaRewardedId();
                this.unityRewardedId = config.getUnityRewardedId();
                this.appLovinRewardedId = config.getAppLovinRewardedId();
                this.appLovinDiscRewardedZoneId = config.getAppLovinDiscRewardedZoneId();
                this.ironSourceRewardedId = config.getIronSourceRewardedId();
                this.wortiseRewardedId = config.getWortiseRewardedId();
                this.startAppId = config.getStartAppId();
                this.legacyGDPR = config.isLegacyGDPR();
            }
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder build(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder show(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            Activity activity = activityRef.get();
            showRewardedAd(activity, onComplete, onDismiss, onError);
            return this;
        }

        @NonNull
        public Builder show(@NonNull Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(displayActivity, onComplete, onDismiss, onError);
            return this;
        }

        @NonNull
        public Builder load() {
            loadRewardedAd(null, null);
            return this;
        }

        @NonNull
        public Builder load(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder load(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        /**
         * Used internally by AdGlide to request an ad on the fly and show it
         * immediately.
         */
        @NonNull
        public Builder loadAndShow(Activity displayActivity,
                OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            this.showOnLoad = true;
            this.onCompleteListener = onComplete;
            this.onDismissListener = onDismiss;
            this.onErrorListener = onError;
            loadRewardedAd(onComplete, onDismiss);
            return this;
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
        public Builder adMobId(@NonNull String id) {
            this.adMobRewardedId = id;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String id) {
            this.metaRewardedId = id;
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String id) {
            this.unityRewardedId = id;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String id) {
            this.appLovinRewardedId = id;
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String id) {
            this.appLovinDiscRewardedZoneId = id;
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String id) {
            this.ironSourceRewardedId = id;
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String id) {
            this.startAppId = id;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String id) {
            this.wortiseRewardedId = id;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(false, onComplete, onDismiss);
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(true, onComplete, onDismiss);
        }

        private void loadRewardedAdMain(boolean isBackup, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isRewardedEnabled() || !adStatus) {
                    Log.d(TAG, "Rewarded Ad is disabled globally or locally.");
                    if (showOnLoad && onDismiss != null) {
                        showOnLoad = false;
                        onDismiss.onRewardedAdDismissed();
                    }
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Rewarded.");
                    if (showOnLoad && onDismiss != null) {
                        showOnLoad = false;
                        onDismiss.onRewardedAdDismissed();
                    }
                    return;
                }

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    if (showOnLoad && onDismiss != null) {
                        showOnLoad = false;
                        onDismiss.onRewardedAdDismissed();
                    }
                    return;
                }

                String network;
                if (isBackup) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            Log.d(TAG, "No backup ad network configured.");
                            if (showOnLoad && onDismiss != null) {
                                showOnLoad = false;
                                onDismiss.onRewardedAdDismissed();
                            }
                            return;
                        }
                    }
                    network = waterfallManager.getNext();
                    if (network == null) {
                        Log.d(TAG, "All backup rewarded ads failed to load");
                        if (showOnLoad && onDismiss != null) {
                            showOnLoad = false;
                            onDismiss.onRewardedAdDismissed();
                        }
                        return;
                    }
                } else {
                    network = adNetwork;
                    if (waterfallManager != null)
                        waterfallManager.reset();
                }

                if (network.equals(NONE)) {
                    loadRewardedBackupAd(onComplete, onDismiss);
                    return;
                }

                com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded", "Loading started: " + network);
                loadAdFromNetwork(network, onComplete, onDismiss);

            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedAdMain: " + e.getMessage());
                if (!isBackup)
                    loadRewardedBackupAd(onComplete, onDismiss);
            }
        }

        private void loadAdFromNetwork(String network, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            Activity activity = activityRef.get();
            if (activity == null) {
                Log.e(TAG, "Activity is null. Cannot load Rewarded from network.");
                return;
            }

            destroy();
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadRewardedBackupAd(onComplete, onDismiss);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(this, network);
            Log.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Rewarded Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                loadRewardedBackupAd(onComplete, onDismiss);
                return;
            }

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

                    if (showOnLoad) {
                        showOnLoad = false;
                        showRewardedAd(activity, onCompleteListener, onDismissListener, onErrorListener);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    com.partharoypc.adglide.util.PerformanceLogger.error("Rewarded",
                            "Failed [" + network + "]: " + error);
                    Log.e(TAG, network + " Rewarded ad failed to load: " + error);
                    loadRewardedBackupAd(onComplete, onDismiss);
                }

                @Override
                public void onAdDismissed() {
                    if (onDismiss != null)
                        onDismiss.onRewardedAdDismissed();
                    loadRewardedAd(onComplete, onDismiss);
                }

                @Override
                public void onAdCompleted() {
                    if (onComplete != null)
                        onComplete.onRewardedAdComplete();
                }
            });
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            Activity activity = activityRef.get();
            showRewardedAd(activity, onComplete, onDismiss, onError);
        }

        public void showRewardedAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            if (currentProvider != null && currentProvider.isAdAvailable()) {
                Activity activity = activityRef.get();
                currentProvider.showRewardedAd(displayActivity != null ? displayActivity : activity,
                        new RewardedProvider.RewardedListener() {
                            @Override
                            public void onAdLoaded() {
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                if (onError != null)
                                    onError.onRewardedAdError();
                            }

                            @Override
                            public void onAdDismissed() {
                                if (onDismiss != null)
                                    onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdCompleted() {
                                com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded",
                                        "Completed: "
                                                + (currentProvider != null ? currentProvider.getClass().getSimpleName()
                                                        : "Unknown"));
                                if (onComplete != null)
                                    onComplete.onRewardedAdComplete();
                            }
                        });
            } else {
                Log.w(TAG, "No ad available to show.");
                if (onError != null)
                    onError.onRewardedAdError();
            }
        }

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobRewardedId;
                case META -> builder.metaRewardedId;
                case UNITY -> builder.unityRewardedId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinRewardedId;
                case APPLOVIN_DISCOVERY -> builder.appLovinDiscRewardedZoneId;
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> builder.ironSourceRewardedId;
                case STARTAPP -> !builder.startAppId.isEmpty() ? builder.startAppId : "startapp_id";
                case WORTISE -> builder.wortiseRewardedId;
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
