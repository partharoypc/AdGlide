package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.partharoypc.adglide.provider.NetworkInitializerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdNetwork {

    public static class Initialize implements NetworkInitializer.InitializerConfig {

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

        @Override
        public String getAppId() {
            AdGlideNetwork network = AdGlideNetwork.fromString(adNetwork);
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adMobAppId;
                case UNITY:
                    return unityGameId;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return appLovinSdkKey;
                case IRONSOURCE:
                case META_BIDDING_IRONSOURCE:
                    return ironSourceAppKey;
                case STARTAPP:
                    return startappAppId;
                case WORTISE:
                    return wortiseAppId;
                default:
                    return "";
            }
        }

        private String getAppIdForNetwork(String networkName) {
            AdGlideNetwork network = AdGlideNetwork.fromString(networkName);
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adMobAppId;
                case UNITY:
                    return unityGameId;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return appLovinSdkKey;
                case IRONSOURCE:
                case META_BIDDING_IRONSOURCE:
                    return ironSourceAppKey;
                case STARTAPP:
                    return startappAppId;
                case WORTISE:
                    return wortiseAppId;
                default:
                    return "";
            }
        }

        @Override
        public boolean isDebug() {
            return debug;
        }

        @Override
        public boolean isTestMode() {
            return testMode;
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
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
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
                if (network != null) {
                    this.backupAdNetworks.add(network.getValue());
                }
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
                AdGlideNetwork primaryNetwork = AdGlideNetwork.fromString(adNetwork);
                if (primaryNetwork == AdGlideNetwork.NONE && !adNetwork.isEmpty()) {
                    Log.w(TAG, "Unknown Primary Ad Network: [" + adNetwork + "]. Skipping initialization.");
                } else {
                    initializeSdk(adNetwork);
                    Log.d(TAG, "[" + adNetwork + "] is selected as Primary Ads");
                }
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
                    AdGlideNetwork backupNetwork = AdGlideNetwork.fromString(network);
                    if (backupNetwork == AdGlideNetwork.NONE && !network.isEmpty()) {
                        Log.w(TAG, "Unknown Backup Ad Network: [" + network + "]. Skipping initialization.");
                        continue;
                    }

                    // Skip if it's the same as the primary network (already initialized)
                    if (network.equals(adNetwork)) {
                        continue;
                    }

                    initializeSdk(network);
                    Log.d(TAG, "[" + network + "] is selected as Backup Ads");
                }
            }
        }

        private void initializeSdk(String network) {
            try {
                NetworkInitializer initializer = NetworkInitializerFactory.getInitializer(network);
                if (initializer != null) {
                    initializer.initialize(context, new NetworkInitializer.InitializerConfig() {
                        @Override
                        public String getAppId() {
                            return getAppIdForNetwork(network);
                        }

                        @Override
                        public boolean isDebug() {
                            return debug;
                        }

                        @Override
                        public boolean isTestMode() {
                            return testMode;
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize " + network + " SDK. Error: " + e.getMessage());
            }
        }

    }

}
