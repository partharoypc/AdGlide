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
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

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

    public static class Builder extends BaseAdBuilder<Builder> implements BannerProvider.BannerConfig, DefaultLifecycleObserver {

        private BannerProvider currentProvider;
        private View currentAdView;
        private ViewGroup customContainer;
        private boolean darkTheme = false;
        private boolean isAdaptive = true;

        
        private int autoRefreshSeconds = 0;
        private android.os.Handler refreshHandler;
        private Runnable refreshRunnable;
        private boolean isPaused = false;

        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.BANNER);
            if (context instanceof LifecycleOwner) {
                ((LifecycleOwner) context).getLifecycle().addObserver(this);
            } else {
                Activity activity = getActivity();
                if (activity instanceof LifecycleOwner) {
                    ((LifecycleOwner) activity).getLifecycle().addObserver(this);
                }
            }
        }

        @Override
        public boolean isDarkTheme() {
            return darkTheme;
        }

        @Override
        public boolean isMrec() {
            return false;
        }

        @Override
        public boolean isAdaptive() {
            return isAdaptive;
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            if (isPaused) {
                isPaused = false;
                AdGlideLog.d(TAG, "Banner Auto-Refresh resumed (Activity started)");
                scheduleAutoRefresh();
            }
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            isPaused = true;
            AdGlideLog.d(TAG, "Banner Auto-Refresh paused (Activity stopped/background)");
            stopAutoRefresh();
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            AdGlideLog.d(TAG, "Banner Builder lifecycle destroyed. Cleaning up.");
            destroyAndDetachBanner();
            owner.getLifecycle().removeObserver(this);
        }


        @Override
        protected void doLoad(AdGlideCallback callback) {
            this.callback = callback;
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                stopAutoRefresh();
                return;
            }
            if (adLoader == null) return;
            showShimmer(callback);
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, new AdGlideCallback() {
                @Override
                public void onAdLoaded() {
                    // Shimmer removal is handled inside displayAdView which gets triggered on success
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    hideShimmer(currentNetwork);
                    if (callback != null) callback.onAdFailedToLoad(error);
                }
            });
        }

        @Override
        protected void doShow(Activity activity, AdGlideCallback callback) {
            displayAdView(currentNetwork, currentAdView, callback);
        }


        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder adaptive(boolean isAdaptive) {
            this.isAdaptive = isAdaptive;
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



        private void loadAdFromNetwork(String networkToLoad, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            try {
                this.currentNetwork = networkToLoad;
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                AdGlideLog.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT) + "] Banner Ad with ID: "
                        + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    AdGlideLog.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    resultCallback.onFailure("Invalid Ad Unit ID");
                    return;
                }

                Activity activity = getActivity();
                if (activity == null) {
                    AdGlideLog.e(TAG, "Activity context is missing. Cannot load Banner from network. Falling back to Application context for loader, but match rate may be affected.");
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
                refreshRunnable = () -> doLoad(null);
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
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.BANNER, network) : "0";
        }

        private void displayAdView(String network, View adView, AdGlideCallback callback) {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) {
                AdGlideLog.e(TAG, "Activity is null or finishing, cannot display banner.");
                return;
            }
            activity.runOnUiThread(() -> {
                try {
                    ViewGroup container = customContainer != null ? customContainer : getContainerForNetwork(network);
                    if (container != null) {
                        // Cleanup: Stop animations before swapping
                        com.partharoypc.adglide.helper.ShimmerHelper.stopShimmer(container);

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

        private void showShimmer(AdGlideCallback callback) {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) return;
            
            // For banners, we use the primary network to find the default container if custom is null
            String primary = currentNetwork != null ? currentNetwork : (AdGlide.getConfig() != null ? AdGlide.getConfig().getPrimaryNetwork() : com.partharoypc.adglide.util.Constant.ADMOB);
            ViewGroup container = customContainer != null ? customContainer : getContainerForNetwork(primary);
            
            if (container != null) {
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing()) return;
                    container.removeAllViews();
                    View shimmer = activity.getLayoutInflater().inflate(R.layout.adglide_shimmer_banner, container, false);
                    container.addView(shimmer);
                    container.setVisibility(View.VISIBLE);
                    com.partharoypc.adglide.helper.ShimmerHelper.startShimmer(shimmer);
                });
            }
        }

        private void hideShimmer(String network) {
            Activity activity = getActivity();
            if (activity == null) return;
            activity.runOnUiThread(() -> {
                ViewGroup container = customContainer != null ? customContainer : getContainerForNetwork(network);
                if (container != null) {
                    com.partharoypc.adglide.helper.ShimmerHelper.stopShimmer(container);
                    container.removeAllViews();
                    container.setVisibility(View.GONE);
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
            Activity activity = getActivity();
            return containerId != -1 && activity != null ? activity.findViewById(containerId) : null;
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
