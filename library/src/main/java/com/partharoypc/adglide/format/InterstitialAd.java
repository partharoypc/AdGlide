package com.partharoypc.adglide.format;

import com.partharoypc.adglide.util.WaterfallManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

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

        private StartAppAd startAppInterstitialAd;
        private com.wortise.ads.interstitial.InterstitialAd wortiseInterstitialAd;
        private int retryAttempt;
        private int counter = 1;

        private boolean adStatus = true;
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
            return this;
        }

        @androidx.annotation.NonNull
        public Builder build(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadInterstitialAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            loadInterstitialAd(onInterstitialAdDismissedListener);
            return this;
        }

        /**
         * Shows the ad if it is loaded, using the originally provided Activity.
         */
        public void show() {
            showInterstitialAd(null, null, null);
        }

        /**
         * Shows the ad if it is loaded, using a specifically provided Activity.
         * Crucial for showing pre-loaded ads in different Activities/Fragments.
         */
        public void show(@NonNull Activity displayActivity) {
            showInterstitialAd(displayActivity, null, null);
        }

        public void show(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            showInterstitialAd(null, onInterstitialAdShowedListener, onInterstitialAdDismissedListener);
        }

        public void show(@NonNull Activity displayActivity,
                OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            showInterstitialAd(displayActivity, onInterstitialAdShowedListener, onInterstitialAdDismissedListener);
        }

        /**
         * Sets the ad status (e.g., ON/OFF).
         * 
         * @param adStatus The status string.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder status(boolean adStatus) {
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
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        /**
         * Sets the primary ad network using AdGlideNetwork enum.
         * 
         * @param network The primary network enum.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        /**
         * Sets a single backup ad network.
         * 
         * @param backupAdNetwork The backup network key.
         * @return The configured Builder instance.
         */
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

        /**
         * Sets multiple backup ad networks for a waterfall fallback.
         * 
         * @param backupAdNetworks An array or varargs of backup network keys.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder backups(@Nullable String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        /**
         * Sets multiple backup ad networks using AdGlideNetwork enum.
         * 
         * @param backupAdNetworks Varargs of network enums.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        /**
         * Sets the AdMobInterstitial Ad Unit ID.
         * 
         * @param adMobInterstitialId The placement ID.
         * @return The configured Builder instance.
         */
        @NonNull
        public Builder adMobId(@NonNull String adMobInterstitialId) {
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
        public Builder metaId(@NonNull String metaInterstitialId) {
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
        public Builder unityId(@NonNull String unityInterstitialId) {
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
        public Builder appLovinId(@NonNull String appLovinInterstitialId) {
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
        public Builder zoneId(@NonNull String appLovinInterstitialZoneId) {
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
        public Builder ironSourceId(@NonNull String ironSourceInterstitialId) {
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
        public Builder wortiseId(@NonNull String wortiseInterstitialId) {
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
        public Builder placement(int placementStatus) {
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
        public Builder interval(int interval) {
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
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        private void loadInterstitialAd() {
            loadInterstitialAd(null);
        }

        public void loadBackupInterstitialAd() {
            loadBackupInterstitialAd(null);
        }

        public void showInterstitialAd(Activity displayActivity, OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    if (counter == interval) {
                        switch (AdGlideNetwork.fromString(adNetwork)) {
                            case ADMOB:
                            case META_BIDDING_ADMOB: {
                                if (adMobInterstitialAd != null) {
                                    adMobInterstitialAd.show(targetActivity);
                                    Log.d(TAG, "admob interstitial not null");
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                    Log.d(TAG, "admob interstitial null");
                                }
                                break;
                            }

                            case META: {
                                if (metaInterstitialAd != null && metaInterstitialAd.isAdLoaded()) {
                                    metaInterstitialAd.show();
                                    Log.d(TAG, "meta interstitial not null");
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                    Log.d(TAG, "meta interstitial null");
                                }
                                break;
                            }

                            case UNITY: {
                                if (UnityAds.isInitialized()) {
                                    UnityAds.show(targetActivity, unityInterstitialId, new IUnityAdsShowListener() {
                                        @Override
                                        public void onUnityAdsShowFailure(String placementId,
                                                UnityAds.UnityAdsShowError error, String message) {
                                            showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
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
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                }
                                break;
                            }

                            case APPLOVIN:
                            case APPLOVIN_MAX:
                            case META_BIDDING_APPLOVIN_MAX: {
                                if (appLovinMaxInterstitialAd != null && appLovinMaxInterstitialAd.isReady()) {
                                    appLovinMaxInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                }
                                break;
                            }

                            case IRONSOURCE:
                            case META_BIDDING_IRONSOURCE: {
                                if (IronSource.isInterstitialReady()) {
                                    IronSource.showInterstitial();
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                }
                                break;
                            }

                            case STARTAPP: {
                                if (startAppInterstitialAd != null && startAppInterstitialAd.isReady()) {
                                    startAppInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                }
                                break;
                            }

                            case WORTISE: {
                                if (wortiseInterstitialAd != null && wortiseInterstitialAd.isAvailable()) {
                                    wortiseInterstitialAd.showAd();
                                } else {
                                    showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
                                }
                                break;
                            }

                            default:
                                showBackupInterstitialAd(targetActivity, showedListener, dismissedListener);
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
            showBackupInterstitialAd(null, null, null);
        }

        public void showBackupInterstitialAd(Activity displayActivity, OnInterstitialAdShowedListener showedListener,
                OnInterstitialAdDismissedListener dismissedListener) {
            try {
                Activity targetActivity = displayActivity != null ? displayActivity : activity;
                if (adStatus && placementStatus != 0) {
                    Log.d(TAG,
                            "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");
                    switch (AdGlideNetwork.fromString(backupAdNetwork)) {
                        case ADMOB:
                        case META_BIDDING_ADMOB: {
                            if (adMobInterstitialAd != null) {
                                adMobInterstitialAd.show(targetActivity);
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
                                UnityAds.show(targetActivity, unityInterstitialId);
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
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Interstitial Ad load.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    Log.d(TAG, "Interstitial Ad is enabled: " + adNetwork);
                    loadAdFromNetwork(adNetwork, onInterstitialAdDismissedListener);
                } else {
                    Log.d(TAG, "Interstitial Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadInterstitialAd: " + e.getMessage());
            }
        }

        public void loadBackupInterstitialAd(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Backup Interstitial Ad load.");
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
                        Log.d(TAG, "All backup interstitial ads failed to load");
                        return;
                    }
                    backupAdNetwork = networkToLoad;
                    Log.d(TAG, "Loading Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT)
                            + "]");

                    loadAdFromNetwork(backupAdNetwork, onInterstitialAdDismissedListener);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadBackupInterstitialAd: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                switch (AdGlideNetwork.fromString(networkToLoad)) {
                    case ADMOB:
                    case META_BIDDING_ADMOB: {
                        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adMobInterstitialId)) {
                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                            break;
                        }
                        Object cachedAdMob = AdRepository.getInstance().getInterstitial(adNetwork, adMobInterstitialId);
                        if (cachedAdMob instanceof com.google.android.gms.ads.interstitial.InterstitialAd) {
                            adMobInterstitialAd = (com.google.android.gms.ads.interstitial.InterstitialAd) cachedAdMob;
                            setupAdMobCallback(onInterstitialAdDismissedListener);
                            Log.d(TAG, "AdMob Interstitial loaded from cache");
                        } else {
                            com.google.android.gms.ads.interstitial.InterstitialAd.load(activity, adMobInterstitialId,
                                    Tools.getAdRequest(activity, legacyGDPR), new InterstitialAdLoadCallback() {
                                        @Override
                                        public void onAdLoaded(
                                                @NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                                            adMobInterstitialAd = interstitialAd;
                                            setupAdMobCallback(onInterstitialAdDismissedListener);
                                            Log.i(TAG, "onAdLoaded");
                                        }

                                        @Override
                                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                                            if (loadAdError
                                                    .getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                                                com.partharoypc.adglide.util.AdMobRateLimiter
                                                        .recordFailure(adMobInterstitialId);
                                            }
                                            Log.i(TAG, loadAdError.getMessage());
                                            adMobInterstitialAd = null;
                                            loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                            Log.d(TAG, "Failed load AdMob Interstitial Ad");
                                        }
                                    });
                        }
                        break;
                    }

                    case META: {
                        Object cachedMeta = AdRepository.getInstance().getInterstitial(adNetwork, metaInterstitialId);
                        if (cachedMeta instanceof com.facebook.ads.InterstitialAd) {
                            metaInterstitialAd = (com.facebook.ads.InterstitialAd) cachedMeta;
                            setupMetaCallback(onInterstitialAdDismissedListener);
                            Log.d(TAG, "Meta Interstitial loaded from cache");
                        } else {
                            metaInterstitialAd = new com.facebook.ads.InterstitialAd(activity, metaInterstitialId);
                            setupMetaCallback(onInterstitialAdDismissedListener);

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
                                if (onInterstitialAdDismissedListener != null) {
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }
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

                    case IRONSOURCE:
                    case META_BIDDING_IRONSOURCE: {
                        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                            @Override
                            public void onAdReady(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                Log.d(TAG, "ironSource Interstitial Ad loaded");
                            }

                            @Override
                            public void onAdLoadFailed(IronSourceError ironSourceError) {
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "ironSource Interstitial Ad failed to load: "
                                        + ironSourceError.getErrorMessage());
                            }

                            @Override
                            public void onAdOpened(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                            }

                            @Override
                            public void onAdClosed(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                if (onInterstitialAdDismissedListener != null) {
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }
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
                            public void onAdClicked(com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo adInfo) {
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
                                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
                                Log.d(TAG, "StartApp Interstitial Ad failed to load");
                            }
                        });
                        break;
                    }

                    case WORTISE: {
                        wortiseInterstitialAd = new com.wortise.ads.interstitial.InterstitialAd(activity,
                                wortiseInterstitialId);
                        wortiseInterstitialAd.setListener(new com.wortise.ads.interstitial.InterstitialAd.Listener() {
                            @Override
                            public void onInterstitialClicked(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                            }

                            @Override
                            public void onInterstitialDismissed(
                                    @NonNull com.wortise.ads.interstitial.InterstitialAd interstitialAd) {
                                loadInterstitialAd(onInterstitialAdDismissedListener);
                                if (onInterstitialAdDismissedListener != null) {
                                    onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                                }
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
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to load interstitial for " + networkToLoad + ". Error: " + e.getMessage());
                loadBackupInterstitialAd(onInterstitialAdDismissedListener);
            }
        }

        private void setupAdMobCallback(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            adMobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    loadInterstitialAd(onInterstitialAdDismissedListener);
                    if (onInterstitialAdDismissedListener != null) {
                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                    }
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

        private void setupMetaCallback(OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            metaAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
                }

                @Override
                public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
                    metaInterstitialAd.loadAd();
                    if (onInterstitialAdDismissedListener != null) {
                        onInterstitialAdDismissedListener.onInterstitialAdDismissed();
                    }
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
        }

        public void showInterstitialAd(OnInterstitialAdShowedListener onInterstitialAdShowedListener,
                OnInterstitialAdDismissedListener onInterstitialAdDismissedListener) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (counter == interval) {
                        switch (AdGlideNetwork.fromString(adNetwork)) {
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
                if (adStatus && placementStatus != 0) {
                    Log.d(TAG,
                            "Show Backup Interstitial Ad [" + backupAdNetwork.toUpperCase(java.util.Locale.ROOT) + "]");
                    switch (AdGlideNetwork.fromString(backupAdNetwork)) {
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
            if (wortiseInterstitialAd != null) {
                wortiseInterstitialAd.destroy();
                wortiseInterstitialAd = null;
            }
            if (startAppInterstitialAd != null) {
                startAppInterstitialAd = null;
            }
        }

        private com.facebook.ads.InterstitialAdListener metaAdListener;

    }

}
