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
import android.util.Log;
import androidx.annotation.NonNull;

public class AdMobBannerProvider implements BannerProvider {
    private AdView adView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig config, BannerListener listener) {
        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adUnitId)) {
            listener.onAdFailedToLoad("AdMob rate limit hit");
            return;
        }

        adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(getAdSize(activity, config));

        AdRequest.Builder builder = new AdRequest.Builder();
        if (config.isCollapsible() && !config.isMrec()) {
            Bundle extras = new Bundle();
            extras.putString("collapsible", "bottom");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                adView.setOnPaidEventListener(adValue -> {
                    com.partharoypc.adglide.util.OnPaidEventListener paidListener = com.partharoypc.adglide.AdGlide
                            .getConfig() != null ? com.partharoypc.adglide.AdGlide.getConfig().getOnPaidEventListener()
                                    : null;
                    if (paidListener != null) {
                        paidListener.onPaidEvent(adValue.getValueMicros(), adValue.getCurrencyCode(),
                                String.valueOf(adValue.getPrecisionType()), "AdMob Banner", adUnitId);
                    }
                });
                listener.onAdLoaded(adView);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                    com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                }
                listener.onAdFailedToLoad(adError.getMessage());
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
        if (!config.isAdaptive()) {
            return AdSize.BANNER;
        }
        // Simple implementation of adaptive banner for provider
        // Original logic is in Tools.java which we will refactor later
        int adWidth;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            android.view.WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            float widthPixels = windowMetrics.getBounds().width();
            float density = activity.getResources().getDisplayMetrics().density;
            adWidth = (int) (widthPixels / density);
        } else {
            android.util.DisplayMetrics outMetrics = new android.util.DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;
            adWidth = (int) (widthPixels / density);
        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
