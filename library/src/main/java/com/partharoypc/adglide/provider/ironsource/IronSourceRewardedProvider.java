package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.partharoypc.adglide.provider.RewardedProvider;

public class IronSourceRewardedProvider implements RewardedProvider {
    private boolean isAvailable = false;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        IronSource.setLevelPlayRewardedVideoListener(new LevelPlayRewardedVideoListener() {
            @Override
            public void onAdOpened(AdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Rewarded opened");
                listener.onAdShowed();
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                isAvailable = false;
                listener.onAdDismissed();
            }

            @Override
            public void onAdAvailable(AdInfo adInfo) {
                isAvailable = true;
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Rewarded available: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onAdUnavailable() {
                isAvailable = false;
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Rewarded unavailable");
                listener.onAdFailedToLoad("IronSource Rewarded unavailable");
            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                isAvailable = false;
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Rewarded show failed: " + ironSourceError.getErrorMessage());
                listener.onAdShowFailed(ironSourceError.getErrorMessage());
            }

            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo) {
                listener.onAdCompleted();
            }

            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo) {
            }
        });
        IronSource.loadRewardedVideo();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (IronSource.isRewardedVideoAvailable()) {
            try {
                IronSource.showRewardedVideo();
            } catch (Exception e) {
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("IronSource Rewarded not available");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return IronSource.isRewardedVideoAvailable();
    }

    @Override
    public void destroy() {
        isAvailable = false;
    }
}
