package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.rewarded.RewardedAd;

public class WortiseRewardedProvider implements RewardedProvider {
    private RewardedAd rewardedAd;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        rewardedAd = new RewardedAd(activity, adUnitId);
        rewardedAd.setListener(new RewardedAd.Listener() {
            public void onRewardedLoaded(@NonNull RewardedAd ad) {
                listener.onAdLoaded();
            }

            public void onRewardedFailedToLoad(@NonNull RewardedAd ad, @NonNull AdError error) {
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
                // Triggered when ad is shown.
            }

            public void onRewardedClicked(@NonNull RewardedAd ad) {
            }

            public void onRewardedImpression(@NonNull RewardedAd ad) {
            }

            public void onRewardedFailedToShow(@NonNull RewardedAd ad, @NonNull AdError error) {
            }

            public void onRewardedRevenuePaid(@NonNull RewardedAd ad, @NonNull com.wortise.ads.RevenueData reward) {
            }

            // Dummy implementation for older or newer versions
            public void onRewardedAdLoaded(@NonNull RewardedAd ad) {
            }

            public void onRewardedAdFailedToLoad(@NonNull RewardedAd ad, @NonNull AdError error) {
            }

            public void onRewardedAdDismissed(@NonNull RewardedAd ad) {
            }

            public void onRewardedAdCompleted(@NonNull RewardedAd ad) {
            }

            public void onRewardedAdClicked(@NonNull RewardedAd ad) {
            }

            public void onRewardedAdImpression(@NonNull RewardedAd ad) {
            }

            public void onRewardedAdShown(@NonNull RewardedAd ad) {
            }
        });
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isAvailable()) {
            rewardedAd.showAd();
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
