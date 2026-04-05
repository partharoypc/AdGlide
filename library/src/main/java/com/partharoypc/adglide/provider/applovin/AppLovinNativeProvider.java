package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import com.partharoypc.adglide.util.AdGlideLog;
import android.view.View;
import android.widget.FrameLayout;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.partharoypc.adglide.provider.NativeProvider;

public class AppLovinNativeProvider implements NativeProvider {
    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd maxNativeAd;
    private static final String TAG = "AdGlide.AppLovin";

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        nativeAdLoader = new MaxNativeAdLoader(adUnitId);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                AdGlideLog.d(TAG, "Native Ad loaded");
                if (maxNativeAd != null) {
                    nativeAdLoader.destroy(maxNativeAd);
                }
                maxNativeAd = ad;

                if (nativeAdView != null) {
                    listener.onAdLoaded(nativeAdView);
                } else {
                    // Fallback to manual rendering if view is null
                    int layoutRes = getLayoutForStyle(config.getStyle());
                    com.applovin.mediation.nativeAds.MaxNativeAdViewBinder binder = new com.applovin.mediation.nativeAds.MaxNativeAdViewBinder.Builder(layoutRes)
                            .setTitleTextViewId(com.partharoypc.adglide.R.id.adglide_native_headline)
                            .setBodyTextViewId(com.partharoypc.adglide.R.id.adglide_native_body)
                            .setIconImageViewId(com.partharoypc.adglide.R.id.adglide_native_icon)
                            .setMediaContentViewGroupId(com.partharoypc.adglide.R.id.adglide_native_media)
                            .setOptionsContentViewGroupId(com.partharoypc.adglide.R.id.adglide_ad_choices_container)
                            .setCallToActionButtonId(com.partharoypc.adglide.R.id.adglide_native_cta)
                            .build();
                    MaxNativeAdView customView = new MaxNativeAdView(binder, activity);
                    nativeAdLoader.render(customView, ad);
                    listener.onAdLoaded(customView);
                }
                listener.onAdShowed();
            }

            @Override
            public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                AdGlideLog.e(TAG, "Native Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onNativeAdClicked(MaxAd ad) {
                listener.onAdClicked();
            }
        });
        AdGlideLog.d(TAG, "Loading Native Ad: " + adUnitId);
        nativeAdLoader.loadAd();
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "small":
                return com.partharoypc.adglide.R.layout.adglide_app_lovin_radio_template_view;
            case "medium":
                return com.partharoypc.adglide.R.layout.adglide_app_lovin_medium_template_view;
            case "banner":
                return com.partharoypc.adglide.R.layout.adglide_app_lovin_news_template_view;
            case "video":
                return com.partharoypc.adglide.R.layout.adglide_app_lovin_video_large_template_view;
            default:
                return com.partharoypc.adglide.R.layout.adglide_app_lovin_medium_template_view;
        }
    }

    @Override
    public void destroy() {
        if (nativeAdLoader != null && maxNativeAd != null) {
            nativeAdLoader.destroy(maxNativeAd);
            maxNativeAd = null;
        }
    }
}
