package com.partharoypc.adglide.provider.housead;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.provider.InterstitialProvider;
import com.partharoypc.adglide.util.ImageDownloader;

public class HouseAdInterstitialProvider implements InterstitialProvider {

    private Bitmap cachedAdImage = null;
    private AdGlideConfig config;

    @Override
    public void loadInterstitial(Activity activity, String adUnitId, InterstitialConfig unusedConfig,
            InterstitialListener listener) {
        this.config = AdGlide.getConfig();
        if (config == null || !config.isHouseAdEnabled() || config.getHouseAdInterstitialImage() == null
                || config.getHouseAdInterstitialImage().isEmpty()) {
            if (listener != null)
                listener.onAdFailedToLoad("House Ad interstitial not configured or disabled");
            return;
        }

        ImageDownloader.downloadImage(activity, config.getHouseAdInterstitialImage(),
                new ImageDownloader.ImageLoaderCallback() {
                    @Override
                    public void onImageLoaded(Bitmap bitmap) {
                        cachedAdImage = bitmap;
                        if (listener != null)
                            listener.onAdLoaded();
                    }

                    @Override
                    public void onError(Exception e) {
                        if (listener != null)
                            listener.onAdFailedToLoad(e.getMessage());
                    }
                });
    }

    @Override
    public void showInterstitial(Activity activity, InterstitialListener listener) {
        if (cachedAdImage == null) {
            if (listener != null)
                listener.onAdShowFailed("House ad image not loaded");
            return;
        }

        Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        RelativeLayout layout = new RelativeLayout(activity);
        layout.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.setBackgroundColor(Color.BLACK);

        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(cachedAdImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imgParams);

        imageView.setOnClickListener(v -> {
            if (config != null && config.getHouseAdInterstitialClickUrl() != null
                    && !config.getHouseAdInterstitialClickUrl().isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(config.getHouseAdInterstitialClickUrl()));
                    activity.startActivity(intent);
                } catch (Exception ignored) {
                }
            }
            dialog.dismiss();
        });

        // Close button (Basic top right X)
        ImageView closeButton = new ImageView(activity);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setBackgroundColor(Color.parseColor("#80000000")); // semi-transparent black background
        closeButton.setPadding(32, 32, 32, 32);
        RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        closeParams.setMargins(0, 48, 48, 0);
        closeButton.setLayoutParams(closeParams);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        layout.addView(imageView);
        layout.addView(closeButton);
        dialog.setContentView(layout);

        dialog.setOnDismissListener(d -> {
            if (listener != null) {
                listener.onAdDismissed();
            }
        });

        dialog.show();

        if (listener != null) {
            listener.onAdShowed();
        }
    }

    @Override
    public boolean isAdLoaded() {
        return cachedAdImage != null;
    }

    @Override
    public void destroy() {
        if (cachedAdImage != null && !cachedAdImage.isRecycled()) {
            cachedAdImage.recycle();
        }
        cachedAdImage = null;
    }
}
