package com.partharoypc.adglide.format;

import android.app.Activity;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.util.AdGlideLog;
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

        
        private int autoRefreshSeconds = 0;
        private android.os.Handler refreshHandler;
        private Runnable refreshRunnable;

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
        public boolean isMrec() {
            return false;
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
        public Builder container(ViewGroup container) {
            this.customContainer = container;
            return this;
        }
        
        @NonNull
        public Builder autoRefresh(int seconds) {
            this.autoRefreshSeconds = seconds;
            return this;
        }

        public void loadBannerAd(AdGlideCallback callback) {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity == null || activity.isFinishing()) {
                stopAutoRefresh();
                return;
            }
            if (adLoader == null) return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private void loadAdFromNetwork(String networkToLoad, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            try {
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                AdGlideLog.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT) + "] Banner Ad with ID: "
                        + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    AdGlideLog.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    resultCallback.onFailure("Invalid Ad Unit ID");
                    return;
                }

                Activity activity = activityRef.get();
                if (activity == null) {
                    AdGlideLog.e(TAG, "Activity is null. Cannot load Banner from network.");
                    resultCallback.onFailure("Activity is null");
                    return;
                }

                BannerProvider provider = BannerProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    provider.loadBanner(activity, adUnitId, this, new BannerProvider.BannerListener() {
                        @Override
                        public void onAdLoaded(View adView) {
                            com.partharoypc.adglide.util.PerformanceLogger.log("Banner",
                                    "Loaded: " + networkToLoad);
                            resultCallback.onSuccess();
                            if (callback != null)
                                callback.onAdLoaded();
                            
                            if (currentProvider != null && currentProvider != provider) {
                                currentProvider.destroy();
                            }
                            currentProvider = provider;
                            displayAdView(networkToLoad, adView, callback);
                            AdGlide.notifyAdShowed("BANNER", networkToLoad);
                            scheduleAutoRefresh();
                        }

                        @Override
                        public void onAdClicked() {
                            AdGlide.notifyAdClicked("BANNER", networkToLoad);
                        }


                        @Override
                        public void onAdFailedToLoad(String error) {
                            com.partharoypc.adglide.util.PerformanceLogger.error("Banner",
                                    "Failed [" + networkToLoad + "]: " + error);
                            AdGlideLog.e(TAG, "Banner failed to load for " + networkToLoad + ": " + error);
                            
                            resultCallback.onFailure(error);
                            scheduleAutoRefresh();
                        }
                    });
                } else {
                    resultCallback.onFailure("Provider is null");
                    scheduleAutoRefresh();
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed to load banner for " + networkToLoad + ". Error: " + e.getMessage());
                resultCallback.onFailure(e.getMessage());
                scheduleAutoRefresh();
            }
        }

        private void scheduleAutoRefresh() {
            if (autoRefreshSeconds <= 0) return;
            if (refreshHandler == null) {
                refreshHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                refreshRunnable = () -> loadBannerAd(null);
            }
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshHandler.postDelayed(refreshRunnable, autoRefreshSeconds * 1000L);
        }

        private void stopAutoRefresh() {
            if (refreshHandler != null && refreshRunnable != null) {
                refreshHandler.removeCallbacks(refreshRunnable);
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
                AdGlideLog.e(TAG, "Activity reference is null, cannot display banner.");
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
                        AdGlideLog.e(TAG, "No container found for Banner [" + network
                                + "]. Use .container() or provide default XML ID.");
                    }
                } catch (Exception e) {
                    AdGlideLog.e(TAG, "Error displaying ad view: " + e.getMessage());
                }
            });
        }

        private ViewGroup getContainerForNetwork(String network) {
            int containerId = -1;
            containerId = switch (network) {
                case ADMOB, META_BIDDING_ADMOB, HOUSE_AD -> R.id.ad_mob_banner_view_container;
                case META -> R.id.meta_banner_view_container;
                case UNITY -> R.id.unity_banner_view_container;
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> R.id.iron_source_banner_view_container;
                case STARTAPP -> R.id.start_app_banner_view_container;
                case WORTISE -> R.id.wortise_banner_view_container;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX ->
                        R.id.app_lovin_banner_view_container;
                default -> containerId;
            };
            return containerId != -1 && activityRef.get() != null ? activityRef.get().findViewById(containerId) : null;
        }

        public void destroyAndDetachBanner() {
            stopAutoRefresh();
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
