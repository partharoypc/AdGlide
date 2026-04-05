package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;

import com.partharoypc.adglide.provider.InterstitialProvider;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class MetaInterstitialProvider implements InterstitialProvider {
    private InterstitialAd interstitialAd;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        interstitialAd = new InterstitialAd(activity, adUnitId);
        InterstitialAdListener adListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Meta", "Interstitial showed: " + adUnitId);
                listener.onAdShowed();
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd = null;
                listener.onAdDismissed();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                interstitialAd = null;
                AdGlideLog.e(com.partharoypc.adglide.util.Constant.AD_NETWORK_META,
                        "Interstitial Error: [" + adError.getErrorCode() + "] " + adError.getErrorMessage());
                com.partharoypc.adglide.util.PerformanceLogger.error("Meta",
                        "Interstitial failed: [" + adError.getErrorCode() + "] " + adError.getErrorMessage());
                
                listener.onAdFailedToLoad("[" + adError.getErrorCode() + "] " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Meta", "Interstitial loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
                listener.onAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                listener.onAdShowed();
            }

        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            try {
                interstitialAd.show();
            } catch (Exception e) {
                AdGlideLog.e(com.partharoypc.adglide.util.Constant.AD_NETWORK_META, "Failed to show interstitial: " + e.getMessage());
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("Meta Interstitial not loaded");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return interstitialAd != null && interstitialAd.isAdLoaded();
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }
}
