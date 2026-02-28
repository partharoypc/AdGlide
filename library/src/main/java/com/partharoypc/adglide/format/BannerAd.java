package com.partharoypc.adglide.format;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;
import com.partharoypc.adglide.provider.BannerProvider;
import com.partharoypc.adglide.provider.BannerProviderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.partharoypc.adglide.util.Constant.*;

public class BannerAd {

    public static class Builder implements BannerProvider.BannerConfig {

        private static final String TAG = "AdGlide";
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private BannerProvider currentProvider;
        private View currentAdView;

        private boolean adStatus = false;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobBannerId = "";
        private String metaBannerId = "";
        private String unityBannerId = "";
        private String appLovinBannerId = "";
        private String ironSourceBannerId = "";
        private String wortiseBannerId = "";
        private String startAppId = "";
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private boolean collapsibleBanner = false;
        private boolean adaptiveBanner = true;
        private ViewGroup customContainer;

        public Builder(Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adStatus = com.partharoypc.adglide.AdGlide.isBannerEnabled();
            if (com.partharoypc.adglide.AdGlide.getConfig() != null) {
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                this.adNetwork = config.getPrimaryNetwork();
                if (!config.getBackupNetworks().isEmpty()) {
                    this.backupAdNetwork = config.getBackupNetworks().get(0);
                    this.waterfallManager = new com.partharoypc.adglide.util.WaterfallManager(
                            config.getBackupNetworks().toArray(new String[0]));
                }
                this.adMobBannerId = config.getAdMobBannerId();
                this.metaBannerId = config.getMetaBannerId();
                this.unityBannerId = config.getUnityBannerId();
                this.appLovinBannerId = config.getAppLovinBannerId();
                this.ironSourceBannerId = config.getIronSourceBannerId();
                this.wortiseBannerId = config.getWortiseBannerId();
                this.startAppId = config.getStartAppId();
                this.legacyGDPR = config.isLegacyGDPR();
            }
        }

        @Override
        public boolean isDarkTheme() {
            return darkTheme;
        }

        @Override
        public boolean isLegacyGDPR() {
            return legacyGDPR;
        }

        @Override
        public boolean isCollapsible() {
            return collapsibleBanner;
        }

        @Override
        public boolean isMrec() {
            return false;
        }

        @Override
        public boolean isAdaptive() {
            return adaptiveBanner;
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder load() {
            loadBannerAd();
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            if (waterfallManager == null) {
                waterfallManager = new WaterfallManager(this.backupAdNetwork);
            } else {
                waterfallManager.getNetworks().add(this.backupAdNetwork);
            }
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
            }
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobBannerId) {
            this.adMobBannerId = adMobBannerId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaBannerId) {
            this.metaBannerId = metaBannerId;
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String unityBannerId) {
            this.unityBannerId = unityBannerId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinBannerId) {
            this.appLovinBannerId = appLovinBannerId;
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String appLovinBannerZoneId) {
            // Zone ID is deprecated in MAX but kept for compatibility
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String ironSourceBannerId) {
            this.ironSourceBannerId = ironSourceBannerId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseBannerId) {
            this.wortiseBannerId = wortiseBannerId;
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String startAppId) {
            this.startAppId = startAppId;
            return this;
        }

        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @NonNull
        public Builder collapsible(boolean collapsibleBanner) {
            this.collapsibleBanner = collapsibleBanner;
            return this;
        }

        @NonNull
        public Builder adaptive(boolean adaptiveBanner) {
            this.adaptiveBanner = adaptiveBanner;
            return this;
        }

        @NonNull
        public Builder container(ViewGroup container) {
            this.customContainer = container;
            return this;
        }

        public void loadBannerAd() {
            try {
                if (!com.partharoypc.adglide.AdGlide.isBannerEnabled() || !adStatus) {
                    Log.d(TAG, "Banner Ad is disabled globally or locally.");
                    return;
                }
                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Banner.");
                    return;
                }
                com.partharoypc.adglide.util.PerformanceLogger.log("Banner", "Loading started: " + adNetwork);
                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    if (com.partharoypc.adglide.AdGlide.getConfig() != null
                            && com.partharoypc.adglide.AdGlide.getConfig().isHouseAdEnabled()) {
                        Log.d(TAG, "Falling back to House Ad due to offline status.");
                        loadAdFromNetwork(HOUSE_AD);
                    }
                    return;
                }
                if (waterfallManager != null) {
                    waterfallManager.reset();
                }
                Log.d(TAG, "Banner Ad is enabled");
                loadAdFromNetwork(adNetwork);
            } catch (Exception e) {
                Log.e(TAG, "Error loading Banner Ad: " + e.getMessage());
            }
        }

        public void loadBackupBannerAd() {
            try {
                if (!com.partharoypc.adglide.AdGlide.isBannerEnabled() || !adStatus) {
                    Log.d(TAG, "Banner Ad is disabled globally or locally. Skipping backup.");
                    return;
                }
                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load backup Banner.");
                    return;
                }
                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    if (com.partharoypc.adglide.AdGlide.getConfig() != null
                            && com.partharoypc.adglide.AdGlide.getConfig().isHouseAdEnabled()) {
                        loadAdFromNetwork(HOUSE_AD);
                    }
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
                    Log.d(TAG, "All backup banner ads failed to load");
                    return;
                }

                Log.d(TAG, "[" + networkToLoad + "] is selected as Backup Ads");
                loadAdFromNetwork(networkToLoad);
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Banner Ad: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad) {
            try {
                destroyAndDetachBanner();
                String adUnitId = getAdUnitIdForNetwork(this, networkToLoad);
                Log.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT) + "] Banner Ad with ID: "
                        + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    Log.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    loadBackupBannerAd();
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    Log.e(TAG, "Activity is null. Cannot load Banner from network.");
                    return;
                }

                BannerProvider provider = BannerProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    currentProvider = provider;
                    provider.loadBanner(activity, adUnitId, this, new BannerProvider.BannerListener() {
                        @Override
                        public void onAdLoaded(View adView) {
                            com.partharoypc.adglide.util.PerformanceLogger.log("Banner",
                                    "Loaded: " + networkToLoad);
                            displayAdView(networkToLoad, adView);
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("Banner",
                                    "Failed [" + networkToLoad + "]: " + error);
                            Log.e(TAG, "Banner failed to load for " + networkToLoad + ": " + error);
                            loadBackupBannerAd();
                        }
                    });
                } else {
                    loadBackupBannerAd();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load banner for " + networkToLoad + ". Error: " + e.getMessage());
                loadBackupBannerAd();
            }
        }

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobBannerId;
                case META -> builder.metaBannerId;
                case UNITY -> builder.unityBannerId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinBannerId;
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> builder.ironSourceBannerId;
                case STARTAPP -> !builder.startAppId.isEmpty() ? builder.startAppId : "startapp_id";
                case WORTISE -> builder.wortiseBannerId;
                case HOUSE_AD -> "house_ad";
                default -> "";
            };
        }

        private void displayAdView(String network, View adView) {
            Activity activity = activityRef.get();
            if (activity == null)
                return;
            activity.runOnUiThread(() -> {
                try {
                    ViewGroup container = customContainer != null ? customContainer : getContainerForNetwork(network);
                    if (container != null) {
                        container.removeAllViews();
                        container.addView(adView);
                        container.setVisibility(View.VISIBLE);
                        currentAdView = adView;
                    } else {
                        Log.e(TAG, "No container found for Banner [" + network
                                + "]. Use .container() or provide default XML ID.");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying ad view: " + e.getMessage());
                }
            });
        }

        private ViewGroup getContainerForNetwork(String network) {
            int containerId = -1;
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                case HOUSE_AD:
                    containerId = R.id.ad_mob_banner_view_container;
                    break;
                case META:
                    containerId = R.id.meta_banner_view_container;
                    break;
                case UNITY:
                    containerId = R.id.unity_banner_view_container;
                    break;
                case IRONSOURCE:
                case META_BIDDING_IRONSOURCE:
                    containerId = R.id.iron_source_banner_view_container;
                    break;
                case STARTAPP:
                    containerId = R.id.start_app_banner_view_container;
                    break;
                case WORTISE:
                    containerId = R.id.wortise_banner_view_container;
                    break;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    containerId = R.id.app_lovin_banner_view_container;
                    break;
            }
            return containerId != -1 && activityRef.get() != null ? activityRef.get().findViewById(containerId) : null;
        }

        public void destroyAndDetachBanner() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
            if (currentAdView != null) {
                View parent = (View) currentAdView.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(currentAdView);
                    parent.setVisibility(View.GONE);
                }
                currentAdView = null;
            }
        }
    }
}
