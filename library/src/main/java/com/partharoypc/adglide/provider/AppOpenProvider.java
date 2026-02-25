package com.partharoypc.adglide.provider;

import android.app.Activity;
import android.content.Context;

public interface AppOpenProvider {
    void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener);

    void showAppOpenAd(Activity activity, AppOpenListener listener);

    boolean isAdAvailable();

    boolean isShowingAd();

    void destroy();

    interface AppOpenListener {
        void onAdLoaded();

        void onAdFailedToLoad(String error);

        void onAdDismissed();

        void onAdShowFailed(String error);

        void onAdShowed();
    }
}
