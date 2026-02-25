package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;

public class IronSourceInterstitialProvider implements InterstitialProvider {
    private boolean isLoaded = false;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config,
            InterstitialListener listener) {
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
            @Override
            public void onAdReady(AdInfo adInfo) {
                isLoaded = true;
                listener.onAdLoaded();
            }

            @Override
            public void onAdLoadFailed(IronSourceError error) {
                isLoaded = false;
                listener.onAdFailedToLoad(error.getErrorMessage());
            }

            @Override
            public void onAdOpened(AdInfo adInfo) {
            }

            @Override
            public void onAdShowSucceeded(AdInfo adInfo) {
                listener.onAdShowed();
            }

            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo) {
                isLoaded = false;
                listener.onAdShowFailed(error.getErrorMessage());
            }

            @Override
            public void onAdClicked(AdInfo adInfo) {
            }

            @Override
            public void onAdClosed(AdInfo adInfo) {
                isLoaded = false;
                listener.onAdDismissed();
            }
        });
        IronSource.loadInterstitial();
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        } else {
            listener.onAdShowFailed("IronSource Interstitial not ready");
        }
    }

    @Override
    public boolean isAdLoaded() {
        return IronSource.isInterstitialReady();
    }

    @Override
    public void destroy() {
        // IronSource uses static methods, no individual ad destroy
    }
}
