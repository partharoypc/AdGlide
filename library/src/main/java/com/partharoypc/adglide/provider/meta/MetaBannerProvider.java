package com.partharoypc.adglide.provider.meta;

import android.app.Activity;

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
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed(com.partharoypc.adglide.util.Constant.AD_NETWORK_META)) {
            listener.onAdFailedToLoad("Meta is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        AdSize adSize;
        if (config.isMrec()) {
            adSize = AdSize.RECTANGLE_HEIGHT_250;
        } else {
            adSize = AdSize.BANNER_HEIGHT_90;
        }
        adView = new AdView(activity, adUnitId, adSize);

        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure(com.partharoypc.adglide.util.Constant.AD_NETWORK_META, adUnitId);
                listener.onAdFailedToLoad("[" + adError.getErrorCode() + "] " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess(com.partharoypc.adglide.util.Constant.AD_NETWORK_META, adUnitId);
                listener.onAdLoaded(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                listener.onAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                listener.onAdShowed();
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
