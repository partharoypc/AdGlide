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
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.AdGlideCallback;
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
import com.partharoypc.adglide.util.AdFormat;
import com.partharoypc.adglide.util.AdPoolManager;
import com.partharoypc.adglide.util.WaterfallManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and displaying native ads using a Provider pattern.
 * Supports dynamic ad network loading to avoid hard dependencies.
 */
public class NativeAd {

    public static class Builder extends BaseAdBuilder<Builder> {
        private static final String TAG = "AdGlide.Native";
        private ViewGroup nativeAdViewContainer;
        private ViewGroup customContainer;
        private NativeProvider currentProvider;
        private String nativeAdStyle = "medium";
        private boolean darkTheme = false;
        private int nativeBackgroundLight;
        private int nativeBackgroundDark;
        private View preloadedAdView;
        private boolean isAdLoaded = false;

        public String getNativeStyle() {
            return nativeAdStyle;
        }



        public Builder(@NonNull android.content.Context context) {
            super(context, com.partharoypc.adglide.util.AdFormat.NATIVE);
            this.nativeBackgroundLight = ContextCompat.getColor(context, android.R.color.transparent);
            this.nativeBackgroundDark = ContextCompat.getColor(context, android.R.color.transparent);
        }

        @Override
        protected void doLoad(AdGlideCallback callback) {
            this.callback = callback;
            Activity activity = getActivity();
            if (activity == null) {
                AdGlideLog.e(TAG, "Cannot load Native Ad: Activity reference is null.");
                if (callback != null) callback.onAdFailedToLoad("Activity is null");
                return;
            }
            if (adLoader == null) return;
            showShimmer(callback);
            adLoader.startLoading((networkToLoad, resultCallback) -> {
                loadAdFromNetwork(networkToLoad, resultCallback, callback);
            }, new AdGlideCallback() {
                @Override
                public void onAdLoaded() {
                    // Shimmer removal is handled inside displayAdView which gets triggered on success
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    hideShimmer();
                    if (callback != null) callback.onAdFailedToLoad(error);
                }
            });
        }

        @Override
        protected void doShow(Activity activity, AdGlideCallback callback) {
            displayAdView(activity, callback);
        }

        public boolean isAdLoaded() {
            return isAdLoaded && preloadedAdView != null;
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



        private void showShimmer(AdGlideCallback callback) {
            Activity activity = getActivity();
            if (activity == null || activity.isFinishing()) return;
            
            ViewGroup container = customContainer != null ? customContainer : activity.findViewById(R.id.native_ad_view_container);
            if (container != null) {
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing()) return;
                    container.removeAllViews();
                    
                    int shimmerLayout = getShimmerLayoutForStyle(nativeAdStyle);
                    View shimmer = activity.getLayoutInflater().inflate(shimmerLayout, container, false);
                    
                    container.addView(shimmer);
                    container.setVisibility(View.VISIBLE);
                    com.partharoypc.adglide.helper.ShimmerHelper.startShimmer(shimmer);
                });
            }
        }

        private int getShimmerLayoutForStyle(String style) {
            if (style == null) return R.layout.adglide_shimmer_native_medium;
            
            switch (style.toLowerCase(java.util.Locale.ROOT)) {
                case "small":
                case "radio":
                    return R.layout.adglide_shimmer_native_small;
                case "banner":
                case "news":
                    return R.layout.adglide_shimmer_native_news;
                case "video":
                case "large":
                    return R.layout.adglide_shimmer_native_video;
                case "medium":
                default:
                    return R.layout.adglide_shimmer_native_medium;
            }
        }

        private void hideShimmer() {
            Activity activity = getActivity();
            if (activity == null) return;
            activity.runOnUiThread(() -> {
                ViewGroup container = customContainer != null ? customContainer : activity.findViewById(R.id.native_ad_view_container);
                if (container != null) {
                    // Recursive stop all animations
                    com.partharoypc.adglide.helper.ShimmerHelper.stopShimmer(container);
                    
                    // Legacy Cleanup: Hide ProgressBars from old templates
                    View legacyProgress = container.findViewById(R.id.progress_bar_ad);
                    if (legacyProgress != null) legacyProgress.setVisibility(View.GONE);
                    
                    container.removeAllViews();
                    container.setVisibility(View.GONE);
                }
            });
        }

        private void loadAdFromNetwork(String network, com.partharoypc.adglide.util.AdLoader.LoadResultCallback resultCallback, AdGlideCallback callback) {
            destroyNativeAd();
            NativeProvider provider = NativeProviderFactory.getProvider(network);
            if (provider == null) {
                AdGlideLog.w(TAG, "No provider available for " + network + ". Loading backup.");
                resultCallback.onFailure("Provider null");
                return;
            }

            this.currentProvider = provider;
            String adUnitId = getAdUnitIdForNetwork(network);
            AdGlideLog.d(TAG, "Loading [" + network.toUpperCase(java.util.Locale.ROOT) + "] Native Ad with ID: " + adUnitId);
            if (adUnitId == null || adUnitId.trim().isEmpty() || (adUnitId.equals("0") && !network.equals(STARTAPP))) {
                AdGlideLog.d(TAG, "Ad unit ID for " + network + " is invalid. Trying backup.");
                resultCallback.onFailure("Invalid Ad Unit ID");
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
            };

            Activity activity = getActivity();
            if (activity == null) {
                AdGlideLog.e(TAG, "Activity context is missing during Native load. Falling back to application context where possible.");
            }

            provider.loadNativeAd(activity, adUnitId, config, new NativeProvider.NativeListener() {
                @Override
                public void onAdLoaded(View adView) {
                    preloadedAdView = adView;
                    isAdLoaded = true;
                    
                    if (adLoader != null && adLoader.isTimedOut()) {
                        AdGlideLog.d(TAG, "Native ad loaded AFTER timeout. Caching as Late Fill.");
                        AdPoolManager.cacheLateFillNative(network, NativeAd.Builder.this);
                    }
                    resultCallback.onSuccess();
                    if (callback != null)
                        callback.onAdLoaded();
                    
                    // Only display immediately if a container was already provided
                    if (customContainer != null || (getActivity() != null && getActivity().findViewById(R.id.native_ad_view_container) != null)) {
                        Activity activity = getActivity();
                        if (activity != null) displayAdView(activity, callback);
                    }
                }

                @Override
                public void onAdFailedToLoad(String error) {
                    AdGlideLog.e(TAG, network + " Native failed: " + error);
                    resultCallback.onFailure(error);
                }
            });
        }

        private static String getAdUnitIdForNetwork(String network) {
            com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
            return config != null ? config.resolveAdUnitId(com.partharoypc.adglide.util.AdFormat.NATIVE, network) : "0";
        }

        private void displayAdView(Activity activity, AdGlideCallback callback) {
            if (activity == null || activity.isFinishing()) {
                AdGlideLog.e(TAG, "Activity is null or finishing, cannot display native ad.");
                return;
            }

            activity.runOnUiThread(() -> {
                if (activity.isFinishing()) return;

                if (customContainer != null) {
                    nativeAdViewContainer = customContainer;
                } else {
                    nativeAdViewContainer = (ViewGroup) activity.findViewById(R.id.native_ad_view_container);
                }

                if (nativeAdViewContainer != null && preloadedAdView != null) {
                    // Recursive stop all animations
                    com.partharoypc.adglide.helper.ShimmerHelper.stopShimmer(nativeAdViewContainer);
                    
                    // Legacy Cleanup: Hide ProgressBars from old templates
                    View legacyProgress = nativeAdViewContainer.findViewById(R.id.progress_bar_ad);
                    if (legacyProgress != null) legacyProgress.setVisibility(View.GONE);

                    // SAFE VIEW ATTACHMENT: Detach from previous parent if exists
                    if (preloadedAdView.getParent() != null) {
                        ((ViewGroup) preloadedAdView.getParent()).removeView(preloadedAdView);
                    }
                    
                    nativeAdViewContainer.removeAllViews();
                    nativeAdViewContainer.addView(preloadedAdView);
                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                    animateIn(nativeAdViewContainer);
                    if (callback != null)
                        callback.onAdShowed();
                } else {
                    AdGlideLog.w(TAG, "Native ad container or preloaded view is null. Native ad display failed.");
                    if (nativeAdViewContainer != null) {
                        hideShimmer();
                    }
                }
            });
        }

        /**
         * Attaches a pre-loaded native ad to a container.
         * Used by AdPoolManager for zero-wait display.
         */
        public void attachToContainer(ViewGroup container, AdGlideCallback callback) {
            this.customContainer = container;
            if (isAdLoaded()) {
                Activity activity = getActivity();
                if (activity != null) displayAdView(activity, callback);
            } else {
                load(callback);
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
            if (nativeAdViewContainer == null) {
                Activity activity = getActivity();
                if (activity != null) {
                    nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
                }
            }
            if (nativeAdViewContainer != null) {
                nativeAdViewContainer.setPadding(left, top, right, bottom);
                int colorRes = darkTheme ? nativeBackgroundDark : nativeBackgroundLight;
                nativeAdViewContainer.setBackgroundColor(colorRes);
            }
        }

        public void setNativeAdMargin(int left, int top, int right, int bottom) {
            if (nativeAdViewContainer == null) {
                Activity activity = getActivity();
                if (activity != null) {
                    nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
                }
            }
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
            if (nativeAdViewContainer == null) {
                Activity activity = getActivity();
                if (activity != null) {
                    nativeAdViewContainer = activity.findViewById(R.id.native_ad_view_container);
                }
            }
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
