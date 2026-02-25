package com.partharoypc.adglide.format;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        private final Activity activity;
        private BannerProvider currentProvider;
        private View currentAdView;

        private boolean adStatus = true;
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
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private boolean collapsibleBanner = false;

        public Builder(Activity activity) {
            this.activity = activity;
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
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
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

        public void loadBannerAd() {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Banner Ad load.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    Log.d(TAG, "Banner Ad is enabled");
                    loadAdFromNetwork(adNetwork);
                } else {
                    Log.d(TAG, "Banner Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Banner Ad: " + e.getMessage());
            }
        }

        public void loadBackupBannerAd() {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Backup Banner Ad load.");
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
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Banner Ad: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad) {
            try {
                destroyAndDetachBanner();
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                Log.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT) + "] Banner Ad with ID: "
                        + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    Log.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    loadBackupBannerAd();
                    return;
                }

                BannerProvider provider = BannerProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    currentProvider = provider;
                    provider.loadBanner(activity, adUnitId, this, new BannerProvider.BannerListener() {
                        @Override
                        public void onAdLoaded(View adView) {
                            displayAdView(networkToLoad, adView);
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
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

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adMobBannerId;
                case META:
                    return metaBannerId;
                case UNITY:
                    return unityBannerId;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return appLovinBannerId;
                case IRONSOURCE:
                case META_BIDDING_IRONSOURCE:
                    return ironSourceBannerId;
                case STARTAPP:
                    return !startAppId.isEmpty() ? startAppId : "startapp_id";
                case WORTISE:
                    return wortiseBannerId;
                default:
                    return "";
            }
        }

        private void displayAdView(String network, View adView) {
            activity.runOnUiThread(() -> {
                try {
                    ViewGroup container = getContainerForNetwork(network);
                    if (container != null) {
                        container.removeAllViews();
                        container.addView(adView);
                        container.setVisibility(View.VISIBLE);
                        currentAdView = adView;
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
            return containerId != -1 ? activity.findViewById(containerId) : null;
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
