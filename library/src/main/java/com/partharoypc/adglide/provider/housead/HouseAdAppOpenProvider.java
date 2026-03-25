package com.partharoypc.adglide.provider.housead;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.AppOpenProvider;
import com.partharoypc.adglide.util.ImageDownloader;
import androidx.core.content.ContextCompat;

public class HouseAdAppOpenProvider implements AppOpenProvider {

    private Bitmap cachedAdImage = null;
    private AdGlideConfig config;
    private boolean isShowing = false;

    @Override
    public void loadAppOpenAd(Context context, String adUnitId, AppOpenListener listener) {
        this.config = AdGlide.getConfig();
        if (config == null || !config.isHouseAdEnabled() || config.getHouseAdInterstitialImage() == null
                || config.getHouseAdInterstitialImage().isEmpty()) {
            if (listener != null)
                listener.onAdFailedToLoad("House Ad AppOpen not configured or disabled");
            return;
        }

        ImageDownloader.downloadImage(context, config.getHouseAdInterstitialImage(),
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
    public void showAppOpenAd(Activity activity, AppOpenListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            if (listener != null) listener.onAdShowFailed("Activity is invalid");
            return;
        }

        if (cachedAdImage == null) {
            if (listener != null) listener.onAdShowFailed("House ad image not loaded");
            return;
        }

        Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        FrameLayout rootLayout = new FrameLayout(activity);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rootLayout.setBackgroundColor(Color.BLACK);

        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(cachedAdImage);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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

        // Ad Label (Top Left)
        TextView adLabel = new TextView(activity);
        adLabel.setText("Ad ");
        adLabel.setTextColor(Color.WHITE);
        adLabel.setTextSize(10);
        adLabel.setTypeface(null, Typeface.BOLD);
        adLabel.setBackgroundColor(Color.parseColor("#80000000"));
        adLabel.setPadding(16, 4, 16, 4);
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.gravity = Gravity.TOP | Gravity.START;
        adLabel.setLayoutParams(labelParams);

        // Close button (Top Right)
        ImageView closeButton = new ImageView(activity);
        closeButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        closeButton.setBackgroundColor(Color.parseColor("#60000000"));
        int padding = (int) (12 * activity.getResources().getDisplayMetrics().density);
        closeButton.setPadding(padding, padding, padding, padding);
        
        int size = (int) (48 * activity.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(size, size);
        closeParams.gravity = Gravity.TOP | Gravity.END;
        closeParams.setMargins(0, padding, padding, 0);
        closeButton.setLayoutParams(closeParams);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        rootLayout.addView(imageView);
        rootLayout.addView(adLabel);
        rootLayout.addView(closeButton);
        dialog.setContentView(rootLayout);

        dialog.setOnDismissListener(d -> {
            isShowing = false;
            if (listener != null) {
                listener.onAdDismissed();
            }
        });

        try {
            isShowing = true;
            dialog.show();
            if (listener != null) {
                listener.onAdShowed();
            }
        } catch (Exception e) {
            isShowing = false;
            if (listener != null) {
                listener.onAdShowFailed(e.getMessage());
            }
        }
    }

    @Override
    public boolean isAdAvailable() {
        return cachedAdImage != null;
    }

    @Override
    public boolean isShowingAd() {
        return isShowing;
    }

    @Override
    public void destroy() {
        if (cachedAdImage != null && !cachedAdImage.isRecycled()) {
            cachedAdImage.recycle();
        }
        cachedAdImage = null;
    }
}
