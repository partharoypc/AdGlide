package com.partharoypc.adglide.format;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.provider.NativeProviderFactory;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import static com.partharoypc.adglide.util.Constant.*;

/**
 * Handles loading and displaying native ads within a custom View.
 * Uses a Builder pattern for configuration and the Provider architecture.
 */
public class NativeAdView {

    public static class Builder implements NativeProvider.NativeConfig {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private View view;

        private NativeProvider currentProvider;
        private View currentNativeAdView;
        private ViewGroup nativeAdViewContainer;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;


        private boolean darkTheme = false;

        private String nativeAdStyle = "";
        private int nativeBackgroundLight = android.R.color.transparent;
        private int nativeBackgroundDark = android.R.color.transparent;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @Override
        public boolean isDarkTheme() {
            return darkTheme;
        }


        public int getNativeBackgroundLight() {
            return nativeBackgroundLight;
        }

        public int getNativeBackgroundDark() {
            return nativeBackgroundDark;
        }

        @Override
        public String getStyle() {
            return nativeAdStyle;
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder load() {
            loadNativeAd();
            return this;
        }

        @NonNull
        public Builder padding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        @NonNull
        public Builder margin(int left, int top, int right, int bottom) {
            setNativeAdMargin(left, top, right, bottom);
            return this;
        }

        @NonNull
        public Builder backgroundResource(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
            return this;
        }

        @NonNull
        public Builder view(View view) {
            this.view = view;
            return this;
        }

        @NonNull
        public Builder style(@NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @NonNull
        public Builder style(AdGlideNativeStyle nativeAdStyle) {
            return style(nativeAdStyle.getValue());
        }

        @NonNull
        public Builder backgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {
            try {
                if (adStatus) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Native Ad load.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
                    }
                    if (view == null) {
                        Log.e(TAG, "Native Ad View is null. Cannot find container.");
                        return;
                    }
                    nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
                    Log.d(TAG, "Native Ad is enabled");
                    loadAdFromNetwork(adNetwork);
                } else {
                    Log.d(TAG, "Native Ad is disabled");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Native Ad: " + e.getMessage());
            }
        }

        public void loadBackupNativeAd() {
            try {
                if (adStatus) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Backup Native Ad load.");
                        return;
                    }
                    if (waterfallManager == null) {
                        if (backupAdNetwork != null && !backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }

                    String networkToLoad = waterfallManager.getNext();
                    if (networkToLoad == null) {
                        Log.d(TAG, "All backup native ads failed to load");
                        return;
                    }

                    Log.d(TAG, "[" + networkToLoad + "] is selected as Backup Native Ad");
                    loadAdFromNetwork(networkToLoad);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading Backup Native Ad: " + e.getMessage());
            }
        }

        private void loadAdFromNetwork(String networkToLoad) {
            try {
                String adUnitId = getAdUnitIdForNetwork(networkToLoad);
                if (adUnitId.equals("0") || adUnitId.isEmpty()) {
                    loadBackupNativeAd();
                    return;
                }

                NativeProvider provider = NativeProviderFactory.getProvider(networkToLoad);
                if (provider != null) {
                    destroyNativeAd(); // Clean up previous attempt
                    currentProvider = provider;
                    provider.loadNativeAd(activity, adUnitId, this, new NativeProvider.NativeListener() {
                        @Override
                        public void onAdLoaded(View adView) {
                            displayAdView(adView);
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            Log.e(TAG, "Native Ad failed to load for " + networkToLoad + ": " + error);
                            loadBackupNativeAd();
                        }
                    });
                } else {
                    Log.w(TAG, "No NativeProvider available for " + networkToLoad);
                    loadBackupNativeAd();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load native ad for " + networkToLoad + ". Error: " + e.getMessage());
                loadBackupNativeAd();
            }
        }

        private static String getAdUnitIdForNetwork(String network) {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null) return "";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobNativeId();
                case META -> config.getMetaNativeId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinNativeId();
                case WORTISE -> config.getWortiseNativeId();
                case STARTAPP -> config.getStartAppId();
                default -> "";
            };
        }

        private void displayAdView(View adView) {
            activity.runOnUiThread(() -> {
                try {
                    if (nativeAdViewContainer != null) {
                        nativeAdViewContainer.removeAllViews();

                        // Handle potential parent issue if adView is reused
                        if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }

                        nativeAdViewContainer.addView(adView);
                        nativeAdViewContainer.setVisibility(View.VISIBLE);
                        currentNativeAdView = adView;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying native ad view: " + e.getMessage());
                }
            });
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                if (darkTheme) {
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
                } else {
                    nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
                }
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            setMargins(nativeAdViewContainer, left, top, right, bottom);
        }

        public void setMargins(View view, int left, int top, int right, int bottom) {
            if (view != null && view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                p.setMargins(left, top, right, bottom);
                view.requestLayout();
            }
        }

        public void setNativeAdBackgroundResource(int drawableBackground) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setBackgroundResource(drawableBackground);
            }
        }

        /**
         * Destroys and releases all native ad resources to prevent memory leaks.
         * Should be called when the hosting View is destroyed.
         */
        public void destroyNativeAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
            if (currentNativeAdView != null && nativeAdViewContainer != null) {
                nativeAdViewContainer.removeView(currentNativeAdView);
                nativeAdViewContainer.setVisibility(View.GONE);
                currentNativeAdView = null;
            }
        }
    }
}
