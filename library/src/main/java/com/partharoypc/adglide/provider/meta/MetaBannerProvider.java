package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import com.partharoypc.adglide.provider.BannerProvider;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

public class MetaBannerProvider implements BannerProvider {
    private AdView adView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        AdSize adSize;
        if (config.isMrec()) {
            adSize = AdSize.RECTANGLE_HEIGHT_250;
        } else {
            adSize = com.partharoypc.adglide.util.Tools.isTablet(activity)
                    ? AdSize.BANNER_HEIGHT_90
                    : AdSize.BANNER_HEIGHT_50;
        }
        adView = new AdView(activity, adUnitId, adSize);

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                listener.onAdFailedToLoad(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                listener.onAdLoaded(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };

        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }
}
