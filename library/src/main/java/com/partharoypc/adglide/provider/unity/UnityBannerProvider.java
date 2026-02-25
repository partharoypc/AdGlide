package com.partharoypc.adglide.provider.unity;

import android.app.Activity;
import android.util.Log;
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
        bannerView = new BannerView(activity, adUnitId,
                new UnityBannerSize(Constant.UNITY_ADS_BANNER_WIDTH_MEDIUM,
                        Constant.UNITY_ADS_BANNER_HEIGHT_MEDIUM));

        bannerView.setListener(new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerAdView) {
                Log.d("AdGlide.Unity", "Banner Ad loaded: " + adUnitId);
                listener.onAdLoaded(bannerAdView);
            }

            @Override
            public void onBannerClick(BannerView bannerAdView) {
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
                Log.e("AdGlide.Unity",
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
