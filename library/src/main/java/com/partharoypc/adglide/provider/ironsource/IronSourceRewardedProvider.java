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
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                listener.onAdDismissed();
            }

            @Override
            public void onAdAvailable(AdInfo adInfo) {
                isAvailable = true;
                listener.onAdLoaded();
            }

            @Override
            public void onAdUnavailable() {
                isAvailable = false;
            }

            @Override
            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                isAvailable = false;
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
            IronSource.showRewardedVideo();
        }
    }

    @Override
    public boolean isAdAvailable() {
        return IronSource.isRewardedVideoAvailable();
    }

    @Override
    public void destroy() {
    }
}
