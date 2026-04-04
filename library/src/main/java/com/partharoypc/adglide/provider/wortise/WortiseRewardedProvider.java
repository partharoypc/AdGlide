package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.RevenueData;
import com.wortise.ads.rewarded.RewardedAd;

public class WortiseRewardedProvider implements RewardedProvider {
    private RewardedAd rewardedAd;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed("wortise")) {
            listener.onAdFailedToLoad("Wortise is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        rewardedAd = new RewardedAd(activity, adUnitId);
        rewardedAd.setListener(new RewardedAd.Listener() {
            @Override
            public void onRewardedLoaded(@NonNull RewardedAd ad) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess("wortise", adUnitId);
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Rewarded loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onRewardedFailedToLoad(@NonNull RewardedAd ad, @NonNull AdError error) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("wortise", adUnitId);
                com.partharoypc.adglide.util.PerformanceLogger.error("Wortise", "Rewarded failed: " + error.getMessage());
                listener.onAdFailedToLoad(error.toString());
            }

            @Override
            public void onRewardedDismissed(@NonNull RewardedAd ad) {
                listener.onAdDismissed();
            }

            @Override
            public void onRewardedCompleted(@NonNull RewardedAd ad,
                    @NonNull com.wortise.ads.rewarded.models.Reward reward) {
                listener.onAdCompleted();
            }

            @Override
            public void onRewardedShown(@NonNull RewardedAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Rewarded showed: " + adUnitId);
                listener.onAdShowed();
            }

            @Override
            public void onRewardedClicked(@NonNull RewardedAd ad) {
                listener.onAdClicked();
            }

            @Override
            public void onRewardedImpression(@NonNull RewardedAd ad) {
            }

            @Override
            public void onRewardedFailedToShow(@NonNull RewardedAd ad, @NonNull AdError error) {
                com.partharoypc.adglide.util.PerformanceLogger.error("Wortise", "Rewarded show failed: " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }

            @Override
            public void onRewardedRevenuePaid(@NonNull RewardedAd ad, @NonNull RevenueData revenueData) {
            }
        });
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isAvailable()) {
            try {
                rewardedAd.showAd();
            } catch (Exception e) {
                AdGlideLog.e("AdGlide.Wortise", "Failed to show rewarded: " + e.getMessage());
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
