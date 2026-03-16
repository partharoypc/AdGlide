package com.partharoypc.adglide.format;

import static com.partharoypc.adglide.util.Constant.ADMOB;
import static com.partharoypc.adglide.util.Constant.APPLOVIN;
import static com.partharoypc.adglide.util.Constant.APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.META;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_ADMOB;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_APPLOVIN_MAX;
import static com.partharoypc.adglide.util.Constant.META_BIDDING_IRONSOURCE;
import static com.partharoypc.adglide.util.Constant.NONE;
import static com.partharoypc.adglide.util.Constant.STARTAPP;
import static com.partharoypc.adglide.util.Constant.UNITY;
import static com.partharoypc.adglide.util.Constant.WORTISE;

import com.partharoypc.adglide.AdGlideConfig;
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
import com.partharoypc.adglide.util.AdGlideCallback;
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
        private final java.lang.ref.WeakReference<Activity> activityRef;
        private ViewGroup nativeAdViewContainer;
        private ViewGroup customContainer;
        private NativeProvider currentProvider;
        private String nativeAdStyle = "medium";
        private boolean darkTheme = false;
        private int nativeBackgroundLight;
        private int nativeBackgroundDark;

        private final com.partharoypc.adglide.util.AdLoader adLoader;

        public Builder(Activity activity) {
            this.activityRef = new java.lang.ref.WeakReference<>(activity);
            this.adLoader = new com.partharoypc.adglide.util.AdLoader(activity,
                    com.partharoypc.adglide.util.AdFormat.NATIVE);
            // Default semantic colors: Transparent
            this.nativeBackgroundLight = ContextCompat.getColor(activity, android.R.color.transparent);
            this.nativeBackgroundDark = ContextCompat.getColor(activity, android.R.color.transparent);
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder load() {
            loadNativeAd(null);
            return this;
        }

        @NonNull
        public Builder load(AdGlideCallback callback) {
            loadNativeAd(callback);
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

        // --- Deprecated chained methods. Keeping for compatibility ---

        @NonNull
        public Builder status(boolean adStatus) {
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return this;
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            return this;
        }

        @Nullable
        public Builder backup(AdGlideNetwork backupAdNetwork) {
            return this;
        }

        @Nullable
        public Builder backups(String... backupAdNetworks) {
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return this;
        }

        @NonNull
        public Builder adMobId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder zoneId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder startAppId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder ironSourceId(@NonNull String id) {
            return this;
        }

        @NonNull
        public Builder darkTheme(boolean darkTheme) {
            this.darkTheme = darkTheme;
            return this;
        }

        @NonNull
        public Builder legacyGDPR(boolean legacyGDPR) {
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

        @NonNull
        public Builder container(ViewGroup container) {
            this.customContainer = container;
            return this;
        }

        public void loadNativeAd(AdGlideCallback callback) {
            loadNativeAdMain(false, callback);
        }

        public void loadBackupNativeAd(AdGlideCallback callback) {
            loadNativeAdMain(true, callback);
        }

        private void loadNativeAdMain(boolean isBackup, AdGlideCallback callback) {
            if (adLoader == null)
                return;
            if (!isBackup) {
                adLoader.startLoading(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Native load failed: " + error);
                        if (callback != null)
                            callback.onAdFailedToLoad(error);
                    }
                });
            } else {
                adLoader.loadNext(new com.partharoypc.adglide.util.AdLoader.AdLoadCallback() {
                    @Override
                    public void onAdLoaded(String network) {
                        loadAdFromNetwork(network, callback);
                    }

                    @Override
                    public void onAdFailed(String error) {
                        Log.d(TAG, "Native backup load failed: " + error);
                        if (callback != null)
                            callback.onAdFailedToLoad(error);
                    }
                });
            }
        }

        private void loadAdFromNetwork(String network, AdGlideCallback callback) {
            destroyNativeAd();
            NativeProvider provider = NativeProviderFactory.getProvider(network);
            if (provider == null) {
                Log.w(TAG, "No provider available for " + network + ". Loading backup.");
                loadBackupNativeAd(callback);
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            Log.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Native Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                Log.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                loadBackupNativeAd(callback);
                return;
            }

            final boolean finalLegacyGDPR = com.partharoypc.adglide.AdGlide.getConfig() != null &&
                    com.partharoypc.adglide.AdGlide.getConfig().isLegacyGDPR();

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
                    return finalLegacyGDPR;
                }
            };

            Activity activity = activityRef.get();
            if (activity == null) {
                Log.e(TAG, "Activity is null. Cannot load Native from network.");
                return;
            }

            provider.loadNativeAd(activity, adUnitId, config, new NativeProvider.NativeListener() {
                @Override
                public void onAdLoaded(View adView) {
                    if (callback != null)
                        callback.onAdLoaded();
                    displayAdView(adView, callback);
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    Log.e(TAG, network + " Native failed: " + error);
                    if (callback != null)
                        callback.onAdFailedToLoad(error);
                    loadBackupNativeAd(callback);
                }
            });
        }

        private static String getAdUnitIdForNetwork(String network) {
            AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            if (config == null)
                return "0";
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> config.getAdMobNativeId();
                case META -> config.getMetaNativeId();
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> config.getAppLovinNativeId();
                case WORTISE -> config.getWortiseNativeId();
                case STARTAPP -> !config.getStartAppId().isEmpty() ? config.getStartAppId() : "startapp_id";
                case IRONSOURCE, META_BIDDING_IRONSOURCE -> config.getIronSourceNativeId();
                case UNITY -> null; // Unity does not support Native Ads
                default -> "0";
            };
        }

        private void displayAdView(View adView, AdGlideCallback callback) {
            Activity activity = activityRef.get();
            if (activity == null)
                return;

            if (customContainer != null) {
                nativeAdViewContainer = customContainer;
            } else {
                nativeAdViewContainer = (ViewGroup) activity.findViewById(R.id.native_ad_view_container);
            }

            if (nativeAdViewContainer != null && adView != null) {
                nativeAdViewContainer.removeAllViews();
                nativeAdViewContainer.addView(adView);
                animateIn(nativeAdViewContainer);
                if (callback != null)
                    callback.onAdShowed();
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
            Activity activity = activityRef.get();
            if (activity == null)
                return;
            nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                int colorRes = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
                nativeAdViewContainer.setBackgroundColor(colorRes);
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            Activity activity = activityRef.get();
            if (activity == null)
                return;
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
            Activity activity = activityRef.get();
            if (activity == null)
                return;
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
