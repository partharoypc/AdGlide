package com.partharoypc.adglide.provider.admob;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdMobInterstitialProvider implements InterstitialProvider {
    private InterstitialAd interstitialAd;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adUnitId)) {
            listener.onAdFailedToLoad("AdMob rate limit hit");
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                interstitialAd = ad;
                listener.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (loadAdError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                    com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                }
                interstitialAd = null;
                listener.onAdFailedToLoad(loadAdError.getMessage());
            }
        });
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    listener.onAdDismissed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    interstitialAd = null;
                    listener.onAdShowFailed(adError.getMessage());
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    listener.onAdShowed();
                }
            });
            interstitialAd.show(activity);
        } else {
            listener.onAdShowFailed("AdMob Interstitial not loaded");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return interstitialAd != null;
    }

    @Override
    public void destroy() {
        interstitialAd = null;
    }
}
