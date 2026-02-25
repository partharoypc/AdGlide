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
                        activity.runOnUiThread(() -> {
                            View adView = inflateAndPopulateAdView(activity, ad, config);
                            listener.onAdLoaded(adView);
                        });
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
        int layoutRes = getLayoutForStyle(config.getStyle());

        FrameLayout container = new FrameLayout(activity);
        LayoutInflater.from(activity).inflate(layoutRes, container, true);

        // Detach root view from temp container and return it directly
        View adView = container.getChildAt(0);
        container.removeAllViews();

        // Populate assets
        TextView title = adView.findViewById(R.id.native_ad_title);
        TextView body = adView.findViewById(R.id.native_ad_body);
        Button cta = adView.findViewById(R.id.native_ad_call_to_action);
        ImageView icon = adView.findViewById(R.id.native_ad_icon);

        if (title != null && ad.getTitle() != null)
            title.setText(ad.getTitle());

        if (body != null) {
            if (ad.getBody() != null) {
                body.setVisibility(View.VISIBLE);
                body.setText(ad.getBody());
            } else {
                body.setVisibility(View.GONE);
            }
        }

        if (cta != null) {
            if (ad.getCallToAction() != null) {
                cta.setVisibility(View.VISIBLE);
                cta.setText(ad.getCallToAction());
            } else {
                cta.setVisibility(View.GONE);
            }
        }

        if (icon != null) {
            if (ad.getIcon() != null && ad.getIcon().getDrawable() != null) {
                icon.setImageDrawable(ad.getIcon().getDrawable());
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.GONE);
            }
        }

        return adView;
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "banner":
                return R.layout.adglide_ironsource_news_template_view;
            case "small":
                return R.layout.adglide_ironsource_radio_template_view;
            case "medium":
            case "large":
            case "video":
            default:
                return R.layout.adglide_ironsource_medium_template_view;
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
