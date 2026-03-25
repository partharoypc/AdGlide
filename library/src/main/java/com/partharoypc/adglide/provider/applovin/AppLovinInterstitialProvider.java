package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

public class AppLovinInterstitialProvider implements InterstitialProvider {
    private MaxInterstitialAd maxInterstitialAd;
    private static final String TAG = "AdGlide.AppLovin";

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        maxInterstitialAd = new MaxInterstitialAd(adUnitId);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                AdGlideLog.d(TAG, "Interstitial Ad loaded");
                com.partharoypc.adglide.util.PerformanceLogger.log(TAG, "Interstitial loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                AdGlideLog.e(TAG, "Interstitial Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                AdGlideLog.e(TAG, "Interstitial Ad failed to display: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log(TAG, "Interstitial showed: " + ad.getAdUnitId());
                listener.onAdShowed();
            }

            @Override
            public void onAdHidden(MaxAd ad) {
                listener.onAdDismissed();
            }

            @Override
            public void onAdClicked(MaxAd ad) {
            }
        });



        AdGlideLog.d(TAG, "Loading Interstitial Ad: " + adUnitId);
        maxInterstitialAd.loadAd();
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            try {
                maxInterstitialAd.showAd(activity);
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Failed to show interstitial: " + e.getMessage());
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("AppLovin Interstitial not ready");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return maxInterstitialAd != null && maxInterstitialAd.isReady();
    }

    @Override
    public void destroy() {
        if (maxInterstitialAd != null) {
            maxInterstitialAd.destroy();
            maxInterstitialAd = null;
        }
    }
}
