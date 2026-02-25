package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;
import android.view.View;
import com.partharoypc.adglide.provider.BannerProvider;
import com.startapp.sdk.ads.banner.Banner;

public class StartAppBannerProvider implements BannerProvider {
    private View banner;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        if (config.isMrec()) {
            banner = new com.startapp.sdk.ads.banner.Mrec(activity, new com.startapp.sdk.ads.banner.BannerListener() {
                @Override
                public void onReceiveAd(View view) {
                    listener.onAdLoaded(view);
                }

                @Override
                public void onFailedToReceiveAd(View view) {
                    listener.onAdFailedToLoad("StartApp failed to receive ad");
                }

                @Override
                public void onImpression(View view) {
                }

                @Override
                public void onClick(View view) {
                }
            });
        } else {
            banner = new Banner(activity, new com.startapp.sdk.ads.banner.BannerListener() {
                @Override
                public void onReceiveAd(View view) {
                    listener.onAdLoaded(view);
                }

                @Override
                public void onFailedToReceiveAd(View view) {
                    listener.onAdFailedToLoad("StartApp failed to receive ad");
                }

                @Override
                public void onImpression(View view) {
                }

                @Override
                public void onClick(View view) {
                }
            });
        }

        if (banner instanceof com.startapp.sdk.ads.banner.BannerBase) {
            ((com.startapp.sdk.ads.banner.BannerBase) banner).loadAd();
        }
    }

    @Override
    public void destroy() {
        // StartApp Banner doesn't have a direct destroy() in the snippet,
        // but it's good practice if it did.
        banner = null;
    }
}
