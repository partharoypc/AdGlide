package com.partharoypc.adglide.provider.admob;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import com.partharoypc.adglide.provider.BannerProvider;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.ads.mediation.admob.AdMobAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.partharoypc.adglide.util.Tools;

public class AdMobBannerProvider implements BannerProvider {
    private AdView adView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(getAdSize(activity, config));

        AdRequest.Builder builder = new AdRequest.Builder();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                listener.onAdLoaded(adView);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                }
                listener.onAdFailedToLoad(adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                listener.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                listener.onAdShowed();
            }

        });

        adView.loadAd(builder.build());
    }

    @Override
    public void destroy() {
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    private AdSize getAdSize(Activity activity, BannerConfig config) {
        if (config.isMrec()) {
            return AdSize.MEDIUM_RECTANGLE;
        }
        if (config.isAdaptive()) {
            // Determine the screen width (less padding) to use for the ad width.
            int adWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;
            float density = activity.getResources().getDisplayMetrics().density;
            int adWidth = (int) (adWidthPixels / density);

            // Get adaptive ad size and return for setting on the ad view.
            @SuppressWarnings("deprecation")
            AdSize adaptiveSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
            return adaptiveSize;
        }
        return AdSize.BANNER;
    }
}
