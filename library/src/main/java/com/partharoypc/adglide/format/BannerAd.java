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
import com.partharoypc.adglide.util.AdGlideCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.partharoypc.adglide.util.Constant.*;

public class BannerAd {

    private static final String TAG = "AdGlide";

    public static class Builder implements BannerProvider.BannerConfig {

        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private BannerProvider currentProvider;
        private View currentAdView;
        private ViewGroup customContainer;
        private boolean darkTheme = false;
        private boolean collapsibleBanner = false;
        private boolean adaptiveBanner = true;

        public Builder(Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                    com.partharoypc.adglide.util.AdFormat.BANNER);
        }

        @Override
        public boolean isDarkTheme() {
            return darkTheme;
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
            loadBannerAd(null);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback) {
            loadBannerAd(callback);
            return this;
        }


        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
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

        public void loadBannerAd(AdGlideCallback callback) {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity == null) {
                Log.e(TAG, "Cannot load Banner Ad: Activity reference is null.");
                if (callback != null) callback.onAdFailedToLoad("Activity is null");
                return;
            }
            adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                @Override
                public void onAdLoaded(String network) {
                    loadAdFromNetwork(network, callback);
                }

                @Override
                public void onAdFailed(String error) {
                    Log.d(TAG, "Banner load failed: " + error);
                    if (callback != null)
                        callback.onAdFailedToLoad(error);
                }
            });
        }

        public void loadBackupBannerAd(AdGlideCallback callback) {
            adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                @Override
                public void onAdLoaded(String network) {
                    loadAdFromNetwork(network, callback);
                }

                @Override
                public void onAdFailed(String error) {
                    Log.d(TAG, "Banner backup load failed: " + error);
                    if (callback != null)
                        callback.onAdFailedToLoad(error);
                }
            });
        }

        private void loadAdFromNetwork(String networkToLoad, AdGlideCallback callback) {
            try {
                destroyAndDetachBanner();
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                Log.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT) + "] Banner Ad with ID: "
                        + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    Log.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    loadBackupBannerAd(callback);
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
                            if (callback != null)
                                callback.onAdLoaded();
                            displayAdView(networkToLoad, adView, callback);
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("Banner",
                                    "Failed [" + networkToLoad + "]: " + error);
                            Log.e(TAG, "Banner failed to load for " + networkToLoad + ": " + error);
                            if (callback != null)
                                callback.onAdFailedToLoad(error);
                            loadBackupBannerAd(callback);
                        }
                    });
                } else {
                    loadBackupBannerAd(callback);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load banner for " + networkToLoad + ". Error: " + e.getMessage());
                loadBackupBannerAd(callback);
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobBannerId();
                case META -> config.getMetaBannerId();
                case UNITY -> config.getUnityBannerId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinBannerId();
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> config.getIronSourceBannerId();
                case STARTAPP -> !config.getStartAppId().isEmpty() ? config.getStartAppId() : "startapp_id";
                case WORTISE -> config.getWortiseBannerId();
                case HOUSE_AD -> "house_ad";
                default -> "";
            };
        }

        private void displayAdView(String network, View adView, AdGlideCallback callback) {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity == null) {
                Log.e(TAG, "Activity reference is null, cannot display banner.");
                return;
            }
            activity.runOnUiThread(() -> {
                try {
                    ViewGroup container = customContainer != null ? customContainer : getContainerForNetwork(network);
                    if (container != null) {
                        container.removeAllViews();
                        container.addView(adView);
                        container.setVisibility(View.VISIBLE);
                        currentAdView = adView;
                        if (callback != null)
                            callback.onAdShowed();
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
