package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.partharoypc.adglide.provider.RewardedProviderFactory;
import com.partharoypc.adglide.util.AdGlideCallback;
import com.partharoypc.adglide.util.Tools;
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
        private final Activity activity;
        private boolean showOnLoad = false;
        private AdGlideCallback callback;
        private RewardedProvider currentProvider;

        public Builder(Activity activity) {
            this.activity = activity;
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
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

        @NonNull
        public Builder status(boolean adStatus) {
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return this;
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return this;
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return this;
        }

        @NonNull
        public Builder adMobId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            return this;
        }

        public void loadRewardedInterstitialAd(AdGlideCallback callback) {
            loadRewardedAdMain(false, callback);
        }

        public void loadRewardedBackupAd(AdGlideCallback callback) {
            loadRewardedAdMain(true, callback);
        }

        private void loadRewardedAdMain(boolean isBackup, AdGlideCallback callback) {
            if (adLoader == null)
                return;
            if (!isBackup) {
                adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Rewarded interstitial load failed: " + error);
                        if (callback != null) {
                            callback.onAdFailedToLoad(error);
                        }
                        if (showOnLoad && callback != null) {
                            showOnLoad = false;
                            callback.onAdDismissed();
                        }
                    }
                });
            } else {
                adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Rewarded interstitial backup load failed: " + error);
                        if (callback != null) {
                            callback.onAdFailedToLoad(error);
                        }
                        if (showOnLoad && callback != null) {
                            showOnLoad = false;
                            callback.onAdDismissed();
                        }
                    }
                });
            }
        }

        private void loadAdFromNetwork(String network, AdGlideCallback callback) {
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadRewardedBackupAd(callback);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);

            final boolean legacyGDPR = com.partharoypc.adglide.AdGlide.getConfig() != null &&
                    com.partharoypc.adglide.AdGlide.getConfig().isLegacyGDPR();

            RewardedProvider.RewardedConfig config = new RewardedProvider.RewardedConfig() {
                @Override
                public boolean isLegacyGDPR() {
                    return legacyGDPR;
                }

                @Override
                public boolean isInterstitial() {
                    return true;
                }
            };

            provider.loadRewardedAd(activity, adUnitId, config, new RewardedProvider.RewardedListener() {
                @Override
                public void onAdLoaded() {
                    Log.d(TAG, network + " Rewarded interstitial loaded");
                    if (callback != null) {
                        callback.onAdLoaded();
                    }
                    if (showOnLoad) {
                        showOnLoad = false;
                        showRewardedInterstitialAd(activity, callback);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    Log.e(TAG, network + " Rewarded interstitial failed: " + error);
                    loadRewardedBackupAd(callback);
                }

                @Override
                public void onAdDismissed() {
                    if (callback != null)
                        callback.onAdDismissed();
                    loadRewardedInterstitialAd(callback);
                }

                @Override
                public void onAdCompleted() {
                    if (callback != null)
                        callback.onAdCompleted();
                }
            });
        }

        public void showRewardedInterstitialAd(AdGlideCallback callback) {
            showRewardedInterstitialAd(activity, callback);
        }

        public void showRewardedInterstitialAd(Activity displayActivity, AdGlideCallback callback) {
            if (currentProvider != null && currentProvider.isAdAvailable()) {
                currentProvider.showRewardedAd(displayActivity != null ? displayActivity : activity,
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
                                if (callback != null)
                                    callback.onAdCompleted();
                            }
                        });
            } else {
                Log.w(TAG, "No ad available to show.");
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
                case WORTISE -> config.getWortiseRewardedIntId();
                default -> "0";
            };
        }
    }
}
