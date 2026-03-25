package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.RewardedProvider;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class StartAppRewardedProvider implements RewardedProvider {
    private StartAppAd startAppAd;
    private boolean isReady = false;

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        isReady = false;
        startAppAd = new StartAppAd(activity);
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                isReady = true;
                listener.onAdLoaded();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                isReady = false;
                listener.onAdFailedToLoad("StartApp: Failed to receive ad");
            }
        });
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (startAppAd != null && isReady) {
            startAppAd.setVideoListener(new com.startapp.sdk.adsbase.adlisteners.VideoListener() {
                @Override
                public void onVideoCompleted() {
                    listener.onAdCompleted();
                }
            });
            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    isReady = false;
                    listener.onAdDismissed();
                }

                @Override
                public void adDisplayed(Ad ad) {
                    com.partharoypc.adglide.util.PerformanceLogger.log("StartApp", "Rewarded showed");
                    listener.onAdShowed();
                }

                @Override
                public void adClicked(Ad ad) {
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                    isReady = false;
                    com.partharoypc.adglide.util.PerformanceLogger.error("StartApp", "Rewarded not displayed: " + (ad != null ? ad.getErrorMessage() : "null"));
                    listener.onAdShowFailed("StartApp Rewarded not displayed");
                }
            });
        } else {
            listener.onAdShowFailed("StartApp Rewarded not ready");
        }
    }

    @Override
    public boolean isAdAvailable() {
        return startAppAd != null && isReady;
    }

    @Override
    public void destroy() {
        startAppAd = null;
    }
}
