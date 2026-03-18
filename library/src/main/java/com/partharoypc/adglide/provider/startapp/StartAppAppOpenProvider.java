package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

public class StartAppAppOpenProvider implements AppOpenProvider {
    private StartAppAd startAppAd;
    private boolean isShowing = false;
    private static final String TAG = "AdGlide.StartApp";
    private AppOpenListener activeListener;

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        this.activeListener = listener;
        startAppAd = new StartAppAd(context);
        startAppAd.loadAd(StartAppAd.AdMode.AUTOMATIC, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                Log.d(TAG, "StartApp AppOpen loaded");
                if (activeListener != null) activeListener.onAdLoaded();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                Log.e(TAG, "StartApp AppOpen failed to load: " + ad.getErrorMessage());
                if (activeListener != null) activeListener.onAdFailedToLoad(ad.getErrorMessage());
            }
        });
    }

    @Override
    public boolean isAdAvailable() {
        return startAppAd != null && startAppAd.isReady();
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        if (isAdAvailable()) {
            this.activeListener = listener;
            startAppAd.showAd(new com.startapp.sdk.adsbase.adlisteners.AdDisplayListener() {
                @Override
                public void adHidden(Ad ad) {
                    isShowing = false;
                    if (activeListener != null) activeListener.onAdDismissed();
                }

                @Override
                public void adDisplayed(Ad ad) {
                    isShowing = true;
                    if (activeListener != null) activeListener.onAdShowed();
                }

                @Override
                public void adClicked(Ad ad) {
                    // No specific callback for click in AppOpenListener interface
                }

                @Override
                public void adNotDisplayed(Ad ad) {
                    isShowing = false;
                    if (activeListener != null) activeListener.onAdShowFailed(ad.getErrorMessage());
                }
            });
        } else {
            listener.onAdShowFailed("Ad not available");
        }
    }

    @Override
    public boolean isShowingAd() {
        return isShowing;
    }

    @Override
    public void destroy() {
        startAppAd = null;
    }
}
