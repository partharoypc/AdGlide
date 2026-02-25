package com.partharoypc.adglide.provider.startapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import java.util.ArrayList;

public class StartAppNativeProvider implements NativeProvider {
    private StartAppNativeAd startAppNativeAd;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        startAppNativeAd = new StartAppNativeAd(activity);
        NativeAdPreferences nativePrefs = new NativeAdPreferences()
                .setAdsNumber(1)
                .setAutoBitmapDownload(true)
                .setPrimaryImageSize(2);

        AdEventListener adEventListener = new AdEventListener() {
            @Override
            public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();
                if (ads != null && !ads.isEmpty()) {
                    NativeAdDetails details = ads.get(0);
                    View adView = inflateAndPopulateAdView(activity, details, config);
                    listener.onAdLoaded(adView);
                } else {
                    listener.onAdFailedToLoad("StartApp: No ads received");
                }
            }

            @Override
            public void onFailedToReceiveAd(com.startapp.sdk.adsbase.Ad ad) {
                listener.onAdFailedToLoad("StartApp: Failed to receive ad");
            }
        };
        startAppNativeAd.loadAd(nativePrefs, adEventListener);
    }

    private View inflateAndPopulateAdView(Activity activity, NativeAdDetails details, NativeConfig config) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layoutRes = getLayoutForStyle(config.getStyle());
        View root = inflater.inflate(layoutRes, null);

        ImageView image = root.findViewById(R.id.start_app_native_image);
        ImageView icon = root.findViewById(R.id.start_app_native_icon);
        TextView title = root.findViewById(R.id.start_app_native_title);
        TextView description = root.findViewById(R.id.start_app_native_description);
        Button button = root.findViewById(R.id.start_app_native_button);

        if (title != null)
            title.setText(details.getTitle());
        if (description != null)
            description.setText(details.getDescription());
        if (image != null)
            image.setImageBitmap(details.getImageBitmap());
        if (icon != null)
            icon.setImageBitmap(details.getSecondaryImageBitmap());
        if (button != null) {
            button.setText(details.isApp() ? "Install" : "Open");
            button.setOnClickListener(v -> root.performClick());
        }

        details.registerViewForInteraction(root);
        return root;
    }

    private int getLayoutForStyle(String style) {
        switch (style) {
            case "small":
                return R.layout.adglide_start_app_radio_template_view;
            case "medium":
                return R.layout.adglide_start_app_medium_template_view;
            case "video_small":
                return R.layout.adglide_start_app_video_small_template_view;
            case "video_large":
                return R.layout.adglide_start_app_video_large_template_view;
            case "news":
                return R.layout.adglide_start_app_news_template_view;
            default:
                return R.layout.adglide_start_app_large_template_view;
        }
    }

    @Override
    public void destroy() {
        startAppNativeAd = null;
    }
}
