package com.partharoypc.adglide.format;

import android.app.Activity;
import android.content.Context;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.util.AdGlideLog;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.provider.NetworkInitializer;
import com.partharoypc.adglide.provider.NetworkInitializerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.partharoypc.adglide.util.Constant.*;

public class AdNetwork {

    public static class Initialize implements NetworkInitializer.InitializerConfig {

        private static final String TAG = "AdGlide";
        private final Context context;
        private AdGlideConfig config;

        public Initialize(@NonNull Context context) {
            this.context = context;
        }

        public Initialize config(AdGlideConfig config) {
            this.config = config;
            return this;
        }

        private String currentInitializingNetwork;

        @Override
        public String getAppId() {
            if (config == null || currentInitializingNetwork == null) return "";
            return getAppIdForNetwork(currentInitializingNetwork);
        }

        private String getAppIdForNetwork(String networkName) {
            if (config == null || networkName == null) return "";
            return switch (networkName) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobAppId();
                case UNITY -> config.getUnityGameId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinSdkKey();
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> config.getIronSourceAppKey();
                case STARTAPP -> config.getStartAppId();
                case WORTISE -> config.getWortiseAppId();
                case META -> ""; 
                default -> "";
            };
        }

        @Override
        public boolean isDebug() {
            return config != null && config.isDebug();
        }

        @Override
        public boolean isTestMode() {
            return config != null && config.isTestMode();
        }

        public Initialize build() {
            if (config == null || context == null) {
                AdGlideLog.e(TAG, "AdGlide not initialized: config or context is null.");
                return this;
            }
            initAds();
            initBackupAds();
            return this;
        }

        public void initAds() {
            if (config.getAdStatus()) {
                if (!Tools.isNetworkAvailable(context)) {
                    AdGlideLog.e(TAG, "Internet connection not available. Skipping Primary Ads initialization.");
                    return;
                }
                String adNetwork = config.getPrimaryNetwork();
                AdGlideNetwork primaryNetwork = AdGlideNetwork.fromString(adNetwork);
                if (primaryNetwork == AdGlideNetwork.NONE && !adNetwork.isEmpty()) {
                    AdGlideLog.w(TAG, "Unknown Primary Ad Network: [" + adNetwork + "]. Skipping initialization.");
                } else {
                    initializeSdk(adNetwork);
                }
            }
        }

        public void initBackupAds() {
            if (config.getAdStatus()) {
                if (!Tools.isNetworkAvailable(context)) {
                    AdGlideLog.e(TAG, "Internet connection not available. Skipping Backup Ads initialization.");
                    return;
                }

                List<String> backupAdNetworks = config.getBackupNetworks();
                String primaryNetwork = config.getPrimaryNetwork();

                for (String network : backupAdNetworks) {
                    if (network.contains("bidding") && !network.equals(primaryNetwork)) {
                        initializeSdk(network);
                    }
                }

                for (String network : backupAdNetworks) {
                    if (network.contains("bidding") || network.equals(primaryNetwork)) {
                        continue;
                    }

                    AdGlideNetwork backupNetwork = AdGlideNetwork.fromString(network);
                    if (backupNetwork == AdGlideNetwork.NONE && !network.isEmpty()) {
                        AdGlideLog.w(TAG, "Unknown Backup Ad Network: [" + network + "]. Skipping initialization.");
                        continue;
                    }

                    initializeSdk(network);
                }
            }
        }

        private void initializeSdk(String networkName) {
            if (context == null || networkName == null || networkName.isEmpty()) return;
            this.currentInitializingNetwork = networkName;
            try {
                NetworkInitializer initializer = NetworkInitializerFactory.getInitializer(networkName);
                if (initializer != null) {
                    initializer.initialize(context, this);
                    AdGlideLog.d(TAG, "[" + networkName.toUpperCase(java.util.Locale.ROOT) + "] SDK initialized successfully.");
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed to initialize " + networkName + " SDK. Error: " + e.getMessage());
            }
        }

    }

}
