package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.HOUSE_AD;
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
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.partharoypc.adglide.provider.InterstitialProviderFactory;
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdPoolManager;
import com.partharoypc.adglide.util.WaterfallManager;

public class InterstitialAd {

    public static class Builder extends BaseAdBuilder<Builder> implements InterstitialProvider.InterstitialConfig {
        private InterstitialProvider currentProvider;

        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.INTERSTITIAL);
        }

        @Override
        public boolean isDebug() {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null && config.isDebug();
        }

        @Override
        public boolean isTestMode() {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null && config.isTestMode();
        }

        @Override
        protected void doLoad(AdGlideCallback callback) {
            if (adLoader == null) return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private void loadAdFromNetwork(String networkToLoad, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            try {
                destroyInterstitialAd();
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                AdGlideLog.d(TAG, "Loading [" + networkToLoad.toUpperCase(java.util.Locale.ROOT)
                        + "] Interstitial Ad with ID: " + adUnitId);
                if (adUnitId == null || adUnitId.trim().isEmpty()
                        || (adUnitId.equals("0") && !networkToLoad.equals(STARTAPP))) {
                    AdGlideLog.d(TAG, "Ad unit ID for " + networkToLoad + " is invalid. Trying backup.");
                    resultCallback.onFailure("Invalid Ad Unit ID");
                    return;
                }

                Activity activity = (activityRef != null) ? activityRef.get() : null;
                if (activity == null) {
                    AdGlideLog.e(TAG, "Activity context is missing. Cannot load Interstitial. Falling back to Application context for loader, but match rate may be affected.");
                }

                InterstitialProvider provider = InterstitialProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    currentProvider = provider;
                    currentNetwork = networkToLoad;
                    provider.loadInterstitial(activity, adUnitId, this,

                            new InterstitialProvider.InterstitialListener() {
                                @Override
                                public void onAdLoaded() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Loaded: " + networkToLoad);
                                    AdGlideLog.d(TAG, networkToLoad + " Interstitial Ad loaded");
                                    
                                    if (adLoader != null && adLoader.isTimedOut()) {
                                        AdGlideLog.d(TAG, "Interstitial loaded AFTER timeout. Caching as Late Fill.");
                                        AdPoolManager.cacheLateFill(AdFormat.INTERSTITIAL, networkToLoad, Builder.this);
                                    }
                                    
                                    resultCallback.onSuccess();
                                    if (showOnLoad) {
                                        showOnLoad = false;
                                        doShow(activity, callback);
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(String error) {
                                    com.partharoypc.adglide.util.PerformanceLogger.error("Interstitial",
                                            "Failed [" + networkToLoad + "]: " + error);
                                    AdGlideLog.e(TAG, networkToLoad + " Interstitial Ad failed to load: " + error);
                                    resultCallback.onFailure(error);
                                }

                                @Override
                                public void onAdDismissed() {
                                    AdGlide.notifyAdDismissed("INTERSTITIAL", networkToLoad);
                                    if (callback != null) {
                                        callback.onAdDismissed();
                                    }
                                    // Removed redundant auto-load call to prevent double loading
                                }


                                @Override
                                public void onAdShowFailed(String error) {
                                    AdGlideLog.e(TAG, networkToLoad + " Interstitial Ad failed to show: " + error);
                                    if (callback != null) {
                                        callback.onAdDismissed();
                                    }
                                    // Removed redundant auto-load call to prevent double loading
                                }

                                @Override
                                public void onAdShowed() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Showed: " + networkToLoad);
                                    AdGlideLog.d(TAG, networkToLoad + " Interstitial Ad showed");
                                    AdGlide.notifyAdShowed("INTERSTITIAL", networkToLoad);
                                    if (callback != null) {
                                        callback.onAdShowed();
                                    }
                                }

                                @Override
                                public void onAdClicked() {
                                    AdGlide.notifyAdClicked("INTERSTITIAL", networkToLoad);
                                }

                            });
                } else {
                    AdGlideLog.d(TAG, "No provider found for network: " + networkToLoad + ". Trying backup.");
                    resultCallback.onFailure("No provider found");
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed to load interstitial for " + networkToLoad + ". Error: " + e.getMessage());
                resultCallback.onFailure(e.getMessage());
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.INTERSTITIAL, network) : "0";
        }

        @Override
        protected void doShow(Activity displayActivity, AdGlideCallback callback) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isInterstitialEnabled()) {
                    AdGlideLog.d(TAG, "Interstitial Ad is disabled globally or locally. Calling dismissed listener.");
                    if (callback != null) {
                        callback.onAdDismissed();
                    }
                    return;
                }

                if (currentProvider != null && currentProvider.isAdLoaded()) {
                    currentProvider.showInterstitial(displayActivity,
                            new InterstitialProvider.InterstitialListener() {
                                @Override
                                public void onAdLoaded() {}
                                @Override
                                public void onAdFailedToLoad(String error) {}
                                @Override
                                public void onAdDismissed() {
                                    AdGlide.setAdShowing(false);
                                    if (callback != null) callback.onAdDismissed();
                                    // Removed redundant auto-load call to prevent double loading
                                }
                                @Override
                                public void onAdShowFailed(String error) {
                                    AdGlide.setAdShowing(false);
                                    AdGlideLog.e(TAG, "Interstitial Ad failed to show: " + error);
                                    if (callback != null) callback.onAdDismissed();
                                    // Removed redundant auto-load call to prevent double loading
                                }
                                @Override
                                public void onAdShowed() {
                                    AdGlide.setAdShowing(true);
                                    AdGlide.notifyAdShowed("INTERSTITIAL", currentNetwork);
                                    if (callback != null) callback.onAdShowed();
                                }

                                @Override
                                public void onAdClicked() {
                                    AdGlide.notifyAdClicked("INTERSTITIAL", currentNetwork);
                                }

                            });
                } else {
                    AdGlideLog.d(TAG, "Interstitial ad not loaded. Skipping show and calling dismissed listener.");
                    if (callback != null) callback.onAdDismissed();
                    // Removed redundant auto-load call to prevent double loading
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Error in showInterstitialAd: " + e.getMessage());
                if (callback != null) callback.onAdDismissed();
            }
        }

        public void destroyInterstitialAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }

        public boolean isAdLoaded() {
            return currentProvider != null && currentProvider.isAdLoaded();
        }
    }
}
