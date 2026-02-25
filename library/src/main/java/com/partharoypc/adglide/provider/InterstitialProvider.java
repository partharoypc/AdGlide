package com.partharoypc.adglide.provider;

import android.app.Activity;

public interface InterstitialProvider {
    void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig config, InterstitialListener listener);

    void showInterstitial(Activity activity, InterstitialListener listener);

    boolean isAdLoaded();

    void destroy();

    interface InterstitialConfig {
        boolean isDebug();

        boolean isTestMode();
    }

    interface InterstitialListener {
        void onAdLoaded();

        void onAdFailedToLoad(String error);

        void onAdDismissed();

        void onAdShowFailed(String error);

        void onAdShowed();
    }
}
