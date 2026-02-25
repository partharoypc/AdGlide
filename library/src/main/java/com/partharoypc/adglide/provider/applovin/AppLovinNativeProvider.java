package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import android.util.Log;
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
        nativeAdLoader = new MaxNativeAdLoader(adUnitId, AppLovinInitializer.getSdk(activity), activity);
        nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
            @Override
            public void onNativeAdLoaded(MaxNativeAdView nativeAdView, MaxAd ad) {
                Log.d(TAG, "Native Ad loaded");
                if (maxNativeAd != null) {
                    nativeAdLoader.destroy(maxNativeAd);
                }
                maxNativeAd = ad;

                // Create a custom view using our template if possible
                int layoutRes = getLayoutForStyle(config.getStyle());
                MaxNativeAdView customView = new MaxNativeAdView(getTemplateName(config.getStyle()), activity);
                nativeAdLoader.render(customView, ad);

                listener.onAdLoaded(customView);
            }

            @Override
            public void onNativeAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "Native Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }
        });
        Log.d(TAG, "Loading Native Ad: " + adUnitId);
        nativeAdLoader.loadAd();
    }

    private String getTemplateName(String style) {
        switch (style) {
            case "small":
                return "radio";
            case "medium":
                return "medium";
            case "banner":
                return "news";
            case "video":
                return "video_large";
            default:
                return "medium";
        }
    }

    private int getLayoutForStyle(String style) {
        // These are not directly used by AppLovin render but kept for reference
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
