package com.partharoypc.adglide.format;

import com.partharoypc.adglide.util.WaterfallManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinSdkUtils;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.partharoypc.adglide.util.OnInterstitialAdDismissedListener;
import com.partharoypc.adglide.util.OnInterstitialAdShowedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.AdRepository;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

public class InterstitialAd {

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private com.google.android.gms.ads.interstitial.InterstitialAd adMobInterstitialAd;
        private com.facebook.ads.InterstitialAd metaInterstitialAd;
        private MaxInterstitialAd appLovinMaxInterstitialAd;
        private com.applovin.adview.AppLovinInterstitialAd appLovinDiscoveryInterstitialAd;
        private StartAppAd startAppInterstitialAd;
        private com.wortise.ads.interstitial.InterstitialAd wortiseInterstitialAd;
        private int retryAttempt;
        private int counter = 1;

        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobInterstitialId = "";
        private String metaInterstitialId = "";
        private String unityInterstitialId = "";
        private String appLovinInterstitialId = "";
        private String appLovinInterstitialZoneId = "";
        private String ironSourceInterstitialId = "";
        private String wortiseInterstitialId = "";
        private int placementStatus = 1;
        private int interval = 3;
        private boolean legacyGDPR = false;

        /**
         * Initializes the InterstitialAd Builder.
         * 
         * @param activity The Activity context.
         */
        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        /**
         * Initiates the ad loading process.
         * 
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder build() {
            loadInterstitialAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            loadInterstitialAd(onInterstitialAdDismissedListener);
            return this;
        }

        /**
         * Shows the ad if it is loaded.
         */
        public void show() {
            showInterstitialAd();
        }

        public void show(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            showInterstitialAd(onInterstitialAdShowedListener, onInterstitialAdDismissedListener);
        }

        /**
         * Sets the ad status (e.g., ON/OFF).
         * 
         * @param adStatus The status string.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setAdStatus(@NonNull String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        /**
         * Sets the primary ad network to use.
         * 
         * @param adNetwork The primary network key.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setAdNetwork(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        /**
         * Sets a single backup ad network.
         * 
         * @param backupAdNetwork The backup network key.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setBackupAdNetwork(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        /**
         * Sets multiple backup ad networks for a waterfall fallback.
         * 
         * @param backupAdNetworks An array or varargs of backup network keys.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setBackupAdNetworks(@Nullable String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        /**
         * Sets the AdMobInterstitial Ad Unit ID.
         * 
         * @param adMobInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setAdMobInterstitialId(@NonNull String adMobInterstitialId) {
            this.adMobInterstitialId = adMobInterstitialId;
            return this;
        }

        /**
         * Sets the MetaInterstitial Ad Unit ID.
         * 
         * @param metaInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setMetaInterstitialId(@NonNull String metaInterstitialId) {
            this.metaInterstitialId = metaInterstitialId;
            return this;
        }

        /**
         * Sets the UnityInterstitial Ad Unit ID.
         * 
         * @param unityInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setUnityInterstitialId(@NonNull String unityInterstitialId) {
            this.unityInterstitialId = unityInterstitialId;
            return this;
        }

        /**
         * Sets the AppLovinInterstitial Ad Unit ID.
         * 
         * @param appLovinInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setAppLovinInterstitialId(@NonNull String appLovinInterstitialId) {
            this.appLovinInterstitialId = appLovinInterstitialId;
            return this;
        }

        /**
         * Sets the AppLovinInterstitialZone Ad Unit ID.
         * 
         * @param appLovinInterstitialZoneId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setAppLovinInterstitialZoneId(@NonNull String appLovinInterstitialZoneId) {
            this.appLovinInterstitialZoneId = appLovinInterstitialZoneId;
            return this;
        }

        /**
         * Sets the ironSourceInterstitial Ad Unit ID.
         * 
         * @param ironSourceInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setironSourceInterstitialId(@NonNull String ironSourceInterstitialId) {
            this.ironSourceInterstitialId = ironSourceInterstitialId;
            return this;
        }

        /**
         * Sets the WortiseInterstitial Ad Unit ID.
         * 
         * @param wortiseInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setWortiseInterstitialId(@NonNull String wortiseInterstitialId) {
            this.wortiseInterstitialId = wortiseInterstitialId;
            return this;
        }

        /**
         * Sets the placement status.
         * 
         * @param placementStatus Integer representing status.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setPlacementStatus(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        /**
         * Sets the display interval.
         * 
         * @param interval The interval count.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        /**
         * Toggles legacy GDPR compliance extras.
         * 
         * @param legacyGDPR True to enable.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        private void loadInterstitialAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            Object cachedAdMob = AdRepository.getInstance().getInterstitial(adNetwork,
                                    adMobInterstitialId);
                            if (cachedAdMob instanceof com.google.android.gms.ads.interstitial.InterstitialAd) {
                                adMobInterstitialAd = (com.google.android.gms.ads.interstitial.InterstitialAd) cachedAdMob;
                                setupAdMobCallback();
                                Log.d(TAG, "AdMob Interstitial loaded from cache");
                            } else {
                                com.google.android.gms.ads.interstitial.InterstitialAd.load(activity,
                                        adMobInterstitialId,
                                        Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                                adMobInterstitialAd = interstitialAd;
                                                setupAdMobCallback();
                                                Log.i(TAG, "onAdLoaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                Log.i(TAG, loadAdError.getMessage());
                                                adMobInterstitialAd = null;
                                                loadBackupInterstitialAd();
                                                Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                            }
                                        });
                            }
                            break;
                        }

                        case META: {
                            metaInterstitialAd = new com.facebook.ads.InterstitialAd(activity, metaInterstitialId);
                            com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                                @Override
                                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                                }

                                @Override
                                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                    metaInterstitialAd.loadAd();
                                }

                                @Override
                                public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                    loadBackupInterstitialAd();
                                }

                                @Override
                                public void onAdLoaded(com.facebook.ads.Ad ad) {
                                    Log.d(TAG, "Meta Interstitial is loaded");
                                }

                                @Override
                                public void onAdClicked(com.facebook.ads.Ad ad) {
                                }

                                @Override
                                public void onLoggingImpression(com.facebook.ads.Ad ad) {
                                }
                            };

                            com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = metaInterstitialAd
                                    .buildLoadAdConfig().withAdListener(adListener).build();
                            metaInterstitialAd.loadAd(loadAdConfig);
                            break;
                        }

                        case UNITY: {
                            try {
                                UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                                    @Override
                                    public void onUnityAdsAdLoaded(String placementId) {
                                        Log.d(TAG, "Unity Interstitial Ad loaded");
                                    }

                                    @Override
                                    public void onUnityAdsFailedToLoad(String placementId,
                                            UnityAds.UnityAdsLoadError error,
                                            String message) {
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                                    }
                                });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup interstitial for Unity. Error: " + e.getMessage());
                                loadBackupInterstitialAd();
                            }
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            try {
                                appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                                appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                                    @Override
                                    public void onAdLoaded(MaxAd ad) {
                                        Log.d(TAG, "AppLovin Interstitial Ad loaded");
                                    }

                                    @Override
                                    public void onAdDisplayed(MaxAd ad) {
                                    }

                                    @Override
                                    public void onAdHidden(MaxAd ad) {
                                        loadInterstitialAd();
                                    }

                                    @Override
                                    public void onAdClicked(MaxAd ad) {
                                    }

                                    @Override
                                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                                    }

                                    @Override
                                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                    }
                                });
                                appLovinMaxInterstitialAd.loadAd();
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup interstitial for AppLovin. Error: " + e.getMessage());
                                loadBackupInterstitialAd();
                            }
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            loadBackupInterstitialAd();
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            try {
                                IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                                    @Override
                                    public void onAdReady(
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                        Log.d(TAG, "ironSource Interstitial Ad loaded");
                                    }

                                    @Override
                                    public void onAdLoadFailed(IronSourceError ironSourceError) {
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "ironSource Interstitial Ad failed to load: "
                                                + ironSourceError.getErrorMessage());
                                    }

                                    @Override
                                    public void onAdOpened(
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    }

                                    @Override
                                    public void onAdClosed(
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                        loadInterstitialAd();
                                    }

                                    @Override
                                    public void onAdShowSucceeded(
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    }

                                    @Override
                                    public void onAdShowFailed(IronSourceError ironSourceError,
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    }

                                    @Override
                                    public void onAdClicked(
                                            com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    }
                                });
                                IronSource.loadInterstitial();
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG,
                                        "Failed to load backup interstitial for IronSource. Error: " + e.getMessage());
                                loadBackupInterstitialAd();
                            }
                            break;
                        }

                        case STARTAPP: {
                            try {
                                startAppInterstitialAd = new StartAppAd(activity);
                                startAppInterstitialAd.loadAd(new AdEventListener() {
                                    @Override
                                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        Log.d(TAG, "StartApp Interstitial Ad loaded");
                                    }

                                    @Override
                                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        loadBackupInterstitialAd();
                                        Log.d(TAG, "StartApp Interstitial Ad failed to load");
                                    }
                                });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup interstitial for StartApp. Error: " + e.getMessage());
                                loadBackupInterstitialAd();
                            }
                            break;
                        }

                        case WORTISE: {
                            try {
                                wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                        wortiseInterstitialId);
                                wortiseInterstitialAd
                                        .setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                                            @Override
                                            public void onInterstitialClicked(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            }

                                            @Override
                                            public void onInterstitialDismissed(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                                loadInterstitialAd();
                                            }

                                            @Override
                                            public void onInterstitialFailedToLoad(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                    @NonNull com.wortise.ads.AdError adError) {
                                                loadBackupInterstitialAd();
                                                Log.d(TAG, "Wortise Interstitial Ad failed to load");
                                            }

                                            @Override
                                            public void onInterstitialFailedToShow(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                    @NonNull com.wortise.ads.AdError adError) {
                                            }

                                            @Override
                                            public void onInterstitialImpression(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            }

                                            @Override
                                            public void onInterstitialLoaded(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                                Log.d(TAG, "Wortise Interstitial Ad loaded");
                                            }

                                            @Override
                                            public void onInterstitialShown(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            }

                                            @Override
                                            public void onInterstitialRevenuePaid(
                                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                    @NonNull com.wortise.ads.RevenueData revenueData) {
                                            }
                                        });
                                wortiseInterstitialAd.loadAd();
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup interstitial for Wortise. Error: " + e.getMessage());
                                loadBackupInterstitialAd();
                            }
                            break;
                        }

                        default:
                            loadBackupInterstitialAd();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Interstitial Ad: " + e.getMessage());
            }
        }

        private void loadBackupInterstitialAd() {
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
                        Log.d(TAG, "All backup interstitial ads failed to load");
                        return;
                    }
                    backupAdNetwork = networkToLoad;
                    Log.d(TAG, "Loading Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT)
                            + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            Object cachedAdMob = AdRepository.getInstance().getInterstitial(backupAdNetwork,
                                    adMobInterstitialId);
                            if (cachedAdMob instanceof com.google.android.gms.ads.interstitial.InterstitialAd) {
                                adMobInterstitialAd = (com.google.android.gms.ads.interstitial.InterstitialAd) cachedAdMob;
                                setupAdMobCallback();
                                Log.d(TAG, "AdMob Backup Interstitial loaded from cache");
                            } else {
                                com.google.android.gms.ads.interstitial.InterstitialAd.load(activity,
                                        adMobInterstitialId,
                                        Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                            @Override
                                            public void onAdLoaded(
                                                    @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                                adMobInterstitialAd = interstitialAd;
                                                setupAdMobCallback();
                                                Log.i(TAG, "onAdLoaded");
                                            }

                                            @Override
                                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                                Log.i(TAG, loadAdError.getMessage());
                                                adMobInterstitialAd = null;
                                                loadBackupInterstitialAd();
                                                Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                            }
                                        });
                            }
                            break;
                        }

                        case META: {
                            Object cachedMeta = AdRepository.getInstance().getInterstitial(backupAdNetwork,
                                    metaInterstitialId);
                            if (cachedMeta instanceof com.facebook.ads.InterstitialAd) {
                                metaInterstitialAd = (com.facebook.ads.InterstitialAd) cachedMeta;
                                setupMetaCallback();
                                Log.d(TAG, "Meta Backup Interstitial loaded from cache");
                            } else {
                                metaInterstitialAd = new com.facebook.ads.InterstitialAd(activity, metaInterstitialId);
                                setupMetaCallback();
                                com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = metaInterstitialAd
                                        .buildLoadAdConfig().withAdListener(metaAdListener).build();
                                metaInterstitialAd.loadAd(loadAdConfig);
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    Log.d(TAG, "Unity Interstitial Ad loaded");
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadBackupInterstitialAd();
                                    Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                            appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Log.d(TAG, "AppLovin Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    loadInterstitialAd();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    loadBackupInterstitialAd();
                                    Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }
                            });
                            appLovinMaxInterstitialAd.loadAd();
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            loadBackupInterstitialAd();
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                                @Override
                                public void onAdReady(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    Log.d(TAG, "ironSource Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError ironSourceError) {
                                    loadBackupInterstitialAd();
                                    Log.d(TAG, "ironSource Interstitial Ad failed to load: "
                                            + ironSourceError.getErrorMessage());
                                }

                                @Override
                                public void onAdOpened(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClosed(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    loadInterstitialAd();
                                }

                                @Override
                                public void onAdShowSucceeded(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdShowFailed(IronSourceError ironSourceError,
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClicked(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }
                            });
                            IronSource.loadInterstitial();
                            break;
                        }

                        case STARTAPP: {
                            startAppInterstitialAd = new StartAppAd(activity);
                            startAppInterstitialAd.loadAd(new AdEventListener() {
                                @Override
                                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                    Log.d(TAG, "StartApp Interstitial Ad loaded");
                                }

                                @Override
                                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                    loadBackupInterstitialAd();
                                    Log.d(TAG, "StartApp Interstitial Ad failed to load");
                                }
                            });
                            break;
                        }

                        case WORTISE: {
                            wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                    wortiseInterstitialId);
                            wortiseInterstitialAd
                                    .setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                                        @Override
                                        public void onInterstitialClicked(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialDismissed(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            loadInterstitialAd();
                                        }

                                        @Override
                                        public void onInterstitialFailedToLoad(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                            loadBackupInterstitialAd();
                                            Log.d(TAG, "Wortise Interstitial Ad failed to load");
                                        }

                                        @Override
                                        public void onInterstitialFailedToShow(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                        }

                                        @Override
                                        public void onInterstitialImpression(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialLoaded(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            Log.d(TAG, "Wortise Interstitial Ad loaded");
                                        }

                                        @Override
                                        public void onInterstitialShown(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialRevenuePaid(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.RevenueData revenueData) {
                                        }
                                    });
                            wortiseInterstitialAd.loadAd();
                            break;
                        }

                        default:
                            loadBackupInterstitialAd();
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Interstitial Ad: " + e.getMessage());
            }
        }

        public void showInterstitialAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (counter == interval) {
                        switch (adNetwork) {
                            case ADMOB:
                            case META_BIDDING_ADMOB: {
                                if (adMobInterstitialAd != null) {
                                    adMobInterstitialAd.show(activity);
                                    Log.d(TAG, "admob interstitial not null");
                                } else {
                                    showBackupInterstitialAd();
                                    Log.d(TAG, "admob interstitial null");
                                }
                                break;
                            }

                            case META: {
                                if (metaInterstitialAd != null && metaInterstitialAd.isAdLoaded()) {
                                    metaInterstitialAd.show();
                                    Log.d(TAG, "meta interstitial not null");
                                } else {
                                    showBackupInterstitialAd();
                                    Log.d(TAG, "meta interstitial null");
                                }
                                break;
                            }

                            case UNITY: {
                                if (UnityAds.isInitialized()) {
                                    UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                        @Override
                                        public void onUnityAdsShowFailure(String placementId,
                                                UnityAds.UnityAdsShowError error, String message) {
                                            showBackupInterstitialAd();
                                            Log.d(TAG, "Unity Interstitial Failed to show: " + message);
                                        }

                                        @Override
                                        public void onUnityAdsShowStart(String placementId) {
                                        }

                                        @Override
                                        public void onUnityAdsShowClick(String placementId) {
                                        }

                                        @Override
                                        public void onUnityAdsShowComplete(String placementId,
                                                UnityAds.UnityAdsShowCompletionState state) {
                                        }
                                    });
                                } else {
                                    showBackupInterstitialAd();
                                }
                                break;
                            }

                            case APPLOVIN:
                            case APPLOVIN_MAX:
                            case META_BIDDING_APPLOVIN_MAX: {
                                if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                    appLovinMaxInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd();
                                }
                                break;
                            }

                            case APPLOVIN_DISCOVERY: {
                                break;
                            }

                            case IRONSOURCE:
                            case META_BIDDING_IRONSOURCE: {
                                if (IronSource.isInterstitialReady()) {
                                    IronSource.showInterstitial();
                                } else {
                                    showBackupInterstitialAd();
                                }
                                break;
                            }

                            case STARTAPP: {
                                if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                    startAppInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd();
                                }
                                break;
                            }

                            case WORTISE: {
                                if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                    wortiseInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd();
                                }
                                break;
                            }

                            default:
                                showBackupInterstitialAd();
                                break;
                        }
                        counter = 1;
                    } else {
                        counter++;
                    }
                    Log.d(TAG, "Current counter : " + counter);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showInterstitialAd: " + e.getMessage());
            }
        }

        public void showBackupInterstitialAd() {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    Log.d(TAG,
                            "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            if (adMobInterstitialAd != null) {
                                adMobInterstitialAd.show(activity);
                            }
                            break;
                        }

                        case META: {
                            if (metaInterstitialAd != null && metaInterstitialAd.isAdLoaded()) {
                                metaInterstitialAd.show();
                            }
                            break;
                        }

                        case UNITY: {
                            if (UnityAds.isInitialized()) {
                                UnityAds.show(activity, unityInterstitialId);
                            }
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                appLovinMaxInterstitialAd.showAd();
                            }
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial();
                            }
                            break;
                        }

                        case STARTAPP: {
                            if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                startAppInterstitialAd.showAd();
                            }
                            break;
                        }

                        case WORTISE: {
                            if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                wortiseInterstitialAd.showAd();
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showBackupInterstitialAd: " + e.getMessage());
            }
        }

        public void loadInterstitialAd(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    switch (adNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                    Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                            adMobInterstitialAd = interstitialAd;
                                            adMobInterstitialAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            loadInterstitialAd(onInterstitialAdDismissedListener);
                                                            onInterstitialAdDismissedListener
                                                                    .onInterstitialAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull com.google.android.gms.ads.AdError adError) {
                                                            Log.d(TAG, "The ad failed to show.");
                                                        }

                                                        @Override
                                                        public void onAdShowedFullScreenContent() {
                                                            adMobInterstitialAd = null;
                                                            Log.d(TAG, "The ad was shown.");
                                                        }
                                                    });
                                            Log.i(TAG, "onAdLoaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            Log.i(TAG, loadAdError.getMessage());
                                            adMobInterstitialAd = null;
                                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                        }
                                    });
                            break;
                        }

                        case META: {
                            metaInterstitialAd = new com.facebook.ads.InterstitialAd(activity, metaInterstitialId);
                            com.facebook.ads.InterstitialAdListener adListener = new InterstitialAdListener() {
                                @Override
                                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                                }

                                @Override
                                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                                    metaInterstitialAd.loadAd();
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }

                                @Override
                                public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                }

                                @Override
                                public void onAdLoaded(com.facebook.ads.Ad ad) {
                                    Log.d(TAG, "Meta Interstitial is loaded");
                                }

                                @Override
                                public void onAdClicked(com.facebook.ads.Ad ad) {
                                }

                                @Override
                                public void onLoggingImpression(com.facebook.ads.Ad ad) {
                                }
                            };

                            com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = metaInterstitialAd
                                    .buildLoadAdConfig().withAdListener(adListener).build();
                            metaInterstitialAd.loadAd(loadAdConfig);
                            break;
                        }

                        case UNITY: {
                            UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    Log.d(TAG, "Unity Interstitial Ad loaded");
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                            appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Log.d(TAG, "AppLovin Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    loadInterstitialAd(onInterstitialAdDismissedListener);
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }
                            });
                            appLovinMaxInterstitialAd.loadAd();
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                                @Override
                                public void onAdReady(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    Log.d(TAG, "ironSource Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError ironSourceError) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "ironSource Interstitial Ad failed to load: "
                                            + ironSourceError.getErrorMessage());
                                }

                                @Override
                                public void onAdOpened(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClosed(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    loadInterstitialAd(onInterstitialAdDismissedListener);
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }

                                @Override
                                public void onAdShowSucceeded(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdShowFailed(IronSourceError ironSourceError,
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClicked(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }
                            });
                            IronSource.loadInterstitial();
                            break;
                        }

                        case STARTAPP: {
                            try {
                                startAppInterstitialAd = new StartAppAd(activity);
                                startAppInterstitialAd.loadAd(new AdEventListener() {
                                    @Override
                                    public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        Log.d(TAG, "StartApp Interstitial Ad loaded");
                                    }

                                    @Override
                                    public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                        loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                        Log.d(TAG, "StartApp Interstitial Ad failed to load");
                                    }
                                });
                            } catch (NoClassDefFoundError | Exception e) {
                                Log.e(TAG, "Failed to load backup interstitial for StartApp. Error: " + e.getMessage());
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            }
                            break;
                        }

                        case WORTISE: {
                            wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                    wortiseInterstitialId);
                            wortiseInterstitialAd
                                    .setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                                        @Override
                                        public void onInterstitialClicked(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialDismissed(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            loadInterstitialAd(onInterstitialAdDismissedListener);
                                            onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                        }

                                        @Override
                                        public void onInterstitialFailedToLoad(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                            Log.d(TAG, "Wortise Interstitial Ad failed to load");
                                        }

                                        @Override
                                        public void onInterstitialFailedToShow(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                        }

                                        @Override
                                        public void onInterstitialImpression(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialLoaded(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            Log.d(TAG, "Wortise Interstitial Ad loaded");
                                        }

                                        @Override
                                        public void onInterstitialShown(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialRevenuePaid(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.RevenueData revenueData) {
                                        }
                                    });
                            wortiseInterstitialAd.loadAd();
                            break;
                        }

                        default:
                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadInterstitialAd: " + e.getMessage());
            }
        }

        public void loadBackupInterstitialAd(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
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
                        Log.d(TAG, "All backup interstitial ads failed to load");
                        return;
                    }
                    backupAdNetwork = networkToLoad;
                    Log.d(TAG, "Loading Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT)
                            + "]");

                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                    Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                            adMobInterstitialAd = interstitialAd;
                                            adMobInterstitialAd
                                                    .setFullScreenContentCallback(new FullScreenContentCallback() {
                                                        @Override
                                                        public void onAdDismissedFullScreenContent() {
                                                            loadInterstitialAd(onInterstitialAdDismissedListener);
                                                            onInterstitialAdDismissedListener
                                                                    .onInterstitialAdDismissed();
                                                        }

                                                        @Override
                                                        public void onAdFailedToShowFullScreenContent(
                                                                @NonNull com.google.android.gms.ads.AdError adError) {
                                                            Log.d(TAG, "The ad failed to show.");
                                                        }

                                                        @Override
                                                        public void onAdShowedFullScreenContent() {
                                                            adMobInterstitialAd = null;
                                                            Log.d(TAG, "The ad was shown.");
                                                        }
                                                    });
                                            Log.i(TAG, "onAdLoaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                            Log.i(TAG, loadAdError.getMessage());
                                            adMobInterstitialAd = null;
                                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                        }
                                    });
                            break;
                        }

                        case META: {
                            Object cachedMeta = AdRepository.getInstance().getInterstitial(adNetwork,
                                    metaInterstitialId);
                            if (cachedMeta instanceof com.facebook.ads.InterstitialAd) {
                                metaInterstitialAd = (com.facebook.ads.InterstitialAd) cachedMeta;
                                setupMetaCallback();
                                Log.d(TAG, "Meta Interstitial loaded from cache");
                            } else {
                                metaInterstitialAd = new com.facebook.ads.InterstitialAd(activity, metaInterstitialId);
                                setupMetaCallback();
                                com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = metaInterstitialAd
                                        .buildLoadAdConfig().withAdListener(metaAdListener).build();
                                metaInterstitialAd.loadAd(loadAdConfig);
                            }
                            break;
                        }

                        case UNITY: {
                            UnityAds.load(unityInterstitialId, new IUnityAdsLoadListener() {
                                @Override
                                public void onUnityAdsAdLoaded(String placementId) {
                                    Log.d(TAG, "Unity Interstitial Ad loaded");
                                }

                                @Override
                                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error,
                                        String message) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "Unity Interstitial Ad failed to load: " + error + " - " + message);
                                }
                            });
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            appLovinMaxInterstitialAd = new MaxInterstitialAd(appLovinInterstitialId, activity);
                            appLovinMaxInterstitialAd.setListener(new MaxAdListener() {
                                @Override
                                public void onAdLoaded(MaxAd ad) {
                                    Log.d(TAG, "AppLovin Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdDisplayed(MaxAd ad) {
                                }

                                @Override
                                public void onAdHidden(MaxAd ad) {
                                    loadInterstitialAd(onInterstitialAdDismissedListener);
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }

                                @Override
                                public void onAdClicked(MaxAd ad) {
                                }

                                @Override
                                public void onAdLoadFailed(String adUnitId, MaxError error) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "AppLovin Interstitial Ad failed to load: " + error.getMessage());
                                }

                                @Override
                                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                                }
                            });
                            appLovinMaxInterstitialAd.loadAd();
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                                @Override
                                public void onAdReady(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    Log.d(TAG, "ironSource Interstitial Ad loaded");
                                }

                                @Override
                                public void onAdLoadFailed(IronSourceError ironSourceError) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "ironSource Interstitial Ad failed to load: "
                                            + ironSourceError.getErrorMessage());
                                }

                                @Override
                                public void onAdOpened(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClosed(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                    loadInterstitialAd(onInterstitialAdDismissedListener);
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }

                                @Override
                                public void onAdShowSucceeded(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdShowFailed(IronSourceError ironSourceError,
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }

                                @Override
                                public void onAdClicked(
                                        com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                }
                            });
                            IronSource.loadInterstitial();
                            break;
                        }

                        case STARTAPP: {
                            startAppInterstitialAd = new StartAppAd(activity);
                            startAppInterstitialAd.loadAd(new AdEventListener() {
                                @Override
                                public void onReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                    Log.i(TAG, "onReceiveAd");
                                }

                                @Override
                                public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                                    loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                    Log.d(TAG, "Failed load StartApp Interstitial Ad");
                                }
                            });
                            break;
                        }

                        case WORTISE: {
                            wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                    wortiseInterstitialId);
                            wortiseInterstitialAd
                                    .setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                                        @Override
                                        public void onInterstitialClicked(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialDismissed(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            loadInterstitialAd(onInterstitialAdDismissedListener);
                                            onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                        }

                                        @Override
                                        public void onInterstitialFailedToLoad(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                            Log.d(TAG, "Wortise Interstitial Ad failed to load");
                                        }

                                        @Override
                                        public void onInterstitialFailedToShow(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.AdError adError) {
                                        }

                                        @Override
                                        public void onInterstitialImpression(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialLoaded(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                            Log.d(TAG, "Wortise Interstitial Ad loaded");
                                        }

                                        @Override
                                        public void onInterstitialShown(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                        }

                                        @Override
                                        public void onInterstitialRevenuePaid(
                                                @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd,
                                                @NonNull com.wortise.ads.RevenueData revenueData) {
                                        }
                                    });
                            wortiseInterstitialAd.loadAd();
                            break;
                        }

                        default:
                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupInterstitialAd: " + e.getMessage());
            }
        }

        public void showInterstitialAd(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    if (counter == interval) {
                        switch (adNetwork) {
                            case ADMOB:
                            case META_BIDDING_ADMOB: {
                                if (adMobInterstitialAd != null) {
                                    adMobInterstitialAd.show(activity);
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                    Log.d(TAG, "admob interstitial not null");
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                    Log.d(TAG, "admob interstitial null");
                                }
                                break;
                            }

                            case META: {
                                if (metaInterstitialAd != null && metaInterstitialAd.isAdLoaded()) {
                                    metaInterstitialAd.show();
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                    Log.d(TAG, "meta interstitial not null");
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                    Log.d(TAG, "meta interstitial null");
                                }
                                break;
                            }

                            case UNITY: {
                                if (UnityAds.isInitialized()) {
                                    UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                        @Override
                                        public void onUnityAdsShowFailure(String placementId,
                                                UnityAds.UnityAdsShowError error, String message) {
                                            showBackupInterstitialAd(onInterstitialAdShowedListener,
                                                    onInterstitialAdDismissedListener);
                                        }

                                        @Override
                                        public void onUnityAdsShowStart(String placementId) {
                                            onInterstitialAdShowedListener.onInterstitialAdShowed();
                                        }

                                        @Override
                                        public void onUnityAdsShowClick(String placementId) {
                                        }

                                        @Override
                                        public void onUnityAdsShowComplete(String placementId,
                                                UnityAds.UnityAdsShowCompletionState state) {
                                        }
                                    });
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                }
                                break;
                            }

                            case APPLOVIN:
                            case APPLOVIN_MAX:
                            case META_BIDDING_APPLOVIN_MAX: {
                                if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                    appLovinMaxInterstitialAd.showAd();
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                }
                                break;
                            }

                            case APPLOVIN_DISCOVERY: {
                                break;
                            }

                            case IRONSOURCE:
                            case META_BIDDING_IRONSOURCE: {
                                if (IronSource.isInterstitialReady()) {
                                    IronSource.showInterstitial();
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                }
                                break;
                            }

                            case STARTAPP: {
                                if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                    startAppInterstitialAd.showAd();
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                }
                                break;
                            }

                            case WORTISE: {
                                if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                    wortiseInterstitialAd.showAd();
                                    onInterstitialAdShowedListener.onInterstitialAdShowed();
                                } else {
                                    showBackupInterstitialAd(onInterstitialAdShowedListener,
                                            onInterstitialAdDismissedListener);
                                }
                                break;
                            }

                            default:
                                showBackupInterstitialAd(onInterstitialAdShowedListener,
                                        onInterstitialAdDismissedListener);
                                break;
                        }
                        counter = 1;
                    } else {
                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                        counter++;
                    }
                    Log.d(TAG, "Current counter : " + counter);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showInterstitialAd: " + e.getMessage());
                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
            }
        }

        public void showBackupInterstitialAd(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                if (adStatus.equals(AD_STATUS_ON) && placementStatus != 0) {
                    Log.d(TAG,
                            "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");
                    switch (backupAdNetwork) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            if (adMobInterstitialAd != null) {
                                adMobInterstitialAd.show(activity);
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        case META: {
                            if (metaInterstitialAd != null && metaInterstitialAd.isAdLoaded()) {
                                metaInterstitialAd.show();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        case UNITY: {
                            if (UnityAds.isInitialized()) {
                                UnityAds.show(activity, unityInterstitialId, new IUnityAdsShowListener() {
                                    @Override
                                    public void onUnityAdsShowFailure(String placementId,
                                            UnityAds.UnityAdsShowError error,
                                            String message) {
                                    }

                                    @Override
                                    public void onUnityAdsShowStart(String placementId) {
                                        onInterstitialAdShowedListener.onInterstitialAdShowed();
                                    }

                                    @Override
                                    public void onUnityAdsShowClick(String placementId) {
                                    }

                                    @Override
                                    public void onUnityAdsShowComplete(String placementId,
                                            UnityAds.UnityAdsShowCompletionState state) {
                                    }
                                });
                            }
                            break;
                        }

                        case APPLOVIN:
                        case APPLOVIN_MAX:
                        case META_BIDDING_APPLOVIN_MAX: {
                            if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                appLovinMaxInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        case APPLOVIN_DISCOVERY: {
                            break;
                        }

                        case IRONSOURCE:
                        case META_BIDDING_IRONSOURCE: {
                            if (IronSource.isInterstitialReady()) {
                                IronSource.showInterstitial();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        case STARTAPP: {
                            if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                startAppInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        case WORTISE: {
                            if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                wortiseInterstitialAd.showAd();
                                onInterstitialAdShowedListener.onInterstitialAdShowed();
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in showBackupInterstitialAd: " + e.getMessage());
                onInterstitialAdDismissedListener.onInterstitialAdDismissed();
            }
        }

        public void destroyInterstitialAd() {
            if (adMobInterstitialAd != null) {
                adMobInterstitialAd = null;
            }
            if (metaInterstitialAd != null) {
                metaInterstitialAd.destroy();
                metaInterstitialAd = null;
            }
            if (appLovinMaxInterstitialAd != null) {
                appLovinMaxInterstitialAd.destroy();
                appLovinMaxInterstitialAd = null;
            }
            if (appLovinDiscoveryInterstitialAd != null) {
                appLovinDiscoveryInterstitialAd = null;
            }
            if (wortiseInterstitialAd != null) {
                wortiseInterstitialAd.destroy();
                wortiseInterstitialAd = null;
            }
            if (startAppInterstitialAd != null) {
                startAppInterstitialAd = null;
            }
        }

        private com.facebook.ads.InterstitialAdListener metaAdListener;

        private void setupAdMobCallback() {
            if (adMobInterstitialAd == null)
                return;
            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    loadInterstitialAd();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    Log.d(TAG, "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    adMobInterstitialAd = null;
                    Log.d(TAG, "The ad was shown.");
                }
            });
        }

        private void setupMetaCallback() {
            metaAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                    metaInterstitialAd.loadAd();
                }

                @Override
                public void onError(com.facebook.ads.Ad ad, com.facebook.ads.AdError adError) {
                    loadBackupInterstitialAd();
                }

                @Override
                public void onAdLoaded(com.facebook.ads.Ad ad) {
                    Log.d(TAG, "Meta Interstitial is loaded");
                }

                @Override
                public void onAdClicked(com.facebook.ads.Ad ad) {
                }

                @Override
                public void onLoggingImpression(com.facebook.ads.Ad ad) {
                }
            };
        }
    }

}
