package com.partharoypc.adglide.provider.meta;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import java.util.ArrayList;
import java.util.List;

public class MetaNativeProvider implements NativeProvider {
    private NativeAd nativeAd;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        if (!com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).isNetworkHealed(com.partharoypc.adglide.util.Constant.AD_NETWORK_META)) {
            listener.onAdFailedToLoad("Meta is currently healing from recent failures.");
            return;
        }
        // Removed redundant notifyLoadStarted call

        nativeAd = new NativeAd(activity, adUnitId);
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure(com.partharoypc.adglide.util.Constant.AD_NETWORK_META, adUnitId);
                AdGlideLog.e(com.partharoypc.adglide.util.Constant.AD_NETWORK_META,
                        "Native Error: [" + adError.getErrorCode() + "] " + adError.getErrorMessage());
                listener.onAdFailedToLoad("[" + adError.getErrorCode() + "] " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (nativeAd != ad)
                    return;

                com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess(com.partharoypc.adglide.util.Constant.AD_NETWORK_META, adUnitId);
                View adView = inflateAndPopulateAdView(activity, (NativeAd) ad, config);
                listener.onAdLoaded(adView);
            }

            @Override
            public void onAdClicked(Ad ad) {
                listener.onAdClicked();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                listener.onAdShowed();
            }
        };
        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
    }

    private View inflateAndPopulateAdView(Activity activity, NativeAd ad, NativeConfig config) {
        NativeAdLayout nativeAdLayout = new NativeAdLayout(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);

        int layoutRes = getLayoutForStyle(config.getStyle());
        LinearLayout nativeAdView = (LinearLayout) inflater.inflate(layoutRes, nativeAdLayout, false);
        nativeAdLayout.addView(nativeAdView);

        // AdChoices
        LinearLayout adChoicesContainer = nativeAdView.findViewById(R.id.ad_choices_container);
        if (adChoicesContainer != null) {
            AdOptionsView adOptionsView = new AdOptionsView(activity, ad, nativeAdLayout);
            adChoicesContainer.removeAllViews();
            adChoicesContainer.addView(adOptionsView, 0);
        }

        TextView nativeAdTitle = nativeAdView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = nativeAdView.findViewById(R.id.native_ad_media);
        com.facebook.ads.MediaView nativeAdIcon = nativeAdView.findViewById(R.id.native_ad_icon);
        TextView nativeAdSocialContext = nativeAdView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = nativeAdView.findViewById(R.id.native_ad_body);
        Button nativeAdCallToAction = nativeAdView.findViewById(R.id.native_ad_call_to_action);

        if (nativeAdTitle != null)
            nativeAdTitle.setText(ad.getAdvertiserName());
        if (nativeAdBody != null)
            nativeAdBody.setText(ad.getAdBodyText());
        if (nativeAdSocialContext != null)
            nativeAdSocialContext.setText(ad.getAdSocialContext());
        if (nativeAdCallToAction != null) {
            nativeAdCallToAction.setVisibility(ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdCallToAction.setText(ad.getAdCallToAction());
        }

        List<View> clickableViews = new ArrayList<>();
        if (nativeAdTitle != null)
            clickableViews.add(nativeAdTitle);
        if (nativeAdIcon != null)
            clickableViews.add(nativeAdIcon);
        if (nativeAdMedia != null)
            clickableViews.add(nativeAdMedia);
        if (nativeAdBody != null)
            clickableViews.add(nativeAdBody);
        if (nativeAdSocialContext != null)
            clickableViews.add(nativeAdSocialContext);
        if (nativeAdCallToAction != null)
            clickableViews.add(nativeAdCallToAction);

        ad.registerViewForInteraction(nativeAdView, nativeAdIcon, nativeAdMedia, clickableViews);

        return nativeAdLayout;
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "medium":
            case "banner":
                return R.layout.adglide_meta_news_template_view;
            case "video":
                return R.layout.adglide_meta_video_large_template_view;
            case "small":
            default:
                return R.layout.adglide_meta_radio_template_view;
        }
    }

    @Override
    public void destroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }
}
