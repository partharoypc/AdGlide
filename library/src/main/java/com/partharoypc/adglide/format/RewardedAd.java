package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

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

/**
 * Handles loading and displaying rewarded ads from multiple ad networks.
 * Uses a Builder pattern for configuration with primary and backup network
 * support.
 */
public class RewardedAd {

    @SuppressWarnings("deprecation")
    public static class Builder {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private com.google.android.gms.ads.rewarded.RewardedAd adMobRewardedAd;
        private com.facebook.ads.RewardedVideoAd fanRewardedVideoAd;
        private MaxRewardedAd applovinMaxRewardedAd;
        private StartAppAd startAppRewardedAd;
        private com.wortise.ads.rewarded.RewardedAd wortiseRewardedAd;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobRewardedId = "";
        private String fanRewardedId = "";
        private String unityRewardedId = "";
        private String applovinMaxRewardedId = "";
        private String applovinDiscRewardedZoneId = "";
        private String ironSourceRewardedId = "";
        private String wortiseRewardedId = "";
        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        public Builder show(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(onComplete, onDismiss, onError);
            return this;
        }

        public Builder build(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadAndShowRewardedAd(onLoaded, onError, onDismiss, onComplete);
            return this;
        }

        public Builder setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Builder setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Builder setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        public Builder setBackupAdNetworks(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        /** @deprecated Use {@link #setAdNetwork(String)} instead. */
        @Deprecated
        public Builder setMainAds(String mainAds) {
            this.adNetwork = mainAds;
            return this;
        }

        /** @deprecated Use {@link #setBackupAdNetwork(String)} instead. */
        @Deprecated
        public Builder setBackupAds(String backupAds) {
            this.backupAdNetwork = backupAds;
            this.waterfallManager = new WaterfallManager(backupAds);
            return this;
        }

        public Builder setAdMobRewardedId(String adMobRewardedId) {
            this.adMobRewardedId = adMobRewardedId;
            return this;
        }

        public Builder setFanRewardedId(String fanRewardedId) {
            this.fanRewardedId = fanRewardedId;
            return this;
        }

        public Builder setUnityRewardedId(String unityRewardedId) {
            this.unityRewardedId = unityRewardedId;
            return this;
        }

        public Builder setApplovinMaxRewardedId(String applovinMaxRewardedId) {
            this.applovinMaxRewardedId = applovinMaxRewardedId;
            return this;
        }

        public Builder setApplovinDiscRewardedZoneId(String applovinDiscRewardedZoneId) {
            this.applovinDiscRewardedZoneId = applovinDiscRewardedZoneId;
            return this;
        }

        public Builder setIronSourceRewardedId(String ironSourceRewardedId) {
            this.ironSourceRewardedId = ironSourceRewardedId;
            return this;
        }

        public Builder setWortiseRewardedId(String wortiseRewardedId) {
            this.wortiseRewardedId = wortiseRewardedId;
            return this;
        }

        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                            adMobRewardedAd = ad;
                                            adMobRewardedAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            adMobRewardedAd = null;
                                                            loadRewardedAd(onComplete, onDismiss);
                                                            onDismiss.onRewardedAdDismissed();
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
                                            adMobRewardedAd = null;
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                            Log.d(TAG,
                                                    "[ADMOB] Failed to load rewarded ad: " + loadAdError.getMessage());
                                        }
                                    });
                            break;
                        }

                        case FAN:
                        case FACEBOOK: {
                            fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                            fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
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
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                            Log.d(TAG,
                                                    "[FAN] Failed to load rewarded ad: " + adError.getErrorMessage());
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            Log.d(TAG, "[FAN] Rewarded ad loaded");
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
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                            applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
                                @Override
                                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                    onComplete.onRewardedAdComplete();
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
                                    onDismiss.onRewardedAdDismissed();
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
                            applovinMaxRewardedAd.loadAd();
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
                        case FAN_BIDDING_IRONSOURCE: {
                            IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                                @Override
                                public void onAdOpened(AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClosed(AdInfo adInfo) {
                                    onDismiss.onRewardedAdDismissed();
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
                                    onComplete.onRewardedAdComplete();
                                }

                                @Override
                                public void onAdClicked(Placement placement, AdInfo adInfo) {
                                }
                            });
                            IronSource.loadRewardedVideo();
                            break;
                        }

                        case WORTISE: {
                            wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                            wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                                @Override
                                public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                        @NonNull com.wortise.ads.AdError error) {
                                    loadRewardedBackupAd(onComplete, onDismiss);
                                    Log.d(TAG, "[WORTISE] Failed to load rewarded ad");
                                }

                                @Override
                                public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                        @NonNull com.wortise.ads.AdError error) {
                                }

                                @Override
                                public void onRewardedImpression(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                }

                                @Override
                                public void onRewardedClicked(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                }

                                @Override
                                public void onRewardedCompleted(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                        @NonNull com.wortise.ads.rewarded.models.Reward reward) {
                                    onComplete.onRewardedAdComplete();
                                }

                                @Override
                                public void onRewardedDismissed(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                    onDismiss.onRewardedAdDismissed();
                                }

                                @Override
                                public void onRewardedLoaded(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                    Log.d(TAG, "[WORTISE] Rewarded ad loaded");
                                }

                                @Override
                                public void onRewardedShown(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                }

                                @Override
                                public void onRewardedRevenuePaid(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                        @NonNull com.wortise.ads.RevenueData revenueData) {
                                }
                            });
                            wortiseRewardedAd.loadAd();
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedAd: " + e.getMessage());
            }
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
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
                    Log.d(TAG, "Loading Backup Rewarded Ad [" + backupAdNetwork.toUpperCase() + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                    Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                            adMobRewardedAd = ad;
                                            adMobRewardedAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            adMobRewardedAd = null;
                                                            loadRewardedAd(onComplete, onDismiss);
                                                            onDismiss.onRewardedAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull com.google.android.gms.ads.AdError adError) {
                                                            adMobRewardedAd = null;
                                                        }
                                                    });
                                            Log.d(TAG, "[ADMOB] [backup] Rewarded ad loaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            adMobRewardedAd = null;
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                            Log.d(TAG, "[ADMOB] [backup] Failed to load rewarded ad");
                                        }
                                    });
                            break;
                        }

                        case FAN:
                        case FACEBOOK: {
                            fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                            fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
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
                                            loadRewardedBackupAd(onComplete, onDismiss);
                                            Log.d(TAG, "[FAN] [backup] Failed to load rewarded ad");
                                        }

                                        @Override
                                        public void onAdLoaded(Ad ad) {
                                            Log.d(TAG, "[FAN] [backup] Rewarded ad loaded");
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
                                    Log.d(TAG, "[UNITY] [backup] Rewarded ad loaded");
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadRewardedBackupAd(onComplete, onDismiss);
                                    Log.d(TAG, "[UNITY] [backup] Failed to load rewarded ad");
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                            applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
                                @Override
                                public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                    onComplete.onRewardedAdComplete();
                                }

                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Log.d(TAG, "[APPLOVIN MAX] [backup] Rewarded ad loaded");
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    loadRewardedAd(onComplete, onDismiss);
                                    onDismiss.onRewardedAdDismissed();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, com.applovin.mediation.MaxError error) {
                                    loadRewardedBackupAd(onComplete, onDismiss);
                                    Log.d(TAG, "[APPLOVIN MAX] [backup] Failed to load rewarded ad");
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, com.applovin.mediation.MaxError error) {
                                }
                            });
                            applovinMaxRewardedAd.loadAd();
                            break;
                        }

                        default:
                            loadRewardedBackupAd(onComplete, onDismiss);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedBackupAd: " + e.getMessage());
            }
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            if (adMobRewardedAd != null) {
                                adMobRewardedAd.show(activity, rewardItem -> {
                                    onComplete.onRewardedAdComplete();
                                    Log.d(TAG, "The user earned the reward.");
                                });
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case FAN:
                        case FACEBOOK: {
                            if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                                fanRewardedVideoAd.show();
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.show(activity, unityRewardedId, new IUnityAdsShowListener() {
                                @Override
                                public void onUnityAdsShowComplete(String placementId,
                                        UnityAds.UnityAdsShowCompletionState state) {
                                    if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                                        onComplete.onRewardedAdComplete();
                                    }
                                    onDismiss.onRewardedAdDismissed();
                                }

                                @Override
                                public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error,
                                        String message) {
                                    showRewardedBackupAd(onComplete, onDismiss, onError);
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
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            if (applovinMaxRewardedAd != null && applovinMaxRewardedAd.isReady()) {
                                applovinMaxRewardedAd.showAd();
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case STARTAPP: {
                            if (startAppRewardedAd != null && startAppRewardedAd.isReady()) {
                                startAppRewardedAd.showAd(new AdDisplayListener() {
                                    @Override
                                    public void adHidden(com.startapp.sdk.adsbase.Ad ad) {
                                        onDismiss.onRewardedAdDismissed();
                                    }

                                    @Override
                                    public void adDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                    }

                                    @Override
                                    public void adClicked(com.startapp.sdk.adsbase.Ad ad) {
                                    }

                                    @Override
                                    public void adNotDisplayed(com.startapp.sdk.adsbase.Ad ad) {
                                        showRewardedBackupAd(onComplete, onDismiss, onError);
                                    }
                                });
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case IRONSOURCE:
                        case FAN_BIDDING_IRONSOURCE: {
                            if (IronSource.isRewardedVideoAvailable()) {
                                IronSource.showRewardedVideo();
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
                            }
                            break;
                        }

                        case WORTISE: {
                            if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                                wortiseRewardedAd.showAd();
                            } else {
                                showRewardedBackupAd(onComplete, onDismiss, onError);
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
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    Log.d(TAG, "Show Backup Rewarded Ad [" + backupAdNetwork.toUpperCase() + "]");
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
                            if (adMobRewardedAd != null) {
                                adMobRewardedAd.show(activity, rewardItem -> {
                                    onComplete.onRewardedAdComplete();
                                });
                            }
                            break;
                        }

                        case FAN:
                        case FACEBOOK: {
                            if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                                fanRewardedVideoAd.show();
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.show(activity, unityRewardedId);
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            if (applovinMaxRewardedAd != null && applovinMaxRewardedAd.isReady()) {
                                applovinMaxRewardedAd.showAd();
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
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case FAN_BIDDING_ADMOB: {
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

                        case FAN:
                        case FACEBOOK: {
                            fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                            fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
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
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                            applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
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
                            applovinMaxRewardedAd.loadAd();
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
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
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
                        case FAN_BIDDING_ADMOB: {
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

                        case FAN:
                        case FACEBOOK: {
                            fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                            fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
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
                        case FAN_BIDDING_APPLOVIN_MAX: {
                            applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                            applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {
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
                            applovinMaxRewardedAd.loadAd();
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
            if (fanRewardedVideoAd != null) {
                fanRewardedVideoAd.destroy();
                fanRewardedVideoAd = null;
            }
            if (applovinMaxRewardedAd != null) {
                applovinMaxRewardedAd.destroy();
                applovinMaxRewardedAd = null;
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
