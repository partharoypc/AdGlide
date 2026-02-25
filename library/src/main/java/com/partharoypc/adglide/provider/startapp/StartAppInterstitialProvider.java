package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class StartAppInterstitialProvider implements InterstitialProvider {
    private StartAppAd startAppAd;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        startAppAd = new StartAppAd(activity);
        startAppAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                listener.onAdLoaded();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                listener.onAdFailedToLoad("StartApp failed to receive ad");
            }
        });
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (startAppAd != null && startAppAd.isReady()) {
            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adDisplayed(Ad ad) {
                    listener.onAdShowed();
                }

                @Override
                public void adHidden(Ad ad) {
                    listener.onAdDismissed();
                }

                @Override
                public void adClicked(Ad ad) {
                }

                public void adNotDisplayed(Ad ad) {
                    listener.onAdShowFailed("StartApp ad not displayed");
                }
            });
        } else {
            listener.onAdShowFailed("StartApp Interstitial not ready");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return startAppAd != null && startAppAd.isReady();
    }

    @Override
    public void destroy() {
        startAppAd = null;
    }
}
