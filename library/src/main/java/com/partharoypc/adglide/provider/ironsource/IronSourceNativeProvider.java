package com.partharoypc.adglide.provider.ironsource;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAd;
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAdListener;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;

public class IronSourceNativeProvider implements NativeProvider {
    private LevelPlayNativeAd levelPlayNativeAd;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        levelPlayNativeAd = new LevelPlayNativeAd.Builder()
                .withPlacementName(adUnitId)
                .withListener(new LevelPlayNativeAdListener() {
                    @Override
                    public void onAdLoaded(LevelPlayNativeAd ad, AdInfo adInfo) {
                        View adView = inflateAndPopulateAdView(activity, ad, config);
                        listener.onAdLoaded(adView);
                    }

                    @Override
                    public void onAdLoadFailed(LevelPlayNativeAd ad, IronSourceError ironSourceError) {
                        listener.onAdFailedToLoad(ironSourceError.getErrorMessage());
                    }

                    @Override
                    public void onAdClicked(LevelPlayNativeAd ad, AdInfo adInfo) {
                    }

                    @Override
                    public void onAdImpression(LevelPlayNativeAd ad, AdInfo adInfo) {
                    }
                })
                .build();

        levelPlayNativeAd.loadAd();
    }

    private View inflateAndPopulateAdView(Activity activity, LevelPlayNativeAd ad, NativeConfig config) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layoutRes = getLayoutForStyle(config.getStyle());

        View nativeAdView = inflater.inflate(layoutRes, null);

        // Populate assets manually as per LevelPlay SDK 7.9.0+ guidelines
        TextView title = nativeAdView.findViewById(R.id.native_ad_title);
        TextView body = nativeAdView.findViewById(R.id.native_ad_body);
        Button cta = nativeAdView.findViewById(R.id.native_ad_call_to_action);
        ImageView icon = nativeAdView.findViewById(R.id.native_ad_icon);

        if (title != null)
            title.setText(ad.getTitle());
        if (body != null)
            body.setText(ad.getBody());
        if (cta != null)
            cta.setText(ad.getCallToAction());

        // Icon and MediaView require more specific handling in LevelPlay
        // For now, we populate what we can.

        return nativeAdView;
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "medium":
            case "banner":
            case "large":
                return R.layout.adglide_start_app_news_template_view;
            case "video":
                return R.layout.adglide_start_app_video_large_template_view;
            case "small":
            default:
                return R.layout.adglide_start_app_radio_template_view;
        }
    }

    @Override
    public void destroy() {
        if (levelPlayNativeAd != null) {
            levelPlayNativeAd.destroyAd();
            levelPlayNativeAd = null;
        }
    }
}
