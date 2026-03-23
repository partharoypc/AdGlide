package com.partharoypc.adglide.provider.housead;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.util.ImageDownloader;

public class HouseAdNativeProvider implements NativeProvider {

    private View adView;
    private Bitmap mainBitmap;
    private Bitmap iconBitmap;

    @Override
    public void loadNativeAd(Activity activity, String adUnitId, NativeConfig config, NativeListener listener) {
        AdGlideConfig adGlideConfig = AdGlide.getConfig();
        if (adGlideConfig == null || !adGlideConfig.isHouseAdEnabled()) {
            listener.onAdFailedToLoad("House Ad disabled or not configured");
            return;
        }

        // We need to load main image and icon (if specified)
        String mainImageUrl = adGlideConfig.getHouseAdNativeImage();
        String iconUrl = adGlideConfig.getHouseAdNativeIcon();

        if (mainImageUrl == null || mainImageUrl.isEmpty()) {
            listener.onAdFailedToLoad("House Ad native image is missing");
            return;
        }

        ImageDownloader.downloadImage(activity, mainImageUrl, new ImageDownloader.ImageLoaderCallback() {
            @Override
            public void onImageLoaded(Bitmap mainBmp) {
                mainBitmap = mainBmp;
                if (iconUrl != null && !iconUrl.isEmpty()) {
                    ImageDownloader.downloadImage(activity, iconUrl, new ImageDownloader.ImageLoaderCallback() {
                        @Override
                        public void onImageLoaded(Bitmap iconBmp) {
                            iconBitmap = iconBmp;
                            inflateAndPopulate(activity, config, listener);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Icon failure is not fatal
                            inflateAndPopulate(activity, config, listener);
                        }
                    });
                } else {
                    inflateAndPopulate(activity, config, listener);
                }
            }

            @Override
            public void onError(Exception e) {
                listener.onAdFailedToLoad("Failed to load main image: " + e.getMessage());
            }
        });
    }

    private void inflateAndPopulate(Activity activity, NativeConfig config, NativeListener listener) {
        AdGlideConfig adGlideConfig = AdGlide.getConfig();
        LayoutInflater inflater = LayoutInflater.from(activity);
        int layoutRes = getLayoutForStyle(config.getStyle());

        adView = inflater.inflate(layoutRes, null);
        
        // Populate standard views
        TextView titleView = adView.findViewById(R.id.primary);
        TextView bodyView = adView.findViewById(R.id.body);
        Button ctaButton = adView.findViewById(R.id.cta);
        ImageView iconView = adView.findViewById(R.id.icon);
        ViewGroup mediaContainer = adView.findViewById(R.id.media_view);

        if (titleView != null) titleView.setText(adGlideConfig.getHouseAdNativeTitle());
        if (bodyView != null) bodyView.setText(adGlideConfig.getHouseAdNativeDescription());
        if (ctaButton != null) {
            ctaButton.setText(adGlideConfig.getHouseAdNativeCTA());
            ctaButton.setOnClickListener(v -> handleAdClick(activity, adGlideConfig.getHouseAdNativeClickUrl()));
        }
        
        if (iconView != null && iconBitmap != null) {
            iconView.setImageBitmap(iconBitmap);
            iconView.setVisibility(View.VISIBLE);
        }

        if (mediaContainer != null && mainBitmap != null) {
            ImageView mainImageView = new ImageView(activity);
            mainImageView.setImageBitmap(mainBitmap);
            mainImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mediaContainer.removeAllViews();
            mediaContainer.addView(mainImageView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        adView.setOnClickListener(v -> handleAdClick(activity, adGlideConfig.getHouseAdNativeClickUrl()));

        listener.onAdLoaded(adView);
    }

    private void handleAdClick(Activity activity, String url) {
        if (url != null && !url.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
            } catch (Exception ignored) {}
        }
    }

    private int getLayoutForStyle(String style) {
        return switch (style) {
            case "small" -> R.layout.adglide_native_small;
            case "video" -> R.layout.adglide_native_video;
            default -> R.layout.adglide_native_medium;
        };
    }

    @Override
    public void destroy() {
        if (mainBitmap != null && !mainBitmap.isRecycled()) mainBitmap.recycle();
        if (iconBitmap != null && !iconBitmap.isRecycled()) iconBitmap.recycle();
        mainBitmap = null;
        iconBitmap = null;
        adView = null;
    }
}
