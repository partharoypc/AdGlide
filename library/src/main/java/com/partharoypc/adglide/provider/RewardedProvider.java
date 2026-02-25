package com.partharoypc.adglide.provider;

import android.app.Activity;

public interface RewardedProvider {
    void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener);

    void showRewardedAd(Activity activity, RewardedListener listener);

    boolean isAdAvailable();

    void destroy();

    interface RewardedConfig {
        boolean isLegacyGDPR();

        boolean isInterstitial();
    }

    interface RewardedListener {
        void onAdLoaded();

        void onAdFailedToLoad(String error);

        void onAdDismissed();

        void onAdCompleted();

        default void onAdShowFailed(String error) {
            // Default no-op: override to handle show failures
        }
    }
}
