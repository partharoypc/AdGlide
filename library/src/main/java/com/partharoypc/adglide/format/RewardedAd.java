package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.AdGlideCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.partharoypc.adglide.provider.RewardedProviderFactory;
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdPoolManager;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying rewarded ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class RewardedAd {

    private static final String TAG = "AdGlide";

    public static class Builder extends BaseAdBuilder<Builder> {
        private RewardedProvider currentProvider;

        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.REWARDED);
        }

        @Override
        protected void doLoad(AdGlideCallback callback) {
            if (adLoader == null) return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private void loadAdFromNetwork(String network, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            Activity activity = (activityRef != null) ? activityRef.get() : null;
            if (activity == null) {
                AdGlideLog.e(TAG, "Activity context is missing. Cannot load Rewarded from network. Falling back to Application context for loader, but match rate may be affected.");
            }

            destroy();
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                AdGlideLog.w(TAG, "No provider available for " + network + ". Loading backup.");
                resultCallback.onFailure("No provider available");
                return;
            }

            this.currentProvider = provider;
            this.currentNetwork = network;
            String adUnitId = getAdUnitIdForNetwork(network);

            AdGlideLog.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Rewarded Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                AdGlideLog.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                resultCallback.onFailure("Invalid Ad Unit ID");
                return;
            }


            RewardedProvider.RewardedConfig config = new RewardedProvider.RewardedConfig() {
                @Override
                public boolean isInterstitial() {
                    return false;
                }
            };

            provider.loadRewardedAd(activity, adUnitId, config, new RewardedProvider.RewardedListener() {
                @Override
                public void onAdLoaded() {
                    com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded", "Loaded: " + network);
                    AdGlideLog.d(TAG, network + " Rewarded ad loaded");
                    
                    if (adLoader != null && adLoader.isTimedOut()) {
                        AdGlideLog.d(TAG, "Rewarded ad loaded AFTER timeout. Caching as Late Fill.");
                        AdPoolManager.cacheLateFill(AdFormat.REWARDED, network, RewardedAd.Builder.this);
                    }
                    
                    resultCallback.onSuccess();
                    if (showOnLoad) {
                        showOnLoad = false;
                        doShow(activity, callback);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    AdGlideLog.e(TAG, network + " Rewarded ad failed to load: " + error);
                    resultCallback.onFailure(error);
                }

                @Override
                public void onAdDismissed() {
                    Activity activity = activityRef.get();
                    AdGlide.notifyAdDismissed("REWARDED", network);
                    if (callback != null)
                        callback.onAdDismissed();
                    // Removed redundant auto-load call to prevent double loading
                }


                @Override
                public void onAdCompleted() {
                    AdGlide.notifyAdCompleted("REWARDED", network);
                    if (callback != null)
                        callback.onAdCompleted();
                }

                @Override
                public void onAdShowed() {
                    AdGlide.notifyAdShowed("REWARDED", network);
                }

                @Override
                public void onAdClicked() {
                    AdGlide.notifyAdClicked("REWARDED", network);
                }

            });
        }


        @Override
        protected void doShow(Activity displayActivity, AdGlideCallback callback) {
            try {
                if (currentProvider != null && currentProvider.isAdAvailable()) {
                    currentProvider.showRewardedAd(displayActivity,
                            new RewardedProvider.RewardedListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdFailedToLoad(String error) {
                                    if (callback != null)
                                        callback.onAdFailedToLoad(error);
                                }

                                @Override
                                public void onAdDismissed() {
                                    AdGlide.setAdShowing(false);
                                    AdGlide.notifyAdDismissed("REWARDED", currentNetwork);
                                    if (callback != null)
                                        callback.onAdDismissed();
                                }

                                @Override
                                public void onAdShowFailed(String error) {
                                    AdGlide.setAdShowing(false);
                                    AdGlideLog.e(TAG, "Rewarded Ad failed to show: " + error);
                                    if (callback != null)
                                        callback.onAdDismissed();
                                }

                                @Override
                                public void onAdCompleted() {
                                    AdGlide.notifyAdCompleted("REWARDED", currentNetwork);
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Rewarded",
                                            "Completed: "
                                                    + (currentProvider != null ? currentProvider.getClass().getSimpleName()
                                                            : "Unknown"));
                                    if (callback != null)
                                        callback.onAdCompleted();
                                }

                                @Override
                                public void onAdShowed() {
                                    AdGlide.setAdShowing(true);
                                    AdGlide.notifyAdShowed("REWARDED", currentNetwork);
                                    if (callback != null) callback.onAdShowed();
                                }

                                @Override
                                public void onAdClicked() {
                                    AdGlide.notifyAdClicked("REWARDED", currentNetwork);
                                }

                            });
                } else {
                    AdGlideLog.w(TAG, "Rewarded ad not loaded. Skipping show.");
                    if (callback != null)
                        callback.onAdDismissed();
                    // Removed redundant auto-load call to prevent double loading
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Error in showRewardedAd: " + e.getMessage());
                if (callback != null) callback.onAdDismissed();
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.REWARDED, network) : "0";
        }

        public void destroy() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }

        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
