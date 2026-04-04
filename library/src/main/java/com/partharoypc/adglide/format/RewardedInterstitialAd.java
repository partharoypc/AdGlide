package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;

import com.partharoypc.adglide.AdGlide;
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
 * Handles loading and displaying rewarded interstitial ads.
 * Currently primarily used by AdMob, but architected for modularity.
 */
public class RewardedInterstitialAd {

    public static class Builder {
        private static final String TAG = "AdGlide";
        private final com.partharoypc.adglide.util.AdLoader adLoader;
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private boolean showOnLoad = false;
        private AdGlideCallback callback;
        private RewardedProvider currentProvider;
        private String currentNetwork;

        public Activity getActivity() {
            return activityRef != null ? activityRef.get() : null;
        }


        public Builder(@NonNull android.content.Context context) {
            if (context instanceof Activity) {
                this.activityRef = new java.lang.ref.WeakReference<>((Activity) context);
            } else {
                this.activityRef = null;
            }
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(context,
                    com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL);
        }

        @NonNull
        public Builder build(AdGlideCallback callback) {
            loadRewardedInterstitialAd(callback);
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
            loadRewardedInterstitialAd(callback);
            return this;
        }

        public void loadRewardedInterstitialAd(AdGlideCallback callback) {
            if (adLoader == null) return;
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, callback);
        }

        private void loadAdFromNetwork(String network, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                AdGlideLog.w(TAG, "No provider available for " + network + ". Loading backup.");
                resultCallback.onFailure("No provider available");
                return;
            }

            this.currentProvider = provider;
            this.currentNetwork = network;
            String adUnitId = getAdUnitIdForNetwork(network);



            RewardedProvider.RewardedConfig config = new RewardedProvider.RewardedConfig() {
                @Override
                public boolean isInterstitial() {
                    return true;
                }
            };

            Activity activity = (activityRef != null) ? activityRef.get() : null;
            if (activity == null) {
                AdGlideLog.e(TAG, "Activity context is missing. Cannot load Rewarded Interstitial from network. Falling back to Application context for loader, but match rate may be affected.");
            }

            provider.loadRewardedAd(activity, adUnitId, config, new RewardedProvider.RewardedListener() {
                @Override
                public void onAdLoaded() {
                    AdGlideLog.d(TAG, network + " Rewarded interstitial loaded");
                    
                    if (adLoader != null && adLoader.isTimedOut()) {
                        AdGlideLog.d(TAG, "Rewarded Interstitial loaded AFTER timeout. Caching as Late Fill.");
                        AdPoolManager.cacheLateFill(AdFormat.REWARDED_INTERSTITIAL, network, RewardedInterstitialAd.Builder.this);
                        return;
                    }
                    
                    resultCallback.onSuccess();
                    if (showOnLoad) {
                        showOnLoad = false;
                        showRewardedInterstitialAd(activity, callback);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    AdGlideLog.e(TAG, network + " Rewarded interstitial failed: " + error);
                    resultCallback.onFailure(error);
                }

                @Override
                public void onAdDismissed() {
                    AdGlide.notifyAdDismissed("REWARDED_INTERSTITIAL", network);
                    if (callback != null)
                        callback.onAdDismissed();
                    // Removed redundant auto-load call to prevent double loading
                }


                @Override
                public void onAdCompleted() {
                    AdGlide.notifyAdCompleted("REWARDED_INTERSTITIAL", network);
                    if (callback != null)
                        callback.onAdCompleted();
                }

                @Override
                public void onAdShowed() {
                    AdGlide.notifyAdShowed("REWARDED_INTERSTITIAL", network);
                }

                @Override
                public void onAdClicked() {
                    AdGlide.notifyAdClicked("REWARDED_INTERSTITIAL", network);
                }

            });
        }

        public void showRewardedInterstitialAd(AdGlideCallback callback) {
            Activity activity = activityRef.get();
            if (activity != null) {
                showRewardedInterstitialAd(activity, callback);
            }
        }

        public void showRewardedInterstitialAd(Activity displayActivity, AdGlideCallback callback) {
            if (currentProvider != null && currentProvider.isAdAvailable()) {
                Activity defaultActivity = activityRef.get();
                currentProvider.showRewardedAd(displayActivity != null ? displayActivity : defaultActivity,
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
                                if (callback != null)
                                    callback.onAdDismissed();
                            }

                            @Override
                            public void onAdCompleted() {
                                AdGlide.notifyAdCompleted("REWARDED_INTERSTITIAL", currentNetwork);
                                if (callback != null)
                                    callback.onAdCompleted();
                            }

                            @Override
                            public void onAdShowed() {
                                AdGlide.notifyAdShowed("REWARDED_INTERSTITIAL", currentNetwork);
                            }

                            @Override
                            public void onAdClicked() {
                                AdGlide.notifyAdClicked("REWARDED_INTERSTITIAL", currentNetwork);
                            }

                        });
            } else {
                AdGlideLog.w(TAG, "No ad available to show.");
                if (callback != null)
                    callback.onAdFailedToLoad("No ad available");
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "0";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobRewardedIntId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinRewardedIntId();
                case com.partharoypc.adglide.util.Constant.UNITY -> config.getUnityRewardedIntId();
                case com.partharoypc.adglide.util.Constant.IRONSOURCE, com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE -> config.getIronSourceRewardedIntId();
                case WORTISE -> config.getWortiseRewardedIntId();
                default -> "0";
            };
        }

        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
