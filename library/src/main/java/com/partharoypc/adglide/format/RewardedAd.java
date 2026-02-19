package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FACEBOOK;
import static com.partharoypc.adglide.util.Constant.FAN;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_AD_MANAGER;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.FAN_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.GOOGLE_AD_MANAGER;
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
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
// import com.wortise.ads.rewarded.RewardedAd;

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
        private com.google.android.gms.ads.rewarded.RewardedAd adManagerRewardedAd;
        private com.facebook.ads.RewardedVideoAd fanRewardedVideoAd;
        private MaxRewardedAd applovinMaxRewardedAd;
        private StartAppAd startAppRewardedAd;
        private com.wortise.ads.rewarded.RewardedAd wortiseRewardedAd;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobRewardedId = "";
        private String adManagerRewardedId = "";
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
            return this;
        }

        public Builder setAdMobRewardedId(String adMobRewardedId) {
            this.adMobRewardedId = adMobRewardedId;
            return this;
        }

        public Builder setAdManagerRewardedId(String adManagerRewardedId) {
            this.adManagerRewardedId = adManagerRewardedId;
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
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adMobRewardedAd = ad;
                                        adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                adMobRewardedAd = null;
                                                loadRewardedAd(onComplete, onDismiss);
                                                onDismiss.onRewardedAdDismissed();
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                adMobRewardedAd = null;
                                            }
                                        });
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adMobRewardedAd = null;
                                        loadRewardedBackupAd(onComplete, onDismiss);
                                        Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad: "
                                                + loadAdError.getMessage() + ", try to load backup ad: "
                                                + backupAdNetwork);
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adManagerRewardedId,
                                Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adManagerRewardedAd = ad;
                                        adManagerRewardedAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        adManagerRewardedAd = null;
                                                        loadRewardedAd(onComplete, onDismiss);
                                                        onDismiss.onRewardedAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        adManagerRewardedAd = null;
                                                    }
                                                });
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adManagerRewardedAd = null;
                                        loadRewardedBackupAd(onComplete, onDismiss);
                                        Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad: "
                                                + loadAdError.getMessage() + ", try to load backup ad: "
                                                + backupAdNetwork);
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        loadRewardedAd(onComplete, onDismiss);
                                        onDismiss.onRewardedAdDismissed();
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        loadRewardedBackupAd(onComplete, onDismiss);
                                        Log.d(TAG,
                                                "[" + adNetwork + "] " + "failed to load rewarded ad: " + fanRewardedId
                                                        + ", try to load backup ad: " + backupAdNetwork);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case UNITY:
                        UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad, try backup: "
                                        + backupAdNetwork);
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                        applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {

                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad complete");
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                applovinMaxRewardedAd.loadAd();
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad, try backup: "
                                        + backupAdNetwork);
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        applovinMaxRewardedAd.loadAd();
                        break;

                    case STARTAPP:
                        startAppRewardedAd = new StartAppAd(activity);
                        startAppRewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad, try backup: "
                                        + backupAdNetwork);
                            }
                        });
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                            @Override
                            public void onAdOpened(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdAvailable(
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onAdUnavailable() {
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdRewarded(com.ironsource.mediationsdk.model.Placement placement,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad complete");
                            }

                            @Override
                            public void onAdClicked(com.ironsource.mediationsdk.model.Placement placement,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }
                        });
                        IronSource.loadRewardedVideo();
                        break;

                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                loadRewardedBackupAd(onComplete, onDismiss);
                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                loadRewardedBackupAd(onComplete, onDismiss);
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
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad complete");
                            }

                            @Override
                            public void onRewardedDismissed(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                wortiseRewardedAd.loadAd();
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onRewardedLoaded(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
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

                    case NONE:
                        break;

                    default:
                        break;
                }
            }
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adMobRewardedAd = ad;
                                        adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                adMobRewardedAd = null;
                                                loadRewardedAd(onComplete, onDismiss);
                                                onDismiss.onRewardedAdDismissed();
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                adMobRewardedAd = null;
                                            }
                                        });
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adMobRewardedAd = null;
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + loadAdError.getMessage());
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adManagerRewardedId,
                                Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adManagerRewardedAd = ad;
                                        adManagerRewardedAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        adManagerRewardedAd = null;
                                                        loadRewardedAd(onComplete, onDismiss);
                                                        onDismiss.onRewardedAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        adManagerRewardedAd = null;
                                                    }
                                                });
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adManagerRewardedAd = null;
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + loadAdError.getMessage());
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        loadRewardedAd(onComplete, onDismiss);
                                        onDismiss.onRewardedAdDismissed();
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + fanRewardedId);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case UNITY:
                        UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                        applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {

                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                onComplete.onRewardedAdComplete();
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onAdDisplayed(MaxAd ad) {
                            }

                            @Override
                            public void onAdHidden(MaxAd ad) {
                                applovinMaxRewardedAd.loadAd();
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdClicked(MaxAd ad) {
                            }

                            @Override
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                            }
                        });
                        applovinMaxRewardedAd.loadAd();
                        break;

                    case STARTAPP:
                        startAppRewardedAd = new StartAppAd(activity);
                        startAppRewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                            @Override
                            public void onAdOpened(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdAvailable(
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onAdUnavailable() {
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                                onComplete.onRewardedAdComplete();
                            }

                            @Override
                            public void onAdClicked(Placement placement, AdInfo adInfo) {
                            }
                        });
                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                        + error.getMessage());
                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to show rewarded ad: "
                                        + error.getMessage());
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
                                wortiseRewardedAd.loadAd();
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onRewardedLoaded(@NonNull com.wortise.ads.rewarded.RewardedAd ad) {
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
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

                    case NONE:
                        break;

                    default:
                        break;

                }
            }
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobRewardedAd != null) {
                            adMobRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerRewardedAd != null) {
                            adManagerRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                            fanRewardedVideoAd.show();
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    case UNITY:
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

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinMaxRewardedAd != null && applovinMaxRewardedAd.isReady()) {
                            applovinMaxRewardedAd.showAd();
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    case STARTAPP:
                        if (startAppRewardedAd != null) {
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

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        if (IronSource.isRewardedVideoAvailable()) {
                            IronSource.showRewardedVideo(ironSourceRewardedId);
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    case WORTISE:
                        if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                            wortiseRewardedAd.showAd();
                        } else {
                            showRewardedBackupAd(onComplete, onDismiss, onError);
                        }
                        break;

                    default:
                        onError.onRewardedAdError();
                        break;
                }
            }

        }

        public void showRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        if (adMobRewardedAd != null) {
                            adMobRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        if (adManagerRewardedAd != null) {
                            adManagerRewardedAd.show(activity, rewardItem -> {
                                onComplete.onRewardedAdComplete();
                                Log.d(TAG, "The user earned the reward.");
                            });
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null && fanRewardedVideoAd.isAdLoaded()) {
                            fanRewardedVideoAd.show();
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case UNITY:
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
                                onError.onRewardedAdError();
                            }

                            @Override
                            public void onUnityAdsShowStart(String placementId) {
                            }

                            @Override
                            public void onUnityAdsShowClick(String placementId) {
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinMaxRewardedAd != null && applovinMaxRewardedAd.isReady()) {
                            applovinMaxRewardedAd.showAd();
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case STARTAPP:
                        if (startAppRewardedAd != null) {
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
                                    onError.onRewardedAdError();
                                }
                            });
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        if (IronSource.isRewardedVideoAvailable()) {
                            IronSource.showRewardedVideo(ironSourceRewardedId);
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    case WORTISE:
                        if (wortiseRewardedAd != null && wortiseRewardedAd.isAvailable()) {
                            wortiseRewardedAd.showAd();
                        } else {
                            onError.onRewardedAdError();
                        }
                        break;

                    default:
                        onError.onRewardedAdError();
                        break;
                }
            }

        }

        public void loadAndShowRewardedAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adMobRewardedAd = ad;
                                        onLoaded.onRewardedAdLoaded();

                                        adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                adMobRewardedAd = null;
                                                onDismiss.onRewardedAdDismissed();
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                adMobRewardedAd = null;
                                            }
                                        });

                                        adMobRewardedAd.show(activity, rewardItem -> {
                                            onComplete.onRewardedAdComplete();
                                            Log.d(TAG, "The user earned the reward.");
                                        });
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adMobRewardedAd = null;
                                        loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad: "
                                                + loadAdError.getMessage() + ", try to load backup ad: "
                                                + backupAdNetwork);
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adManagerRewardedId,
                                Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adManagerRewardedAd = ad;
                                        onLoaded.onRewardedAdLoaded();

                                        adManagerRewardedAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        adManagerRewardedAd = null;
                                                        onDismiss.onRewardedAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        adManagerRewardedAd = null;
                                                    }
                                                });

                                        adManagerRewardedAd.show(activity, rewardItem -> {
                                            onComplete.onRewardedAdComplete();
                                            Log.d(TAG, "The user earned the reward.");
                                        });
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adManagerRewardedAd = null;
                                        loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad: "
                                                + loadAdError.getMessage() + ", try to load backup ad: "
                                                + backupAdNetwork);
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        onDismiss.onRewardedAdDismissed();
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                        Log.d(TAG,
                                                "[" + adNetwork + "] " + "failed to load rewarded ad: " + fanRewardedId
                                                        + ", try to load backup ad: " + backupAdNetwork);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        fanRewardedVideoAd.show();
                                        onLoaded.onRewardedAdLoaded();
                                        Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case UNITY:
                        UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                onLoaded.onRewardedAdLoaded();
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
                                    public void onUnityAdsShowFailure(String placementId,
                                            UnityAds.UnityAdsShowError error, String message) {
                                        onError.onRewardedAdError();
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                    }
                                });
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                        applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {

                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                onComplete.onRewardedAdComplete();
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                onLoaded.onRewardedAdLoaded();
                                applovinMaxRewardedAd.showAd();
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
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
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad");
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                onError.onRewardedAdError();
                            }
                        });
                        applovinMaxRewardedAd.loadAd();
                        break;

                    case STARTAPP:
                        startAppRewardedAd = new StartAppAd(activity);
                        startAppRewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                onLoaded.onRewardedAdLoaded();
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
                                        onError.onRewardedAdError();
                                    }
                                });
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                                Log.d(TAG, "[" + adNetwork + "] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                            @Override
                            public void onAdOpened(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdAvailable(
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onLoaded.onRewardedAdLoaded();
                                IronSource.showRewardedVideo(ironSourceRewardedId);
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onAdUnavailable() {
                                onError.onRewardedAdError();
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onError.onRewardedAdError();
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

                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                loadAndShowRewardedBackupAd(onLoaded, onError, onDismiss, onComplete);
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
                                onLoaded.onRewardedAdLoaded();
                                wortiseRewardedAd.showAd();
                                Log.d(TAG, "[" + adNetwork + "] " + "rewarded ad loaded");
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

                    case NONE:
                        break;

                    default:
                        break;
                }
            }
        }

        public void loadAndShowRewardedBackupAd(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adMobRewardedId,
                                Tools.getAdRequest(activity, legacyGDPR), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adMobRewardedAd = ad;
                                        onLoaded.onRewardedAdLoaded();

                                        adMobRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                adMobRewardedAd = null;
                                                onDismiss.onRewardedAdDismissed();
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(
                                                    @NonNull com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                adMobRewardedAd = null;
                                            }
                                        });

                                        adMobRewardedAd.show(activity, rewardItem -> {
                                            onComplete.onRewardedAdComplete();
                                            Log.d(TAG, "The user earned the reward.");
                                        });
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adMobRewardedAd = null;
                                        onError.onRewardedAdError();
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + loadAdError.getMessage());
                                    }
                                });
                        break;

                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        com.google.android.gms.ads.rewarded.RewardedAd.load(activity, adManagerRewardedId,
                                Tools.getGoogleAdManagerRequest(), new RewardedAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull com.google.android.gms.ads.rewarded.RewardedAd ad) {
                                        adManagerRewardedAd = ad;
                                        onLoaded.onRewardedAdLoaded();

                                        adManagerRewardedAd
                                                .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        adManagerRewardedAd = null;
                                                        onDismiss.onRewardedAdDismissed();
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(
                                                            @NonNull com.google.android.gms.ads.AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        adManagerRewardedAd = null;
                                                    }
                                                });

                                        adManagerRewardedAd.show(activity, rewardItem -> {
                                            onComplete.onRewardedAdComplete();
                                            Log.d(TAG, "The user earned the reward.");
                                        });
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        Log.d(TAG, loadAdError.toString());
                                        adManagerRewardedAd = null;
                                        onError.onRewardedAdError();
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + loadAdError.getMessage());
                                    }
                                });
                        break;

                    case FAN:
                    case FACEBOOK:
                        fanRewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, fanRewardedId);
                        fanRewardedVideoAd.loadAd(fanRewardedVideoAd.buildLoadAdConfig()
                                .withAdListener(new RewardedVideoAdListener() {
                                    @Override
                                    public void onRewardedVideoCompleted() {
                                        onComplete.onRewardedAdComplete();
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad complete");
                                    }

                                    @Override
                                    public void onRewardedVideoClosed() {
                                        onDismiss.onRewardedAdDismissed();
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad closed");
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        onError.onRewardedAdError();
                                        Log.d(TAG,
                                                "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad: "
                                                        + fanRewardedId);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        fanRewardedVideoAd.show();
                                        onLoaded.onRewardedAdLoaded();
                                        Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {

                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {

                                    }
                                })
                                .build());
                        break;

                    case UNITY:
                        UnityAds.load(unityRewardedId, new IUnityAdsLoadListener() {
                            @Override
                            public void onUnityAdsAdLoaded(String placementId) {
                                onLoaded.onRewardedAdLoaded();
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
                                    public void onUnityAdsShowFailure(String placementId,
                                            UnityAds.UnityAdsShowError error, String message) {
                                        onError.onRewardedAdError();
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                    }
                                });
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                    String message) {
                                onError.onRewardedAdError();
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        applovinMaxRewardedAd = MaxRewardedAd.getInstance(applovinMaxRewardedId, activity);
                        applovinMaxRewardedAd.setListener(new MaxRewardedAdListener() {

                            @Override
                            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                                onComplete.onRewardedAdComplete();
                            }

                            @Override
                            public void onAdLoaded(MaxAd ad) {
                                onLoaded.onRewardedAdLoaded();
                                applovinMaxRewardedAd.showAd();
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
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
                            public void onAdLoadFailed(String adUnitId, MaxError error) {
                                onError.onRewardedAdError();
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }

                            @Override
                            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                onError.onRewardedAdError();
                            }
                        });
                        applovinMaxRewardedAd.loadAd();
                        break;

                    case STARTAPP:
                        startAppRewardedAd = new StartAppAd(activity);
                        startAppRewardedAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
                            @Override
                            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                onLoaded.onRewardedAdLoaded();
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
                                        onError.onRewardedAdError();
                                    }
                                });
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                            }

                            @Override
                            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                onError.onRewardedAdError();
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "failed to load rewarded ad");
                            }
                        });
                        break;

                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
                            @Override
                            public void onAdOpened(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdAvailable(
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                if (onLoaded != null) {
                                    onLoaded.onRewardedAdLoaded();
                                    Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
                                }
                                IronSource.showRewardedVideo(ironSourceRewardedId);
                            }

                            @Override
                            public void onAdUnavailable() {
                                onError.onRewardedAdError();
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError,
                                    com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                onError.onRewardedAdError();
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

                    case WORTISE:
                        wortiseRewardedAd = new com.wortise.ads.rewarded.RewardedAd(activity, wortiseRewardedId);
                        wortiseRewardedAd.setListener(new com.wortise.ads.rewarded.RewardedAd.Listener() {
                            @Override
                            public void onRewardedFailedToLoad(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                onError.onRewardedAdError();
                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull com.wortise.ads.rewarded.RewardedAd ad,
                                    @NonNull com.wortise.ads.AdError error) {
                                onError.onRewardedAdError();
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
                                onLoaded.onRewardedAdLoaded();
                                wortiseRewardedAd.showAd();
                                Log.d(TAG, "[" + backupAdNetwork + "] [backup] " + "rewarded ad loaded");
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

                    case NONE:
                        break;

                    default:
                        break;
                }
            }
        }

        /**
         * Destroys all loaded rewarded ad instances and releases resources.
         * Call this method in your Activity's {@code onDestroy()} to prevent memory
         * leaks.
         */
        public void destroyRewardedAd() {
            if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                switch (adNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        adMobRewardedAd = null;
                        break;
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        adManagerRewardedAd = null;
                        break;
                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null) {
                            fanRewardedVideoAd.destroy();
                            fanRewardedVideoAd = null;
                        }
                        break;
                    case UNITY:
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinMaxRewardedAd != null) {
                            applovinMaxRewardedAd.destroy();
                            applovinMaxRewardedAd = null;
                        }
                        break;
                    case STARTAPP:
                        startAppRewardedAd = null;
                        break;
                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        break;
                    case WORTISE:
                        if (wortiseRewardedAd != null) {
                            wortiseRewardedAd.destroy();
                            wortiseRewardedAd = null;
                        }
                        break;
                }

                switch (backupAdNetwork) {
                    case ADMOB:
                    case FAN_BIDDING_ADMOB:
                        adMobRewardedAd = null;
                        break;
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_AD_MANAGER:
                        adManagerRewardedAd = null;
                        break;
                    case FAN:
                    case FACEBOOK:
                        if (fanRewardedVideoAd != null) {
                            fanRewardedVideoAd.destroy();
                            fanRewardedVideoAd = null;
                        }
                        break;
                    case UNITY:
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        if (applovinMaxRewardedAd != null) {
                            applovinMaxRewardedAd.destroy();
                            applovinMaxRewardedAd = null;
                        }
                        break;
                    case STARTAPP:
                        startAppRewardedAd = null;
                        break;
                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        break;
                    case WORTISE:
                        if (wortiseRewardedAd != null) {
                            wortiseRewardedAd.destroy();
                            wortiseRewardedAd = null;
                        }
                        break;
                }
            }
        }

    }

}
