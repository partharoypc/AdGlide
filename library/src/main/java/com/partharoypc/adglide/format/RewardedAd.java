package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;

import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import com.partharoypc.adglide.AdGlideNetwork;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
import com.partharoypc.adglide.util.OnRewardedAdLoadedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

public class RewardedAd {

    @SuppressWarnings("deprecation")
    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private com.google.android.gms.ads.rewarded.RewardedAd adMobRewardedAd;
        private com.facebook.ads.RewardedVideoAd metaRewardedVideoAd;
        private MaxRewardedAd appLovinMaxRewardedAd;
        private StartAppAd startAppRewardedAd;
        private com.wortise.ads.rewarded.RewardedAd wortiseRewardedAd;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobRewardedId = "";
        private String metaRewardedId = "";
        private String unityRewardedId = "";
        private String appLovinMaxRewardedId = "";
        private String applovinDiscRewardedZoneId = "";
        private String ironSourceRewardedId = "";
        private String wortiseRewardedId = "";
        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        /**
         * Initializes the RewardedAd Builder.
         * 
         * @param activity The Activity context.
         */
        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(null, onComplete, onDismiss, onError);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder show(@NonNull Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(displayActivity, onComplete, onDismiss, onError);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadRewardedAd(null, null);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadAndShowRewardedAd(onLoaded, onError, onDismiss, onComplete);
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
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
        public Builder backups(@Nullable String... backupAdNetworks) {
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
        public Builder adMobId(@NonNull String adMobRewardedId) {
            this.adMobRewardedId = adMobRewardedId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaRewardedId) {
            this.metaRewardedId = metaRewardedId;
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String unityRewardedId) {
            this.unityRewardedId = unityRewardedId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinMaxRewardedId) {
            this.appLovinMaxRewardedId = appLovinMaxRewardedId;
            return this;
        }

        /**
         * Sets the ApplovinDiscRewardedZone Ad Unit ID.
         * 
         * @param applovinDiscRewardedZoneId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder zoneId(@NonNull String applovinDiscRewardedZoneId) {
            this.applovinDiscRewardedZoneId = applovinDiscRewardedZoneId;
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String ironSourceRewardedId) {
            this.ironSourceRewardedId = ironSourceRewardedId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseRewardedId) {
            this.wortiseRewardedId = wortiseRewardedId;
            return this;
        }

        /**
         * Sets the placement status.
         * 
         * @param placementStatus Integer representing status.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        /**
         * Toggles legacy GDPR compliance extras.
         * 
         * @param legacyGDPR True to enable.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Rewarded Ad load.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    Log.d(TAG, "Rewarded Ad is enabled");
                    loadAdFromNetwork(adNetwork, onComplete, onDismiss);
                } else {
                    Log.d(TAG, "Rewarded Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Rewarded Ad: " + e.getMessage());
            }
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Backup Rewarded Ad load.");
                        return;
                    }
                    if (waterfallManager == null) {
                        if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup rewarded ads failed to load");
                        return;
                    }

                    backupAdNetwork = networkToLoad;
                    Log.d(TAG, "[" + networkToLoad + "] is selected as Backup Ads");
                    loadAdFromNetwork(backupAdNetwork, onComplete, onDismiss);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Rewarded Ad: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                switch (networkToLoad) {
                    case ADMOB:
                    case META_BIDDING_ADMOB: {
                        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobRewardedId)) {
                            loadRewardedBackupAd(onComplete, onDismiss);
                            break;
                        }
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adMobRewardedId);
                                        adMobRewardedAd = ad;
                                        adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                adMobRewardedAd = null;
                                                loadRewardedAd(onComplete, onDismiss);
                                                if (onDismiss != null) {
                                                    onDismiss.onRewardedAdDismissed();
                                                }
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                                adMobRewardedAd = null;
                                            }
                                        });
                                        Log.d(TAG, "[ADMOB] Rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                        if (loadAdError
                                                .getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                            com.partharoypc.adglide.util.AdMobRateLimiter
                                                    .recordFailure(adMobRewardedId);
                                        }
                                        adMobRewardedAd = null;
                                        loadRewardedBackupAd(onComplete, onDismiss);
                                        Log.d(TAG, "[ADMOB] Failed to load rewarded ad: " + loadAdError.getMessage());
                                    }
                                });
                        break;
                    }

                    case META: {
                        metaRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, metaRewardedId);
                        metaRewardedVideoAd.loadAd(metaRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        if (onComplete != null) {
                                            onComplete.onRewardedAdComplete();
                                        }
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        if (onDismiss != null) {
                                            onDismiss.onRewardedAdDismissed();
                                        }
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        loadRewardedBackupAd(onComplete, onDismiss);
                                        Log.d(TAG, "[Meta] Failed to load rewarded ad: " + adError.getErrorMessage());
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        Log.d(TAG, "[Meta] Rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {
                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {
                                    }
                                }).build());
                        break;
                    }

                    case UNITY: {
                        UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "[UNITY] Rewarded ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[UNITY] Failed to load rewarded ad: " + message);
                            }
                        });
                        break;
                    }

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case META_BIDDING_APPLOVIN_MAX: {
                        appLovinMaxRewardedAd = MaxRewardedAd.getInstance(appLovinMaxRewardedId, activity);
                        appLovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                if (onComplete != null) {
                                    onComplete.onRewardedAdComplete();
                                }
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "[APPLOVIN MAX] Rewarded ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                loadRewardedAd(onComplete, onDismiss);
                                if (onDismiss != null) {
                                    onDismiss.onRewardedAdDismissed();
                                }
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, com.applovin.mediation.MaxError error) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[APPLOVIN MAX] Failed to load rewarded ad: " + error.getMessage());
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, com.applovin.mediation.MaxError error) {
                            }
                        });
                        appLovinMaxRewardedAd.loadAd();
                        break;
                    }

                    case STARTAPP: {
                        startAppRewardedAd = new StartAppAd(activity);
                        startAppRewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "[STARTAPP] Rewarded ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[STARTAPP] Failed to load rewarded ad");
                            }
                        });
                        break;
                    }

                    case IRONSOURCE:
                    case META_BIDDING_IRONSOURCE: {
                        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                            @Override
                            public void onAdOpened(AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(AdInfo adInfo) {
                                if (onDismiss != null) {
                                    onDismiss.onRewardedAdDismissed();
                                }
                            }

                            @Override
                            public void onAdAvailable(AdInfo adInfo) {
                                Log.d(TAG, "[IRONSOURCE] Rewarded ad available");
                            }

                            @Override
                            public void onAdUnavailable() {
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                            }

                            @Override
                            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                                if (onComplete != null) {
                                    onComplete.onRewardedAdComplete();
                                }
                            }

                            @Override
                            public void onAdClicked(Placement placement, AdInfo adInfo) {
                            }
                        });
                        IronSource.loadRewardedVideo();
                        break;
                    }

                    default:
                        loadRewardedBackupAd(onComplete, onDismiss);
                        break;
                }
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load rewarded ad for " + networkToLoad + ". Error: " + e.getMessage());
                loadRewardedBackupAd(onComplete, onDismiss);
            }
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(null, onComplete, onDismiss, onError);
        }

        public void showRewardedAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            if (adMobRewardedAd != null) {
                                adMobRewardedAd.show(targetActivity, rewardItem -> {
                                    onComplete.onRewardedAdComplete();
                                    Log.d(TAG, "The user earned the reward.");
                                });
                            } else {
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case META: {
                            if (metaRewardedVideoAd != null && metaRewardedVideoAd.isAdLoaded()) {
                                metaRewardedVideoAd.show();
                            } else {
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.show(targetActivity, unityRewardedId, new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowComplete(String placementId,
                                        UnityAds.UnityAdsShowCompletionState state) {
                                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                        onComplete.onRewardedAdComplete();
                                    }
                                    if (onDismiss != null) {
                                        onDismiss.onRewardedAdDismissed();
                                    }
                                }

                                @Override
                                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error,
                                        String message) {
                                    showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                                }

                                @Override
                                public void onUnityAdsShowStart(String placementId) {
                                }

                                @Override
                                public void onUnityAdsShowClick(String placementId) {
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            if (appLovinMaxRewardedAd != null && appLovinMaxRewardedAd.isReady()) {
                                appLovinMaxRewardedAd.showAd();
                            } else {
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case STARTAPP: {
                            try {
                                if (startAppRewardedAd != null && startAppRewardedAd.isReady()) {
                                    startAppRewardedAd.showAd(new AdDisplayListener() {
                                        @Override
                                        public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
                                            if (onDismiss != null) {
                                                onDismiss.onRewardedAdDismissed();
                                            }
                                        }

                                        @Override
                                        public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                        }

                                        @Override
                                        public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
                                        }

                                        @Override
                                        public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                            showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                                        }
                                    });
                                } else {
                                    showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                                }
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup rewarded for StartApp. Error: " + e.getMessage());
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            try {
                                if (IronSource.isRewardedVideoAvailable()) {
                                    IronSource.showRewardedVideo();
                                } else {
                                    showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                                }
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup rewarded for IronSource. Error: " + e.getMessage());
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case WORTISE: {
                            try {
                                if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                                    wortiseRewardedAd.showAd();
                                } else {
                                    showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                                }
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup rewarded for Wortise. Error: " + e.getMessage());
                                showRewardedBackupAd(targetActivity, onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showRewardedAd: " + e.getMessage());
            }
        }

        public void showRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            showRewardedBackupAd(null, onComplete, onDismiss, onError);
        }

        public void showRewardedBackupAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    Log.d(TAG, "Show Backup Rewarded Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            if (adMobRewardedAd != null) {
                                adMobRewardedAd.show(targetActivity, rewardItem -> {
                                    onComplete.onRewardedAdComplete();
                                });
                            }
                            break;
                        }

                        case META: {
                            if (metaRewardedVideoAd != null && metaRewardedVideoAd.isAdLoaded()) {
                                metaRewardedVideoAd.show();
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.show(targetActivity, unityRewardedId);
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            if (appLovinMaxRewardedAd != null && appLovinMaxRewardedAd.isReady()) {
                                appLovinMaxRewardedAd.showAd();
                            }
                            break;
                        }

                        case STARTAPP: {
                            if (startAppRewardedAd != null && startAppRewardedAd.isReady()) {
                                startAppRewardedAd.showAd();
                            }
                            break;
                        }

                        case WORTISE: {
                            if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                                wortiseRewardedAd.showAd();
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showRewardedBackupAd: " + e.getMessage());
            }
        }

        public void loadAndShowRewardedAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Rewarded Ad load and show.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                            adMobRewardedAd = ad;
                                            onLoaded.onRewardedAdLoaded();
                                            adMobRewardedAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            adMobRewardedAd = null;
                                                            onDismiss.onRewardedAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull com.google.android.gms.ads.AdError adError) {
                                                            adMobRewardedAd = null;
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            adMobRewardedAd = null;
                                            loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        }
                                    });
                            break;
                        }

                        case META: {
                            metaRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, metaRewardedId);
                            metaRewardedVideoAd.loadAd(metaRewardedVideoAd.buildLoadAdConfig()
                                    .withAdListener(new RewardedVideoAdListener() {
                                        @Override
                                        public void onRewardedVideoCompleted() {
                                            onComplete.onRewardedAdComplete();
                                        }

                                        @Override
                                        public void onRewardedVideoClosed() {
                                            onDismiss.onRewardedAdDismissed();
                                        }

                                        @Override
                                        public void onError(Ad ad, AdError adError) {
                                            loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            onLoaded.onRewardedAdLoaded();
                                        }

                                        @Override
                                        public void onAdClicked(Ad ad) {
                                        }

                                        @Override
                                        public void onLoggingImpression(Ad ad) {
                                        }
                                    }).build());
                            break;
                        }

                        case UNITY: {
                            UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    onLoaded.onRewardedAdLoaded();
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            appLovinMaxRewardedAd = MaxRewardedAd.getInstance(appLovinMaxRewardedId, activity);
                            appLovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
                                @Override
                                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                    onComplete.onRewardedAdComplete();
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    onLoaded.onRewardedAdLoaded();
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    onDismiss.onRewardedAdDismissed();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, com.applovin.mediation.MaxError error) {
                                    loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, com.applovin.mediation.MaxError error) {
                                }
                            });
                            appLovinMaxRewardedAd.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAndShowRewardedAd: " + e.getMessage());
            }
        }

        public void loadAndShowRewardedBackupAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Backup Rewarded Ad load and show.");
                        return;
                    }
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }
                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null)
                        return;
                    backupAdNetwork = networkToLoad;

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                            adMobRewardedAd = ad;
                                            onLoaded.onRewardedAdLoaded();
                                            adMobRewardedAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            adMobRewardedAd = null;
                                                            onDismiss.onRewardedAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull com.google.android.gms.ads.AdError adError) {
                                                            adMobRewardedAd = null;
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            adMobRewardedAd = null;
                                            loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        }
                                    });
                            break;
                        }

                        case META: {
                            metaRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, metaRewardedId);
                            metaRewardedVideoAd.loadAd(metaRewardedVideoAd.buildLoadAdConfig()
                                    .withAdListener(new RewardedVideoAdListener() {
                                        @Override
                                        public void onRewardedVideoCompleted() {
                                            onComplete.onRewardedAdComplete();
                                        }

                                        @Override
                                        public void onRewardedVideoClosed() {
                                            onDismiss.onRewardedAdDismissed();
                                        }

                                        @Override
                                        public void onError(Ad ad, AdError adError) {
                                            loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            onLoaded.onRewardedAdLoaded();
                                        }

                                        @Override
                                        public void onAdClicked(Ad ad) {
                                        }

                                        @Override
                                        public void onLoggingImpression(Ad ad) {
                                        }
                                    }).build());
                            break;
                        }

                        case UNITY: {
                            UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    onLoaded.onRewardedAdLoaded();
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            appLovinMaxRewardedAd = MaxRewardedAd.getInstance(appLovinMaxRewardedId, activity);
                            appLovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
                                @Override
                                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                    onComplete.onRewardedAdComplete();
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    onLoaded.onRewardedAdLoaded();
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    onDismiss.onRewardedAdDismissed();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, com.applovin.mediation.MaxError error) {
                                    loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, com.applovin.mediation.MaxError error) {
                                }
                            });
                            appLovinMaxRewardedAd.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadAndShowRewardedBackupAd: " + e.getMessage());
            }
        }

        public void destroyRewardedAd() {
            if (adMobRewardedAd != null) {
                adMobRewardedAd = null;
            }
            if (metaRewardedVideoAd != null) {
                metaRewardedVideoAd.destroy();
                metaRewardedVideoAd = null;
            }
            if (appLovinMaxRewardedAd != null) {
                appLovinMaxRewardedAd.destroy();
                appLovinMaxRewardedAd = null;
            }
            if (wortiseRewardedAd != null) {
                wortiseRewardedAd.destroy();
                wortiseRewardedAd = null;
            }
            if (startAppRewardedAd != null) {
                startAppRewardedAd = null;
            }
        }
    }
}
