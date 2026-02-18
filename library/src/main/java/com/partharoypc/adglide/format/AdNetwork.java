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

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.ironsource.mediationsdk.IronSource;
import com.partharoypc.adglide.helper.AudienceNetworkInitializeHelper;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.wortise.ads.WortiseSdk;

import java.util.Map;

public class AdNetwork {

    public static class Initialize {

        private static final String TAG = "AdNetwork";
        private final Activity activity;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private String adMobAppId = "";
        private String startappAppId = "0";
        private String unityGameId = "";
        private String appLovinSdkKey = "";
        private String ironSourceAppKey = "";
        private String wortiseAppId = "";
        private boolean debug = true;

        public Initialize(Activity activity) {
            this.activity = activity;
        }

        public Initialize build() {
            initAds();
            initBackupAds();
            return this;
        }

        public Initialize setAdStatus(String adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Initialize setAdNetwork(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Initialize setBackupAdNetwork(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            return this;
        }

        public Initialize setAdMobAppId(String adMobAppId) {
            this.adMobAppId = adMobAppId;
            return this;
        }

        public Initialize setStartappAppId(String startappAppId) {
            this.startappAppId = startappAppId;
            return this;
        }

        public Initialize setUnityGameId(String unityGameId) {
            this.unityGameId = unityGameId;
            return this;
        }

        public Initialize setAppLovinSdkKey(String appLovinSdkKey) {
            this.appLovinSdkKey = appLovinSdkKey;
            return this;
        }

        public Initialize setIronSourceAppKey(String ironSourceAppKey) {
            this.ironSourceAppKey = ironSourceAppKey;
            return this;
        }

        public Initialize setWortiseAppId(String wortiseAppId) {
            this.wortiseAppId = wortiseAppId;
            return this;
        }

        public Initialize setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public void initAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (adNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                if (adapterStatus != null) {
                                    Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d",
                                            adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                                }
                            }
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case UNITY:
                        UnityAds.initialize(activity, unityGameId, debug, new IUnityAdsInitializationListener() {
                            @Override
                            public void onInitializationComplete() {
                                Log.d(TAG, "Unity Ads Initialization Complete");
                            }

                            @Override
                            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error,
                                    String message) {
                                Log.d(TAG, "Unity Ads Initialization Failed: " + error + " - " + message);
                            }
                        });
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case APPLOVIN_DISCOVERY:
                        AppLovinSdk.initializeSdk(activity);
                        break;
                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.REWARDED_VIDEO,
                                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.BANNER);
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, true);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        StartAppSDK.enableReturnAds(false);
                        break;
                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId);
                        break;
                    case NONE:
                        // do nothing
                        break;
                    default:
                        break;
                }
                Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
            }
        }

        public void initBackupAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                switch (backupAdNetwork) {
                    case ADMOB:
                    case GOOGLE_AD_MANAGER:
                    case FAN_BIDDING_ADMOB:
                    case FAN_BIDDING_AD_MANAGER:
                        MobileAds.initialize(activity, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                if (adapterStatus != null) {
                                    Log.d(TAG,
                                            String.format("Adapter name: %s, Description: %s, Latency: %d",
                                                    adapterClass,
                                                    adapterStatus.getDescription(), adapterStatus.getLatency()));
                                }
                            }
                        });
                        AudienceNetworkInitializeHelper.initialize(activity);
                        break;
                    case FAN:
                    case FACEBOOK:
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case UNITY:
                        UnityAds.initialize(activity, unityGameId, debug, new IUnityAdsInitializationListener() {
                            @Override
                            public void onInitializationComplete() {
                                Log.d(TAG, "Unity Ads Initialization Complete");
                            }

                            @Override
                            public void onInitializationFailed(UnityAds.UnityAdsInitializationError error,
                                    String message) {
                                Log.d(TAG, "Unity Ads Initialization Failed: " + error + " - " + message);
                            }
                        });
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                    case FAN_BIDDING_APPLOVIN_MAX:
                        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case APPLOVIN_DISCOVERY:
                        AppLovinSdk.initializeSdk(activity);
                        break;
                    case IRONSOURCE:
                    case FAN_BIDDING_IRONSOURCE:
                        IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.REWARDED_VIDEO,
                                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.BANNER);
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;
                    case STARTAPP:
                        StartAppSDK.init(activity, startappAppId, true);
                        StartAppSDK.setTestAdsEnabled(debug);
                        StartAppAd.disableSplash();
                        StartAppSDK.enableReturnAds(false);
                        break;
                    case WORTISE:
                        WortiseSdk.initialize(activity, wortiseAppId);
                        break;
                    case NONE:
                        // do nothing
                        break;
                    default:
                        break;
                }
                Log.d(TAG, "[" + backupAdNetwork + "] is selected as Backup Ads");
            }
        }

    }

}
