package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_DISCOVERY;
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
import com.partharoypc.adglide.util.OnRewardedAdLoadedListener;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying rewarded ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class RewardedAd {

    public static class Builder {
        private static final String TAG = "AdGlide.Rewarded";
        private final Activity activity;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private final Map<String, String> adUnitIds = new HashMap<>();
        private int placementStatus = 1;
        private boolean legacyGDPR = false;

        private RewardedProvider currentProvider;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder build(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder build(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder show(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(activity, onComplete, onDismiss, onError);
            return this;
        }

        @NonNull
        public Builder show(@NonNull Activity displayActivity, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(displayActivity, onComplete, onDismiss, onError);
            return this;
        }

        @NonNull
        public Builder load() {
            loadRewardedAd(null, null);
            return this;
        }

        @NonNull
        public Builder load(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder load(OnRewardedAdLoadedListener onLoaded, OnRewardedAdErrorListener onError,
                OnRewardedAdDismissedListener onDismiss, OnRewardedAdCompleteListener onComplete) {
            loadRewardedAd(onComplete, onDismiss);
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @NonNull
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            this.waterfallManager = new WaterfallManager(this.backupAdNetwork);
            return this;
        }

        @NonNull
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @NonNull
        public Builder backups(@Nullable String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks != null && backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
            }
            return this;
        }

        @NonNull
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String id) {
            adUnitIds.put("admob", id);
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String id) {
            adUnitIds.put("meta", id);
            return this;
        }

        @NonNull
        public Builder unityId(@NonNull String id) {
            adUnitIds.put("unity", id);
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String id) {
            adUnitIds.put("applovin", id);
            adUnitIds.put("applovin_max", id);
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String id) {
            adUnitIds.put("applovin_discovery", id);
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String id) {
            adUnitIds.put("ironsource", id);
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String id) {
            adUnitIds.put("startapp", id);
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String id) {
            adUnitIds.put("wortise", id);
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

        public void loadRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(false, onComplete, onDismiss);
        }

        public void loadRewardedBackupAd(OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            loadRewardedAdMain(true, onComplete, onDismiss);
        }

        private void loadRewardedAdMain(boolean isBackup, OnRewardedAdCompleteListener onComplete,
                OnRewardedAdDismissedListener onDismiss) {
            try {
                if (!adStatus || placementStatus == 0) {
                    Log.d(TAG, "Rewarded Ad is disabled or placement status is 0.");
                    return;
                }

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
                            Log.d(TAG, "No backup ad network configured.");
                            return;
                        }
                    }
                    network = waterfallManager.getNext();
                    if (network == null) {
                        Log.d(TAG, "All backup rewarded ads failed to load");
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
            destroy();
            RewardedProvider provider = RewardedProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadRewardedBackupAd(onComplete, onDismiss);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            Log.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Rewarded Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                loadRewardedBackupAd(onComplete, onDismiss);
                return;
            }

            RewardedProvider.RewardedConfig config = new RewardedProvider.RewardedConfig() {
                @Override
                public boolean isLegacyGDPR() {
                    return legacyGDPR;
                }

                @Override
                public boolean isInterstitial() {
                    return false;
                }
            };

            provider.loadRewardedAd(activity, adUnitId, config, new RewardedProvider.RewardedListener() {
                @Override
                public void onAdLoaded() {
                    Log.d(TAG, network + " Rewarded ad loaded");
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    Log.e(TAG, network + " Rewarded ad failed to load: " + error);
                    loadRewardedBackupAd(onComplete, onDismiss);
                }

                @Override
                public void onAdDismissed() {
                    if (onDismiss != null)
                        onDismiss.onRewardedAdDismissed();
                    loadRewardedAd(onComplete, onDismiss);
                }

                @Override
                public void onAdCompleted() {
                    if (onComplete != null)
                        onComplete.onRewardedAdComplete();
                }
            });
        }

        public void showRewardedAd(OnRewardedAdCompleteListener onComplete, OnRewardedAdDismissedListener onDismiss,
                OnRewardedAdErrorListener onError) {
            showRewardedAd(activity, onComplete, onDismiss, onError);
        }

        public void showRewardedAd(Activity displayActivity, OnRewardedAdCompleteListener onComplete,
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

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adUnitIds.get(ADMOB);
                case META:
                    return adUnitIds.get(META);
                case UNITY:
                    return adUnitIds.get(UNITY);
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return adUnitIds.get(APPLOVIN_MAX);
                case APPLOVIN_DISCOVERY:
                    return adUnitIds.get(APPLOVIN_DISCOVERY);
                case IRONSOURCE:
                case META_BIDDING_IRONSOURCE:
                    return adUnitIds.get(IRONSOURCE);
                case STARTAPP:
                    return adUnitIds.get(STARTAPP) != null ? adUnitIds.get(STARTAPP) : "startapp_id";
                case WORTISE:
                    return adUnitIds.get(WORTISE);
                default:
                    return "0";
            }
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
