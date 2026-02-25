package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.util.WaterfallManager;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.partharoypc.adglide.R;
import com.partharoypc.adglide.util.Constant;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.provider.NativeProviderFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying native ads within a Fragment layout.
 * Uses a Builder pattern for configuration.
 */
public class NativeAdFragment {

    private static void animateIn(View view) {
        if (view != null) {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(400).start();
        }
    }

    public static class Builder {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private View view;

        private LinearLayout nativeAdViewContainer;
        private NativeProvider currentProvider;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;
        private final Map<String, String> adUnitIds = new HashMap<>();
        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private String nativeAdStyle = "large";
        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        @androidx.annotation.NonNull
        public Builder build() {
            return this;
        }

        @androidx.annotation.NonNull
        public Builder load() {
            loadNativeAd();
            return this;
        }

        @androidx.annotation.NonNull
        public Builder padding(int left, int top, int right, int bottom) {
            setNativeAdPadding(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder margin(int left, int top, int right, int bottom) {
            setNativeAdMargin(left, top, right, bottom);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder backgroundResource(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder view(View view) {
            this.view = view;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(@androidx.annotation.NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backup(@androidx.annotation.Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @androidx.annotation.Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @androidx.annotation.Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @androidx.annotation.NonNull
        public Builder adMobId(@androidx.annotation.NonNull String adMobNativeId) {
            adUnitIds.put("admob", adMobNativeId);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder zoneId(@androidx.annotation.NonNull String appLovinDiscMrecZoneId) {
            adUnitIds.put("applovin_discovery", appLovinDiscMrecZoneId);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder metaId(@androidx.annotation.NonNull String metaNativeId) {
            adUnitIds.put("meta", metaNativeId);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder appLovinId(@androidx.annotation.NonNull String appLovinNativeId) {
            adUnitIds.put("applovin", appLovinNativeId);
            adUnitIds.put("applovin_max", appLovinNativeId);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder wortiseId(@androidx.annotation.NonNull String wortiseNativeId) {
            adUnitIds.put("wortise", wortiseNativeId);
            return this;
        }

        @androidx.annotation.NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder style(@androidx.annotation.NonNull String nativeAdStyle) {
            this.nativeAdStyle = nativeAdStyle;
            return this;
        }

        @androidx.annotation.NonNull
        public Builder style(AdGlideNativeStyle nativeAdStyle) {
            return style(nativeAdStyle.getValue());
        }

        @androidx.annotation.NonNull
        public Builder backgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
            return this;
        }

        public void loadNativeAd() {
            loadNativeAdMain(false);
        }

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Native Ad load.");
                        return;
                    }
                    if (isBackup) {
                        if (waterfallManager == null) {
                            if (!backupAdNetwork.isEmpty()) {
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
                        backupAdNetwork = networkToLoad;
                    } else if (waterfallManager != null) {
                        waterfallManager.reset();
                    }

                    String network = isBackup ? backupAdNetwork : adNetwork;
                    initializeViews();
                    loadAdFromNetwork(network);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAdMain: " + e.getMessage());
            }
        }

        private void handleAppLovinLoad(Runnable fallback) {
            loadNativeAdMain(true);
        }

        private void handleStartAppLoad(Runnable fallback) {
            loadNativeAdMain(true);
        }

        private void handleAdMobLoad(Runnable fallback) {
            loadNativeAdMain(true);
        }

        private void handleFacebookLoad(Runnable fallback) {
            loadNativeAdMain(true);
        }

        private void handleWortiseLoad(Runnable fallback) {
            loadNativeAdMain(true);
        }

        private void loadAdFromNetwork(String network) {
            NativeProvider provider = NativeProviderFactory.getProvider(network);
            if (provider == null) {
                loadBackupNativeAd();
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            if (adUnitId == null || adUnitId.trim().isEmpty() || adUnitId.equals("0")) {
                loadBackupNativeAd();
                return;
            }

            NativeProvider.NativeConfig config = new NativeProvider.NativeConfig() {
                @Override
                public String getStyle() {
                    return nativeAdStyle;
                }

                @Override
                public boolean isDarkTheme() {
                    return darkTheme;
                }

                @Override
                public boolean isLegacyGDPR() {
                    return legacyGDPR;
                }
            };

            provider.loadNativeAd(activity, adUnitId, config, new NativeProvider.NativeListener() {
                @Override
                public void onAdLoaded(View adView) {
                    displayAdView(adView);
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    loadBackupNativeAd();
                }
            });
        }

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adUnitIds.get("admob");
                case META:
                    return adUnitIds.get("meta");
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return adUnitIds.get("applovin");
                case WORTISE:
                    return adUnitIds.get("wortise");
                case STARTAPP:
                    return adUnitIds.get("startapp"); // Assuming startapp ID is also stored in the map
                default:
                    return "0";
            }
        }

        private void displayAdView(View adView) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null && adView != null) {
                nativeAdViewContainer.removeAllViews();
                nativeAdViewContainer.addView(adView);
                animateIn(nativeAdViewContainer);
            }
        }

        private void initializeViews() {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
        }

        public void loadBackupNativeAd() {
            loadNativeAdMain(true);
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setPadding(left, top, right, bottom);
            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundDark));
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, nativeBackgroundLight));
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            setMargins(nativeAdViewContainer, left, top, right, bottom);
        }

        public void setMargins(View view, int left, int top, int right, int bottom) {
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                p.setMargins(left, top, right, bottom);
                view.requestLayout();
            }
        }

        public void setNativeAdBackgroundResource(int drawableBackground) {
            nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
            nativeAdViewContainer.setBackgroundResource(drawableBackground);
        }

        /**
         * Destroys and releases all native ad resources to prevent memory leaks.
         * Should be called when the hosting Fragment is destroyed.
         */
        public void destroyNativeAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }

    }

}
