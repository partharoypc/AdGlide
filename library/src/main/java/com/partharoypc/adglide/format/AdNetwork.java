package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.IRONSOURCE;

import static com.partharoypc.adglide.util.Constant.ADMOB;
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
import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.util.Tools;

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
        private final Context context;
        private final Activity activity;
        private boolean adStatus = true;
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
        private boolean testMode = false;

        public Initialize(Context context) {
            this.context = context;
            this.activity = context instanceof Activity ? (Activity) context : null;
        }

        public Initialize build() {
            initAds();
            initBackupAds();
            return this;
        }

        public Initialize status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        public Initialize network(String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        public Initialize network(AdGlideNetwork network) {
            this.adNetwork = network.getValue();
            return this;
        }

        public Initialize backup(String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            if (!this.backupAdNetworks.contains(backupAdNetwork)) {
                this.backupAdNetworks.add(backupAdNetwork);
            }
            return this;
        }

        public Initialize backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        public Initialize backups(String... backupAdNetworks) {
            this.backupAdNetworks.clear();
            this.backupAdNetworks.addAll(Arrays.asList(backupAdNetworks));
            if (!this.backupAdNetworks.isEmpty()) {
                this.backupAdNetwork = this.backupAdNetworks.get(0);
            }
            return this;
        }

        public Initialize backups(AdGlideNetwork... backupAdNetworks) {
            this.backupAdNetworks.clear();
            for (AdGlideNetwork network : backupAdNetworks) {
                this.backupAdNetworks.add(network.getValue());
            }
            if (!this.backupAdNetworks.isEmpty()) {
                this.backupAdNetwork = this.backupAdNetworks.get(0);
            }
            return this;
        }

        public Initialize adMobId(String adMobAppId) {
            this.adMobAppId = adMobAppId;
            return this;
        }

        public Initialize startAppId(String startappAppId) {
            this.startappAppId = startappAppId;
            return this;
        }

        public Initialize unityId(String unityGameId) {
            this.unityGameId = unityGameId;
            return this;
        }

        public Initialize appLovinId(String appLovinSdkKey) {
            this.appLovinSdkKey = appLovinSdkKey;
            return this;
        }

        public Initialize ironSourceId(String ironSourceAppKey) {
            this.ironSourceAppKey = ironSourceAppKey;
            return this;
        }

        public Initialize wortiseId(String wortiseAppId) {
            this.wortiseAppId = wortiseAppId;
            return this;
        }

        public Initialize debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Initialize testMode(boolean testMode) {
            this.testMode = testMode;
            return this;
        }

        public void initAds() {
            if (adStatus) {
                if (!Tools.isNetworkAvailable(context)) {
                    Log.e(TAG, "Internet connection not available. Skipping Primary Ads initialization.");
                    return;
                }
                initializeSdk(adNetwork);
                Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
            }
        }

        public void initBackupAds() {
            if (adStatus) {
                if (!Tools.isNetworkAvailable(context)) {
                    Log.e(TAG, "Internet connection not available. Skipping Backup Ads initialization.");
                    return;
                }
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
                        MobileAds.initialize(context, initializationStatus -> {
                            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                            for (String adapterClass : statusMap.keySet()) {
                                AdapterStatus adapterStatus = statusMap.get(adapterClass);
                                if (adapterStatus != null) {
                                    Log.d(TAG, String.format("Adapter name: %s, Description: %s, Latency: %d",
                                            adapterClass, adapterStatus.getDescription(), adapterStatus.getLatency()));
                                }
                            }
                        });
                        AudienceNetworkInitializeHelper.initializeAd(context, debug || testMode);
                        break;
                    case META:
                        AudienceNetworkInitializeHelper.initializeAd(context, debug || testMode);
                        break;
                    case UNITY:
                        UnityAds.initialize(context, unityGameId, debug || testMode,
                                new IUnityAdsInitializationListener() {
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
                        AppLovinSdk.getInstance(context).setMediationProvider(AppLovinMediationProvider.MAX);
                        AppLovinSdk.getInstance(context).initializeSdk(config -> {
                        });
                        AudienceNetworkInitializeHelper.initializeAd(context, debug || testMode);
                        break;

                    case IRONSOURCE:
                    case META_BIDDING_IRONSOURCE:
                        if (activity != null) {
                            IronSource.init(activity, ironSourceAppKey, IronSource.AD_UNIT.REWARDED_VIDEO,
                                    IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.BANNER);
                        } else {
                            Log.e(TAG,
                                    "IronSource requires an Activity Context to initialize. Skipping IronSource init.");
                        }
                        AudienceNetworkInitializeHelper.initializeAd(context, debug || testMode);
                        break;
                    case STARTAPP:
                        StartAppSDK.init(context, startappAppId, true);
                        StartAppSDK.setTestAdsEnabled(debug || testMode);
                        StartAppAd.disableSplash();
                        StartAppSDK.enableReturnAds(false);
                        break;
                    case WORTISE:
                        WortiseSdk.initialize(context, wortiseAppId);
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
