package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.RevenueData;
import com.wortise.ads.interstitial.InterstitialAd;

public class WortiseInterstitialProvider implements InterstitialProvider {
    private InterstitialAd interstitialAd;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        interstitialAd = new InterstitialAd(activity, adUnitId);
        interstitialAd.setListener(new InterstitialAd.Listener() {
            @Override
            public void onInterstitialClicked(@NonNull InterstitialAd ad) {
            }

            @Override
            public void onInterstitialDismissed(@NonNull InterstitialAd ad) {
                interstitialAd = null;
                listener.onAdDismissed();
            }

            @Override
            public void onInterstitialFailedToLoad(@NonNull InterstitialAd ad, @NonNull AdError error) {
                interstitialAd = null;
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onInterstitialLoaded(@NonNull InterstitialAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Interstitial loaded: " + adUnitId);
                listener.onAdLoaded();
            }

            @Override
            public void onInterstitialShown(@NonNull InterstitialAd ad) {
                com.partharoypc.adglide.util.PerformanceLogger.log("Wortise", "Interstitial showed: " + adUnitId);
                listener.onAdShowed();
            }

            @Override
            public void onInterstitialFailedToShow(@NonNull InterstitialAd ad, @NonNull AdError error) {
                interstitialAd = null;
                com.partharoypc.adglide.util.PerformanceLogger.error("Wortise", "Interstitial show failed: " + error.getMessage());
                listener.onAdShowFailed(error.getMessage());
            }

            @Override
            public void onInterstitialImpression(@NonNull InterstitialAd ad) {
            }

            @Override
            public void onInterstitialRevenuePaid(@NonNull InterstitialAd ad, @NonNull RevenueData revenueData) {
                // Wortise RevenueData fields not publicly accessible; revenue is tracked via server callbacks.
            }
        });
        interstitialAd.loadAd();
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (interstitialAd != null && interstitialAd.isAvailable()) {
            try {
                interstitialAd.showAd();
            } catch (Exception e) {
                AdGlideLog.e("AdGlide.Wortise", "Failed to show interstitial: " + e.getMessage());
                listener.onAdShowFailed(e.getMessage());
            }
        } else {
            listener.onAdShowFailed("Wortise Interstitial not available");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return interstitialAd != null && interstitialAd.isAvailable();
    }

    @Override
    public void destroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }
}
