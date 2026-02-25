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

    @Override
    public void loadRewardedAd(Activity activity, String adUnitId, RewardedConfig config, RewardedListener listener) {
        startAppAd = new StartAppAd(activity);
        startAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull Ad ad) {
                listener.onAdLoaded();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                listener.onAdFailedToLoad("StartApp: Failed to receive ad");
            }
        });
    }

    @Override
    public void showRewardedAd(Activity activity, RewardedListener listener) {
        if (startAppAd != null && startAppAd.isReady()) {
            startAppAd.setVideoListener(new com.startapp.sdk.adsbase.VideoListener() {
                @Override
                public void onVideoCompleted() {
                    listener.onAdCompleted();
                }
            });
            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    listener.onAdDismissed();
                }

                @Override
                public void adDisplayed(Ad ad) {
                }

                @Override
                public void adClicked(Ad ad) {
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                }
            });
        }
    }

    @Override
    public boolean isAdAvailable() {
        return startAppAd != null && startAppAd.isReady();
    }

    @Override
    public void destroy() {
        startAppAd = null;
    }
}
