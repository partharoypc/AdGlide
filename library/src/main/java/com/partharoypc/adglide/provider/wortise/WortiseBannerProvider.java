package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import android.view.View;
import com.partharoypc.adglide.provider.BannerProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.AdSize;
import com.wortise.ads.RevenueData;
import com.wortise.ads.banner.BannerAd;

import androidx.annotation.NonNull;

public class WortiseBannerProvider implements BannerProvider {
    private BannerAd bannerAd;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        bannerAd = new BannerAd(activity);
        AdSize size = config.isMrec() ? AdSize.HEIGHT_250 : AdSize.HEIGHT_50;
        bannerAd.setAdSize(size);
        bannerAd.setAdUnitId(adUnitId);
        bannerAd.setListener(new BannerAd.Listener() {
            @Override
            public void onBannerClicked(@NonNull BannerAd banner) {
            }

            @Override
            public void onBannerFailedToLoad(@NonNull BannerAd banner, @NonNull AdError error) {
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onBannerLoaded(@NonNull BannerAd banner) {
                listener.onAdLoaded(banner);
            }

            @Override
            public void onBannerImpression(@NonNull BannerAd banner) {
            }

            @Override
            public void onBannerRevenuePaid(@NonNull BannerAd banner, @NonNull RevenueData revenueData) {
            }
        });

        bannerAd.loadAd();
    }

    @Override
    public void destroy() {
        if (bannerAd != null) {
            bannerAd.destroy();
            bannerAd = null;
        }
    }
}
