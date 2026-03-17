package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.RevenueData;
import com.wortise.ads.rewarded.RewardedAd;

public class WortiseRewardedProvider implements RewardedProvider {
    private RewardedAd rewardedAd;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        rewardedAd = new RewardedAd(activity, adUnitId);
        rewardedAd.setListener(new RewardedAd.Listener() {
            public void onRewardedLoaded(@NonNull RewardedAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Rewarded loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            public void onRewardedFailedToLoad(@NonNull RewardedAd ad, @NonNull AdError error) {
                com.partharoypc.adglide.util.PerformanceLogger.error("Wortise", "Rewarded failed: " + error.getMessage());
                listener.onAdFailedToLoad(error.toString());
            }

            public void onRewardedDismissed(@NonNull RewardedAd ad) {
                listener.onAdDismissed();
            }

            public void onRewardedCompleted(@NonNull RewardedAd ad,
                    @NonNull com.wortise.ads.rewarded.models.Reward reward) {
                listener.onAdCompleted();
            }

            public void onRewardedShown(@NonNull RewardedAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Rewarded showed: " + adUnitId);
                listener.onAdShowed();
            }

            public void onRewardedClicked(@NonNull RewardedAd ad) {
            }

            public void onRewardedImpression(@NonNull RewardedAd ad) {
            }

            public void onRewardedFailedToShow(@NonNull RewardedAd ad, @NonNull AdError error) {
                com.partharoypc.adglide.util.PerformanceLogger.error("Wortise", "Rewarded show failed: " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }

            public void onRewardedRevenuePaid(@NonNull RewardedAd ad, @NonNull RevenueData revenueData) {
                // Wortise RevenueData fields not publicly accessible; revenue is tracked via server callbacks.
            }

            // Dummy implementation for older or newer versions
            public void onRewardedAdLoaded(@NonNull RewardedAd ad) {}
            public void onRewardedAdFailedToLoad(@NonNull RewardedAd ad, @NonNull AdError error) {}
            public void onRewardedAdDismissed(@NonNull RewardedAd ad) {}
            public void onRewardedAdCompleted(@NonNull RewardedAd ad) {}
            public void onRewardedAdClicked(@NonNull RewardedAd ad) {}
            public void onRewardedAdImpression(@NonNull RewardedAd ad) {}
            public void onRewardedAdShown(@NonNull RewardedAd ad) {}
        });
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isAvailable()) {
            try {
                rewardedAd.showAd();
            } catch (Exception e) {
                Log.e("AdGlide.Wortise", "Failed to show rewarded: " + e.getMessage());
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("Wortise Rewarded not available");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return rewardedAd != null && rewardedAd.isAvailable();
    }

    @Override
    public void destroy() {
        rewardedAd = null;
    }
}
