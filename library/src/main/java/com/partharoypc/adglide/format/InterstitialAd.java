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

import com.partharoypc.adglide.AdGlideConfig;
import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.partharoypc.adglide.provider.InterstitialProviderFactory;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

public class InterstitialAd {

    public static class Builder implements InterstitialProvider.InterstitialConfig {

        private static final String TAG = "AdGlide";
        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private InterstitialProvider currentProvider;
        private boolean showOnLoad = false;
        private AdGlideCallback callback;

        public Builder(@NonNull Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                    com.partharoypc.adglide.util.AdFormat.INTERSTITIAL);
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

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder build(AdGlideCallback callback) {
            return this;
        }

        @NonNull
        public Builder load() {
            loadInterstitialAd(null);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback) {
            loadInterstitialAd(callback);
            return this;
        }

        /**
         * Used internally by AdGlide to request an ad on the fly and show it
         * immediately.
         */
        @NonNull
        public Builder loadAndShow(Activity displayActivity, AdGlideCallback callback) {
            this.showOnLoad = true;
            this.callback = callback;
            loadInterstitialAd(callback);
            return this;
        }

        public void show() {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity != null) {
                showInterstitialAd(activity, null);
            } else {
                AdGlideLog.e(TAG, "Cannot show Interstitial Ad: Activity reference is null.");
            }
        }

        public void show(@NonNull Activity displayActivity) {
            showInterstitialAd(displayActivity, null);
        }

        public void show(AdGlideCallback callback) {
            Activity activity = activityRef != null ? activityRef.get() : null;
            if (activity != null) {
                showInterstitialAd(activity, callback);
            } else {
                AdGlideLog.e(TAG, "Cannot show Interstitial Ad: Activity reference is null.");
                if (callback != null) callback.onAdDismissed();
            }
        }

        public void show(@NonNull Activity displayActivity, AdGlideCallback callback) {
            showInterstitialAd(displayActivity, callback);
        }


        // --- Core Internal Logic ---

        private void loadInterstitialAd(AdGlideCallback callback) {
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

                Activity activity = activityRef.get();
                if (activity == null) {
                    AdGlideLog.e(TAG, "Activity is null. Cannot load Interstitial from network.");
                    resultCallback.onFailure("Activity is null");
                    return;
                }

                InterstitialProvider provider = InterstitialProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    currentProvider = provider;
                    provider.loadInterstitial(activity, adUnitId, this,
                            new InterstitialProvider.InterstitialListener() {
                                @Override
                                public void onAdLoaded() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Loaded: " + networkToLoad);
                                    AdGlideLog.d(TAG, networkToLoad + " Interstitial Ad loaded");
                                    resultCallback.onSuccess();
                                    if (showOnLoad) {
                                        showOnLoad = false;
                                        showInterstitialAd(activity, callback);
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
                                    if (callback != null) {
                                        callback.onAdDismissed();
                                    }
                                    loadInterstitialAd(callback); // Load next ad after dismissal
                                }

                                @Override
                                public void onAdShowFailed(String error) {
                                    AdGlideLog.e(TAG, networkToLoad + " Interstitial Ad failed to show: " + error);
                                    if (callback != null) {
                                        callback.onAdDismissed();
                                    }
                                    loadInterstitialAd(callback); // Load next ad after show failure
                                }

                                @Override
                                public void onAdShowed() {
                                    com.partharoypc.adglide.util.PerformanceLogger.log("Interstitial",
                                            "Showed: " + networkToLoad);
                                    AdGlideLog.d(TAG, networkToLoad + " Interstitial Ad showed");
                                    if (callback != null) {
                                        callback.onAdShowed();
                                    }
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
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobInterstitialId();
                case META -> config.getMetaInterstitialId();
                case UNITY -> config.getUnityInterstitialId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinInterstitialId();
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> config.getIronSourceInterstitialId();
                case STARTAPP -> !config.getStartAppId().isEmpty() ? config.getStartAppId() : "startapp";
                case WORTISE -> config.getWortiseInterstitialId();
                case HOUSE_AD -> "house_ad";
                default -> "";
            };
        }

        public void showInterstitialAd(Activity displayActivity, AdGlideCallback callback) {
            try {
                if (!com.partharoypc.adglide.AdGlide.isInterstitialEnabled()) {
                    AdGlideLog.d(TAG, "Interstitial Ad is disabled globally or locally. Calling dismissed listener.");
                    if (callback != null) {
                        callback.onAdDismissed();
                    }
                    return;
                }

                if (currentProvider != null && currentProvider.isAdLoaded()) {
                    Activity activity = activityRef.get();
                    currentProvider.showInterstitial(displayActivity != null ? displayActivity : activity,
                            new InterstitialProvider.InterstitialListener() {
                                @Override
                                public void onAdLoaded() {}
                                @Override
                                public void onAdFailedToLoad(String error) {}
                                @Override
                                public void onAdDismissed() {
                                    if (callback != null) callback.onAdDismissed();
                                    loadInterstitialAd(callback);
                                }
                                @Override
                                public void onAdShowFailed(String error) {
                                    AdGlideLog.e(TAG, "Interstitial Ad failed to show: " + error);
                                    if (callback != null) callback.onAdDismissed();
                                    loadInterstitialAd(callback);
                                }
                                @Override
                                public void onAdShowed() {
                                    if (callback != null) callback.onAdShowed();
                                }
                            });
                } else {
                    AdGlideLog.d(TAG, "Interstitial ad not loaded. Skipping show and calling dismissed listener.");
                    if (callback != null) callback.onAdDismissed();
                    loadInterstitialAd(callback);
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
