package com.partharoypc.adglide.provider.admob;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;

public class AdMobNativeProvider implements NativeProvider {
    private NativeAd nativeAd;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        if (!com.partharoypc.adglide.util.AdMobRateLimiter.isRequestAllowed(adUnitId)) {
            listener.onAdFailedToLoad("AdMob rate limit hit");
            return;
        }

        AdLoader adLoader = new AdLoader.Builder(activity, adUnitId)
                .forNativeAd(ad -> {
                    com.partharoypc.adglide.util.AdMobRateLimiter.resetCooldown(adUnitId);
                    if (this.nativeAd != null) {
                        this.nativeAd.destroy();
                    }
                    this.nativeAd = ad;

                    View adView = inflateAndPopulateAdView(activity, ad, config);
                    listener.onAdLoaded(adView);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        if (adError.getCode() == com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL) {
                            com.partharoypc.adglide.util.AdMobRateLimiter.recordFailure(adUnitId);
                        }
                        listener.onAdFailedToLoad(adError.getMessage());
                    }
                })
                .build();

        com.google.android.gms.ads.AdRequest.Builder requestBuilder = new com.google.android.gms.ads.AdRequest.Builder();
        adLoader.loadAd(requestBuilder.build());
    }

    private View inflateAndPopulateAdView(Activity activity, NativeAd ad, NativeConfig config) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layoutRes = getLayoutForStyle(config.getStyle());

        android.widget.FrameLayout container = new android.widget.FrameLayout(activity);
        inflater.inflate(layoutRes, container, true);

        NativeAdView nativeAdView = container.findViewById(R.id.native_ad_view);

        if (nativeAdView == null) {
            if (container.getChildCount() > 0) {
                View child = container.getChildAt(0);
                container.removeView(child);
                return child;
            }
            return container;
        }

        // Detach from the temporary container
        container.removeView(nativeAdView);

        populateNativeAdView(ad, nativeAdView);
        return nativeAdView;
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "small":
                return R.layout.adglide_ad_mob_radio_template_view;
            case "medium":
                return R.layout.adglide_ad_mob_medium_template_view;
            case "banner":
                return R.layout.adglide_ad_mob_news_template_view;
            case "video":
                return R.layout.adglide_ad_mob_video_large_template_view;
            default:
                return R.layout.adglide_ad_mob_medium_template_view;
        }
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView nativeAdView) {
        nativeAdView.setMediaView(nativeAdView.findViewById(R.id.media_view));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.primary));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.cta));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.icon));

        if (nativeAdView.getHeadlineView() != null)
            ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAdView.getMediaView() != null)
            nativeAdView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAdView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                nativeAdView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                nativeAdView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) nativeAdView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (nativeAdView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                nativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                nativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) nativeAdView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (nativeAdView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                nativeAdView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                nativeAdView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        nativeAdView.setNativeAd(nativeAd);
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }
}
