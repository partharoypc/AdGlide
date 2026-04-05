package com.partharoypc.adglide.provider.unity;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import android.view.View;
import com.partharoypc.adglide.provider.BannerProvider;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.partharoypc.adglide.util.Constant;

public class UnityBannerProvider implements BannerProvider {
    private BannerView bannerView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        // Removed redundant notifyLoadStarted call

        bannerView = new BannerView((android.content.Context) activity, adUnitId,
                new UnityBannerSize(Constant.UNITY_ADS_BANNER_WIDTH_MEDIUM,
                        Constant.UNITY_ADS_BANNER_HEIGHT_MEDIUM));

        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                AdGlideLog.d(com.partharoypc.adglide.util.Constant.AD_NETWORK_UNITY, "Banner Ad loaded: " + adUnitId);
                listener.onAdLoaded(bannerAdView);
                listener.onAdShowed();
            }

            @Override
            public void onBannerClick(BannerView bannerAdView) {
                listener.onAdClicked();
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                AdGlideLog.e(com.partharoypc.adglide.util.Constant.AD_NETWORK_UNITY,
                        "Banner Ad failed to load: [" + errorInfo.errorCode + "] " + errorInfo.errorMessage);
                listener.onAdFailedToLoad(errorInfo.errorMessage);
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerAdView) {
            }

            @Override
            public void onBannerShown(BannerView bannerAdView) {
            }
        });

        bannerView.load();
    }

    @Override
    public void destroy() {
        if (bannerView != null) {
            bannerView.destroy();
            bannerView = null;
        }
    }
}
