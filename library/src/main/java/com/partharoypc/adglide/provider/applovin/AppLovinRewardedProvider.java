package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import android.util.Log;
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
        rewardedAd = MaxRewardedAd.getInstance(adUnitId, AppLovinInitializer.getSdk(activity), activity);
        rewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                listener.onAdCompleted();
            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "Rewarded Ad loaded");
                listener.onAdLoaded();
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
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
                Log.e(TAG, "Rewarded Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG, "Rewarded Ad failed to display: [" + error.getCode() + "] " + error.getMessage());
            }
        });

        rewardedAd.setRevenueListener(ad -> {
            com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                    .getConfig() != null ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener() : null;
            if (paidListener != null) {
                double valueMicros = ad.getRevenue() * 1000000;
                paidListener.onPaidEvent(valueMicros, "USD", "ESTIMATED", "AppLovin Rewarded", adUnitId);
            }
        });

        Log.d(TAG, "Loading Rewarded Ad: " + adUnitId);
        rewardedAd.loadAd();
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (rewardedAd != null && rewardedAd.isReady()) {
            rewardedAd.showAd();
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
