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
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.ImageDownloader;

public class HouseAdBannerProvider implements BannerProvider {

    private ImageView bannerView;

    @Override
    public void loadBanner(Activity activity, String adUnitId, BannerConfig unusedConfig, BannerListener listener) {
        // Removed redundant notifyLoadStarted call
        AdGlideConfig config = AdGlide.getConfig();
        if (config == null || !config.isHouseAdEnabled() || config.getHouseAdBannerImage() == null
                || config.getHouseAdBannerImage().isEmpty()) {
            com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("house", "HOUSE_BANNER");
            if (listener != null)
                listener.onAdFailedToLoad("House Ad banner not configured or disabled");
            return;
        }

        ImageDownloader.downloadImage(activity, config.getHouseAdBannerImage(),
                new ImageDownloader.ImageLoaderCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        if (activity.isFinishing() || activity.isDestroyed()) {
                            AdGlideLog.w("HouseAd", "Activity destroyed before House Ad banner could be shown.");
                            return;
                        }

                        bannerView = new ImageView(activity);
                        bannerView.setImageBitmap(bitmap);
                        bannerView.setScaleType(ImageView.ScaleType.FIT_XY);
                        bannerView.setBackgroundColor(Color.TRANSPARENT);

                        // Assuming standard 320x50 banner format
                        int width = ViewGroup.LayoutParams.MATCH_PARENT;
                        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                                activity.getResources().getDisplayMetrics());
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                        bannerView.setLayoutParams(params);

                        bannerView.setOnClickListener(v -> {
                            listener.onAdClicked();
                            String clickUrl = config.getHouseAdBannerClickUrl();
                            if (clickUrl != null && !clickUrl.isEmpty()) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    AdGlideLog.e("HouseAd", "Failed to open House Ad click URL: " + e.getMessage());
                                }
                            }
                        });

                        if (listener != null) {
                            com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordSuccess("house", "HOUSE_BANNER");
                            listener.onAdLoaded(bannerView);
                            listener.onAdShowed();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        com.partharoypc.adglide.util.NetworkHealer.getInstance(activity).recordFailure("house", "HOUSE_BANNER");
                        if (listener != null)
                            listener.onAdFailedToLoad(e != null ? e.getMessage() : "Unknown error downloading House Ad image");
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
