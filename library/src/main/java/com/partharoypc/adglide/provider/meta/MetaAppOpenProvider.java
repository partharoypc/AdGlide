package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.partharoypc.adglide.provider.AppOpenProvider;

public class MetaAppOpenProvider implements AppOpenProvider {
    private InterstitialAd interstitialAd;
    private boolean isShowing = false;
    private static final String TAG = "AdGlide.Meta";

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        interstitialAd = new InterstitialAd(context, adUnitId);
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                isShowing = true;
                listener.onAdShowed();
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                isShowing = false;
                listener.onAdDismissed();
                if (interstitialAd != null) {
                    interstitialAd.destroy();
                    interstitialAd = null;
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Meta AppOpen failed: " + adError.getErrorMessage());
                listener.onAdFailedToLoad(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Meta AppOpen loaded");
                listener.onAdLoaded();
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    @Override
    public boolean isAdAvailable() {
        return interstitialAd != null && interstitialAd.isAdLoaded();
    }

    @Override
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        if (isAdAvailable()) {
            interstitialAd.show();
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
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }
}
