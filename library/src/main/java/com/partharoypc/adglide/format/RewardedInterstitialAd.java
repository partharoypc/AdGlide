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

    public static class Builder extends BaseAdBuilder<Builder> {
        private static final String TAG = "AdGlide";
        private RewardedProvider currentProvider;

        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL);
        }

        @Override
        protected void doLoad(AdGlideCallback callback) {
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
                    }
                    
                    resultCallback.onSuccess();
                    if (showOnLoad) {
                        showOnLoad = false;
                        doShow(activity, callback);
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


        @Override
        protected void doShow(Activity displayActivity, AdGlideCallback callback) {
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
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.REWARDED_INTERSTITIAL, network) : "0";
        }

        public boolean isAdAvailable() {
            return currentProvider != null && currentProvider.isAdAvailable();
        }
    }
}
