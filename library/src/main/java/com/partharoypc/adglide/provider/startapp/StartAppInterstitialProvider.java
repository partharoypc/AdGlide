package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;

import com.partharoypc.adglide.provider.InterstitialProvider;
import com.partharoypc.adglide.util.AdGlideLog;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class StartAppInterstitialProvider implements InterstitialProvider {
    private StartAppAd startAppAd;
    private boolean isReady = false;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed("startapp")) {
            listener.onAdFailedToLoad("StartApp is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        isReady = false;
        startAppAd = new StartAppAd(activity);
        startAppAd.loadAd(new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess("startapp", "INTERSTITIAL");
                isReady = true;
                listener.onAdLoaded();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("startapp", "INTERSTITIAL");
                isReady = false;
                AdGlideLog.e(com.partharoypc.adglide.util.Constant.AD_NETWORK_STARTAPP,
                        "Interstitial failed to load: " + ad.getErrorMessage());
                listener.onAdFailedToLoad("StartApp failed to receive ad");
            }
        });
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (startAppAd != null && isReady) {
            startAppAd.showAd(new AdDisplayListener() {
                @Override
                public void adDisplayed(Ad ad) {
                    listener.onAdShowed();
                }

                @Override
                public void adHidden(Ad ad) {
                    isReady = false;
                    listener.onAdDismissed();
                }

                @Override
                public void adClicked(Ad ad) {
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                    isReady = false;
                    listener.onAdShowFailed("StartApp ad not displayed");
                }
            });
        } else {
            listener.onAdShowFailed("StartApp Interstitial not ready");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return startAppAd != null && isReady;
    }

    @Override
    public void destroy() {
        startAppAd = null;
    }
}
