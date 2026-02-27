package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import android.util.Log;
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
        maxInterstitialAd = new MaxInterstitialAd(adUnitId, AppLovinInitializer.getSdk(activity), activity);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "Interstitial Ad loaded");
                listener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "Interstitial Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e(TAG, "Interstitial Ad failed to display: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
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

        maxInterstitialAd.setRevenueListener(ad -> {
            com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                    .getConfig() != null ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener() : null;
            if (paidListener != null) {
                double valueMicros = ad.getRevenue() * 1000000;
                paidListener.onPaidEvent(valueMicros, "USD", "ESTIMATED", "AppLovin Interstitial", adUnitId);
            }
        });

        Log.d(TAG, "Loading Interstitial Ad: " + adUnitId);
        maxInterstitialAd.loadAd();
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (maxInterstitialAd != null && maxInterstitialAd.isReady()) {
            maxInterstitialAd.showAd();
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
