package com.partharoypc.adglide.provider.applovin;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.partharoypc.adglide.provider.BannerProvider;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;

public class AppLovinBannerProvider implements BannerProvider {
    private MaxAdView maxAdView;
    private static final String TAG = "AdGlide.AppLovin";

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        if (config.isMrec()) {
            maxAdView = new MaxAdView(adUnitId, com.applovin.mediation.MaxAdFormat.MREC,
                    AppLovinInitializer.getSdk(activity), activity);
        } else {
            maxAdView = new MaxAdView(adUnitId, AppLovinInitializer.getSdk(activity), activity);
        }
        maxAdView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.d(TAG, "Banner Ad loaded");
                listener.onAdLoaded(maxAdView);
            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e(TAG, "Banner Ad failed to load: [" + error.getCode() + "] " + error.getMessage());
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
            }

            @Override
            public void onAdExpanded(MaxAd ad) {
            }

            @Override
            public void onAdCollapsed(MaxAd ad) {
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
            }

            @Override
            public void onAdHidden(MaxAd ad) {
            }

            @Override
            public void onAdClicked(MaxAd ad) {
            }
        });

        maxAdView.setRevenueListener(ad -> {
            com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                    .getConfig() != null ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener() : null;
            if (paidListener != null) {
                double valueMicros = ad.getRevenue() * 1000000;
                paidListener.onPaidEvent(valueMicros, "USD", "ESTIMATED", "AppLovin Banner", adUnitId);
            }
        });

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int heightPx;
        if (config.isMrec()) {
            heightPx = AppLovinSdkUtils.dpToPx(activity, 250);
            width = AppLovinSdkUtils.dpToPx(activity, 300);
            maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        } else {
            int bannerHeight = com.partharoypc.adglide.util.Tools.isTablet(activity) ? 90 : 50;
            heightPx = AppLovinSdkUtils.dpToPx(activity, bannerHeight);
            maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
        }
        Log.d(TAG, "Loading Banner Ad: " + adUnitId);
        maxAdView.loadAd();
    }

    @Override
    public void destroy() {
        if (maxAdView != null) {
            maxAdView.destroy();
            maxAdView = null;
        }
    }
}
