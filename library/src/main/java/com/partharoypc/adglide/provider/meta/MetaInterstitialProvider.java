package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import android.util.Log;

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
                Log.e("AdGlide.Meta",
                        "Interstitial Error: [" + adError.getErrorCode() + "] " + adError.getErrorMessage());
                listener.onAdFailedToLoad(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                listener.onAdLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (interstitialAd != null && interstitialAd.isAdLoaded()) {
            interstitialAd.show();
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
