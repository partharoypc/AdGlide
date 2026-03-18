package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;
import androidx.annotation.NonNull;

public class IronSourceInterstitialProvider implements InterstitialProvider {
    private LevelPlayInterstitialAd interstitialAd;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        interstitialAd = new LevelPlayInterstitialAd(adUnitId);
        interstitialAd.setListener(new LevelPlayInterstitialAdListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Interstitial loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Interstitial failed: " + error.getErrorMessage());
                listener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.log("IronSource", "Interstitial showed");
                listener.onAdShowed();
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdError error, @NonNull LevelPlayAdInfo adInfo) {
                com.partharoypc.adglide.util.PerformanceLogger.error("IronSource", "Interstitial show failed: " + error.getErrorMessage());
                listener.onAdShowFailed(error.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
            }

            @Override
            public void onAdClosed(@NonNull LevelPlayAdInfo adInfo) {
                listener.onAdDismissed();
            }

            @Override
            public void onAdInfoChanged(@NonNull LevelPlayAdInfo adInfo) {
            }
        });
        interstitialAd.loadAd();
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (interstitialAd != null && interstitialAd.isAdReady()) {
            interstitialAd.showAd(activity);
        } else {
            listener.onAdShowFailed("IronSource Interstitial not ready");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return interstitialAd != null && interstitialAd.isAdReady();
    }

    @Override
    public void destroy() {
        interstitialAd = null;
    }
}
