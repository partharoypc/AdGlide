package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.partharoypc.adglide.provider.RewardedProvider;

public class MetaRewardedProvider implements RewardedProvider {
    private RewardedVideoAd rewardedVideoAd;
    private boolean isAvailable = false;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        rewardedVideoAd = new RewardedVideoAd(activity, adUnitId);
        rewardedVideoAd.loadAd(rewardedVideoAd.buildLoadAdConfig()
                .withAdListener(new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoCompleted() {
                        listener.onAdCompleted();
                    }

                    @Override
                    public void onRewardedVideoClosed() {
                        listener.onAdDismissed();
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        isAvailable = false;
                        Log.e("AdGlide.Meta",
                                "Rewarded Error: [" + adError.getErrorCode() + "] " + adError.getErrorMessage());
                        listener.onAdFailedToLoad(adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        isAvailable = true;
                        listener.onAdLoaded();
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                    }
                }).build());
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedVideoAd != null && isAvailable) {
            rewardedVideoAd.show();
        }
    }

    @Override
    public boolean isAdAvailable() {
        return rewardedVideoAd != null && isAvailable;
    }

    @Override
    public void destroy() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }
    }
}
