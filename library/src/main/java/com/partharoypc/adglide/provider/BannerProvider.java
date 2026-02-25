package com.partharoypc.adglide.provider;

import android.app.Activity;
import android.view.View;

public interface BannerProvider {
    void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener);

    void destroy();

    interface BannerConfig {
        boolean isDarkTheme();

        boolean isLegacyGDPR();

        boolean isCollapsible();

        boolean isMrec();
    }

    interface BannerListener {
        void onAdLoaded(View adView);

        void onAdFailedToLoad(String error);
    }
}
