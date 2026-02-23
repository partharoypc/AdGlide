package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.IRONSOURCE;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.AD_STATUS_ON;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;

import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AdNetwork {

    public static class Initialize {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private String adStatus = "";
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private List<String> backupAdNetworks = new ArrayList<>(); // New: Support for multiple backups
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
            if (!this.backupAdNetworks.contains(backupAdNetwork)) {
                this.backupAdNetworks.add(backupAdNetwork);
            }
            return this;
        }

        public Initialize addBackupAdNetwork(String backupAdNetwork) {
            if (!this.backupAdNetworks.contains(backupAdNetwork)) {
                this.backupAdNetworks.add(backupAdNetwork);
            }
            return this;
        }

        public Initialize setBackupAdNetworks(String... backupAdNetworks) {
            this.backupAdNetworks.clear();
            this.backupAdNetworks.addAll(Arrays.asList(backupAdNetworks));
            if (!this.backupAdNetworks.isEmpty()) {
                this.backupAdNetwork = this.backupAdNetworks.get(0); // Maintain legacy sync
            }
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

        public Initialize setironSourceAppKey(String ironSourceAppKey) {
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
                initializeSdk(adNetwork);
                Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
            }
        }

        public void initBackupAds() {
            if (adStatus.equals(AD_STATUS_ON)) {
                // Initialize legacy single backup if set and list is empty (backward
                // compatibility)
                if (backupAdNetworks.isEmpty() && !backupAdNetwork.isEmpty()) {
                    backupAdNetworks.add(backupAdNetwork);
                }

                for (String network : backupAdNetworks) {
                    initializeSdk(network);
                    Log.d(TAG, "[" + network + "] is selected as Backup Ads");
                }
            }
        }

        private void initializeSdk(String network) {
            try {
                switch (network) {
                    case ADMOB:
                    case META_BIDDING_ADMOB:
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
                    case META:
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
                    case META_BIDDING_APPLOVIN_MAX:
                        AppLovinSdk.getInstance(activity).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(activity).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initializeAd(activity, debug);
                        break;

                    case IRONSOURCE:
                    case META_BIDDING_IRONSOURCE:
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
            } catch (NoClassDefFoundError | Exception e) {
                Log.e(TAG, "Failed to initialize " + network + " SDK. Are you sure you added the dependency? Error: "
                        + e.getMessage());
            }
        }

    }

}
