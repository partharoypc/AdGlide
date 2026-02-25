package com.partharoypc.adglide.provider;

import android.app.Activity;
import android.view.View;

public interface NativeProvider {
    void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener);

    void destroy();

    interface NativeConfig {
        String getStyle();

        boolean isDarkTheme();

        boolean isLegacyGDPR();
    }

    interface NativeListener {
        void onAdLoaded(View adView);

        void onAdFailedToLoad(String error);
    }
}
