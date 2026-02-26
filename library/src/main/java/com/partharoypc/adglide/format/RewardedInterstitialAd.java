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
import com.partharoypc.adglide.util.OnRewardedAdCompleteListener;
import com.partharoypc.adglide.util.OnRewardedAdDismissedListener;
import com.partharoypc.adglide.util.OnRewardedAdErrorListener;
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
        private static final String TAG = "AdGlide.RewardedInt";
        private final Activity activity;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private String adMobRewardedIntId = "";
        private String appLovinRewardedIntId = "";
        private String wortiseRewardedIntId = "";
        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        private RewardedProvider currentProvider;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedInterstitialAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork != null ? backupAdNetwork : "";
            this.waterfallManager = new WaterfallManager(this.backupAdNetwork);
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks != null && backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String id) {
            this.adMobRewardedIntId = id;
            return this;
        }

        @NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        public void loadRewardedInterstitialAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(false, onComplete, onDismiss);
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(true, onComplete, onDismiss);
        }

        private void loadRewardedAdMain(boolean isBackup, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (!adStatus || placementStatus == 0)
                    return;

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    return;
                }

                String network;
                if (isBackup) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }
                    network = waterfallManager.getNext();
                    if (network == null) {
                        Log.d(TAG, "All backup rewarded interstitial ads failed to load");
                        return;
                    }
                } else {
                    network = adNetwork;
                    if (waterfallManager != null)
                        waterfallManager.reset();
                }

                if (network.equals(NONE)) {
                    loadRewardedBackupAd(onComplete, onDismiss);
                    return;
                }

                loadAdFromNetwork(network, onComplete, onDismiss);

            } catch (Exception e) {
                Log.e(TAG, "Error in loadRewardedAdMain: " + e.getMessage());
                if (!isBackup)
                    loadRewardedBackupAd(onComplete, onDismiss);
            }
        }

        private void loadAdFromNetwork(String network, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadRewardedBackupAd(onComplete, onDismiss);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(this, network);

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
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    Log.e(TAG, network + " Rewarded interstitial failed: " + error);
                    loadRewardedBackupAd(onComplete, onDismiss);
                }

                @Override
                public void onAdDismissed() {
                    if (onDismiss != null)
                        onDismiss.onRewardedAdDismissed();
                    loadRewardedInterstitialAd(onComplete, onDismiss);
                }

                @Override
                public void onAdCompleted() {
                    if (onComplete != null)
                        onComplete.onRewardedAdComplete();
                }
            });
        }

        public void showRewardedInterstitialAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            showRewardedInterstitialAd(activity, onComplete, onDismiss, onError);
        }

        public void showRewardedInterstitialAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdErrorListener onError) {
            if (currentProvider != null && currentProvider.isAdAvailable()) {
                currentProvider.showRewardedAd(displayActivity != null ? displayActivity : activity,
                        new RewardedProvider.RewardedListener() {
                            @Override
                            public void onAdLoaded() {
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                if (onError != null)
                                    onError.onRewardedAdError();
                            }

                            @Override
                            public void onAdDismissed() {
                                if (onDismiss != null)
                                    onDismiss.onRewardedAdDismissed();
                            }

                            @Override
                            public void onAdCompleted() {
                                if (onComplete != null)
                                    onComplete.onRewardedAdComplete();
                            }
                        });
            } else {
                Log.w(TAG, "No ad available to show.");
                if (onError != null)
                    onError.onRewardedAdError();
            }
        }

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobRewardedIntId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinRewardedIntId;
                case WORTISE -> builder.wortiseRewardedIntId;
                default -> "0";
            };
        }
    }
}
