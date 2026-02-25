package com.partharoypc.adglide.provider.wortise;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.wortise.ads.AdError;
import com.wortise.ads.RevenueData;
import com.wortise.ads.natives.GoogleNativeAd;

public class WortiseNativeProvider implements NativeProvider {
    private GoogleNativeAd googleNativeAd;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        googleNativeAd = new GoogleNativeAd(activity, adUnitId, new GoogleNativeAd.Listener() {
            @Override
            public void onNativeClicked(@NonNull GoogleNativeAd ad) {
            }

            @Override
            public void onNativeFailedToLoad(@NonNull GoogleNativeAd ad, @NonNull AdError error) {
                listener.onAdFailedToLoad(error.getMessage());
            }

            @Override
            public void onNativeLoaded(@NonNull GoogleNativeAd ad, @NonNull NativeAd nativeAd) {
                View adView = inflateAndPopulateAdView(activity, nativeAd, config);
                listener.onAdLoaded(adView);
            }

            @Override
            public void onNativeImpression(@NonNull GoogleNativeAd ad) {
            }

            @Override
            public void onNativeRevenuePaid(@NonNull GoogleNativeAd ad, @NonNull RevenueData data) {
            }
        });
        googleNativeAd.load();
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
                return R.layout.adglide_wortise_radio_template_view;
            case "medium":
                return R.layout.adglide_wortise_medium_template_view;
            case "video_small":
                return R.layout.adglide_wortise_video_small_template_view;
            case "video_large":
                return R.layout.adglide_wortise_video_large_template_view;
            case "news":
                return R.layout.adglide_wortise_news_template_view;
            default:
                return R.layout.adglide_wortise_large_template_view;
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
        if (googleNativeAd != null) {
            googleNativeAd.destroy();
            googleNativeAd = null;
        }
    }
}
