package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.partharoypc.adglide.provider.RewardedProvider;

public class AppLovinRewardedProvider implements RewardedProvider {
    private MaxRewardedAd rewardedAd;
    private static final String TAG = "AdGlide.AppLovin";

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed("applovin")) {
            listener.onAdFailedToLoad("AppLovin is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        rewardedAd = MaxRewardedAd.getInstance(adUnitId);
        rewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                listener.onAdCompleted();
            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess("applovin", adUnitId);
                AdGlideLog.d(TAG, "Rewarded Ad loaded");
                listener.onAdLoaded();
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log(TAG, "Rewarded showed: " + ad.getAdUnitId());
                listener.onAdShowed();
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                listener.onAdDismissed();
            }

            @Override
            public void onAdClicked(MaxAd ad) {
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("applovin", adUnitId);
                AdGlideLog.e(TAG, "Rewarded Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                AdGlideLog.e(TAG, "Rewarded Ad failed to display: [" + error.getCode() + "] " + error.getMessage());
                com.partharoypc.adglide.util.PerformanceLogger.error(TAG, "Rewarded show failed: " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }
        });
        AdGlideLog.d(TAG, "Loading Rewarded Ad: " + adUnitId);
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isReady()) {
            try {
                rewardedAd.showAd(activity);
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed to show rewarded: " + e.getMessage());
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("AppLovin Rewarded not ready");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return rewardedAd != null && rewardedAd.isReady();
    }

    @Override
    public void destroy() {
        rewardedAd = null;
    }
}
