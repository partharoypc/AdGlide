package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAd;
import com.unity3d.mediation.rewarded.LevelPlayRewardedAdListener;
import com.unity3d.mediation.rewarded.LevelPlayReward;
import androidx.annotation.NonNull;

public class IronSourceRewardedProvider implements RewardedProvider {
    private LevelPlayRewardedAd rewardedAd;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        rewardedAd = new LevelPlayRewardedAd(adUnitId);
        rewardedAd.setListener(new LevelPlayRewardedAdListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Rewarded available: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Rewarded unavailable");
                listener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Rewarded opened");
                listener.onAdShowed();
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError error, @NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Rewarded show failed: " + error.getErrorMessage());
                listener.onAdShowFailed(error.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
                listener.onAdDismissed();
            }

            @Override
            public void onAdRewarded(@NonNull LevelPlayReward reward, @NonNull LevelPlayAdInfo adInfo) {
                listener.onAdCompleted();
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo adInfo) {
            }
        });
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isAdReady()) {
            rewardedAd.showAd(activity);
        } else {
            listener.onAdShowFailed("IronSource Rewarded not available");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return rewardedAd != null && rewardedAd.isAdReady();
    }

    @Override
    public void destroy() {
        rewardedAd = null;
    }
}
