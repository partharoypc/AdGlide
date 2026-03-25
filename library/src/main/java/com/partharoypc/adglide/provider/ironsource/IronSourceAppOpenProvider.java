package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import android.content.Context;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;
import androidx.annotation.NonNull;

public class IronSourceAppOpenProvider implements AppOpenProvider {
    private LevelPlayInterstitialAd interstitialAd;
    private boolean isShowing = false;
    private AppOpenListener activeListener;

    @Override
    public void loadAppOpenAd(Activity activity, String adUnitId, AppOpenListener listener) {
        this.activeListener = listener;
        interstitialAd = new LevelPlayInterstitialAd(adUnitId);
        interstitialAd.setListener(new LevelPlayInterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                if (activeListener != null) activeListener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                if (activeListener != null) activeListener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                isShowing = true;
                if (activeListener != null) activeListener.onAdShowed();
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError error, @NonNull LevelPlayAdInfo adInfo) {
                isShowing = false;
                if (activeListener != null) activeListener.onAdShowFailed(error.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {}

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
                isShowing = false;
                if (activeListener != null) activeListener.onAdDismissed();
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo adInfo) {}
        });
        interstitialAd.loadAd();
    }

    @Override
    public boolean isAdAvailable() {
        return interstitialAd != null && interstitialAd.isAdReady();
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        if (isAdAvailable()) {
            this.activeListener = listener;
            interstitialAd.showAd(activity);
        } else {
            listener.onAdShowFailed("IronSource AppOpen (LevelPlay) not ready");
        }
    }

    @Override
    public boolean isShowingAd() {
        return isShowing;
    }

    @Override
    public void destroy() {
        interstitialAd = null;
    }
}
