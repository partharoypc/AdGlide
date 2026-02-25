package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.provider.NativeProviderFactory;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying native ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class NativeAd {

    public static class Builder {
        private static final String TAG = "AdGlide.Native";
        private final Activity activity;
        private LinearLayout nativeAdViewContainer;

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

        private NativeProvider currentProvider;

        public Builder(Activity activity) {
            this.activity = activity;
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
        public Builder background(int drawableBackground) {
            setNativeAdBackgroundResource(drawableBackground);
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = AdGlideNetwork.fromString(adNetwork).getValue();
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetwork).getValue();
            this.waterfallManager = new WaterfallManager(this.backupAdNetwork);
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return backup(backupAdNetwork.getValue());
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            this.waterfallManager = new WaterfallManager(backupAdNetworks);
            if (backupAdNetworks.length > 0) {
                this.backupAdNetwork = AdGlideNetwork.fromString(backupAdNetworks[0]).getValue();
            }
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobNativeId) {
            adUnitIds.put("admob", adMobNativeId);
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaNativeId) {
            adUnitIds.put("meta", metaNativeId);
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinNativeId) {
            adUnitIds.put("applovin", appLovinNativeId);
            adUnitIds.put("applovin_max", appLovinNativeId);
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String appLovinDiscMrecZoneId) {
            adUnitIds.put("applovin_discovery", appLovinDiscMrecZoneId);
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseNativeId) {
            adUnitIds.put("wortise", wortiseNativeId);
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String startAppId) {
            adUnitIds.put("startapp", startAppId);
            return this;
        }

        @NonNull
        public Builder placement(int placementStatus) {
            this.placementStatus = placementStatus;
            return this;
        }

        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
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
            loadNativeAdMain(false);
        }

        public void loadBackupNativeAd() {
            loadNativeAdMain(true);
        }

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (!adStatus || placementStatus == 0)
                    return;

                if (!Tools.isNetworkAvailable(activity)) {
                    Log.e(TAG, "Internet connection not available.");
                    return;
                }

                String network;
                if (isBackup) {
                    if (waterfallManager == null) {
                        if (!backupAdNetwork.isEmpty()) {
                            waterfallManager = new WaterfallManager(backupAdNetwork);
                        } else {
                            return;
                        }
                    }
                    network = waterfallManager.getNext();
                    if (network == null) {
                        Log.d(TAG, "All backup native ads failed to load");
                        return;
                    }
                } else {
                    network = adNetwork;
                    if (waterfallManager != null)
                        waterfallManager.reset();
                }

                if (network.equals(NONE)) {
                    loadBackupNativeAd();
                    return;
                }

                loadAdFromNetwork(network);

            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAdMain: " + e.getMessage());
                if (!isBackup)
                    loadBackupNativeAd();
            }
        }

        private void loadAdFromNetwork(String network) {
            destroyNativeAd();
            NativeProvider provider = NativeProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadBackupNativeAd();
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            Log.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Native Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
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
                    Log.e(TAG, network + " Native failed: " + error);
                    loadBackupNativeAd();
                }
            });
        }

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adUnitIds.get(ADMOB);
                case META:
                    return adUnitIds.get(META);
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return adUnitIds.get(APPLOVIN);
                case WORTISE:
                    return adUnitIds.get(WORTISE);
                case STARTAPP:
                    return adUnitIds.get(STARTAPP) != null ? adUnitIds.get(STARTAPP) : "startapp_id";
                case IRONSOURCE:
                case com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE:
                    return adUnitIds.get(com.partharoypc.adglide.util.Constant.IRONSOURCE);
                default:
                    return "0";
            }
        }

        private void displayAdView(View adView) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null && adView != null) {
                nativeAdViewContainer.removeAllViews();
                nativeAdViewContainer.addView(adView);
                animateIn(nativeAdViewContainer);
            }
        }

        private void animateIn(View view) {
            if (view != null) {
                view.setAlpha(0f);
                view.setVisibility(View.VISIBLE);
                view.animate().alpha(1f).setDuration(400).start();
            }
        }

        public void setNativeAdPadding(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                int colorRes = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(activity, colorRes));
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                if (nativeAdViewContainer.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) nativeAdViewContainer
                            .getLayoutParams();
                    p.setMargins(left, top, right, bottom);
                    nativeAdViewContainer.requestLayout();
                }
            }
        }

        public void setNativeAdBackgroundResource(int drawableBackground) {
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setBackgroundResource(drawableBackground);
            }
        }

        public void destroyNativeAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
        }
    }
}
