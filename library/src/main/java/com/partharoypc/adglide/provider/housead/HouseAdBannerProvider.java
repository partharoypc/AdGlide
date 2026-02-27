package com.partharoypc.adglide.provider.housead;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.provider.BannerProvider;
import com.partharoypc.adglide.util.ImageDownloader;

public class HouseAdBannerProvider implements BannerProvider {

    private ImageView bannerView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig unusedConfig, BannerListener listener) {
        AdGlideConfig config = AdGlide.getConfig();
        if (config == null || !config.isHouseAdEnabled() || config.getHouseAdBannerImage() == null
                || config.getHouseAdBannerImage().isEmpty()) {
            if (listener != null)
                listener.onAdFailedToLoad("House Ad banner not configured or disabled");
            return;
        }

        ImageDownloader.downloadImage(activity, config.getHouseAdBannerImage(),
                new ImageDownloader.ImageLoaderCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        bannerView = new ImageView(activity);
                        bannerView.setImageBitmap(bitmap);
                        bannerView.setScaleType(ImageView.ScaleType.FIT_XY);
                        bannerView.setBackgroundColor(Color.TRANSPARENT);

                        // Assuming standard 320x50 banner format
                        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320,
                                activity.getResources().getDisplayMetrics());
                        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                                activity.getResources().getDisplayMetrics());
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        bannerView.setLayoutParams(params);

                        bannerView.setOnClickListener(v -> {
                            if (config.getHouseAdBannerClickUrl() != null
                                    && !config.getHouseAdBannerClickUrl().isEmpty()) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(config.getHouseAdBannerClickUrl()));
                                    activity.startActivity(intent);
                                } catch (Exception ignored) {
                                }
                            }
                        });

                        if (listener != null) {
                            listener.onAdLoaded(bannerView);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (listener != null)
                            listener.onAdFailedToLoad(e.getMessage());
                    }
                });
    }

    @Override
    public void destroy() {
        if (bannerView != null) {
            bannerView.setOnClickListener(null);
            bannerView.setImageBitmap(null);
            bannerView = null;
        }
    }
}
