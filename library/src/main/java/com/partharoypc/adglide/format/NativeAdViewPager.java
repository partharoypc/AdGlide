import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.partharoypc.adglide.AdGlideNetwork;
import com.partharoypc.adglide.AdGlideNativeStyle;
import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.provider.NativeProviderFactory;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import static com.partharoypc.adglide.util.Constant.*;

/**
 * Handles loading and displaying native ads within a ViewPager layout.
 * Uses a Builder pattern for configuration and the Provider architecture.
 */
public class NativeAdViewPager {

    public static class Builder implements NativeProvider.NativeConfig {

        private static final String TAG = "AdGlide";
        private final Activity activity;
        private final View view;

        private ViewGroup adContainerView;
        private View currentAdView;
        private ProgressBar progressBarAd;

        private NativeProvider currentProvider;

        private boolean adStatus = true;
        private String adNetwork = "";
        private String backupAdNetwork = "";
        private WaterfallManager waterfallManager;

        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String wortiseNativeId = "";

        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;
        private String nativeAdStyle = "";

        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(Activity activity, View view) {
            this.activity = activity;
            this.view = view;
            initializeViews();
        }

        private void initializeViews() {
            // Find a generic container if possible, or fallback to the stub containers
            adContainerView = view.findViewById(R.id.native_ad_view_container);
            progressBarAd = view.findViewById(R.id.progress_bar_ad);

            if (adContainerView == null) {
                // If the generic container isn't found, we might need to rely on the layout
                // structure directly
                // For ViewPager, assume the root of the provided view acts as the container if
                // needed.
                if (view instanceof ViewGroup) {
                    adContainerView = (ViewGroup) view;
                }
            }
        }

        @NonNull
        public Builder build() {
            return this;
        }

        @NonNull
        public Builder load() {
            loadNativeAdMain(false);
            return this;
        }

        @NonNull
        public Builder status(boolean adStatus) {
            this.adStatus = adStatus;
            return this;
        }

        @NonNull
        public Builder network(@NonNull String adNetwork) {
            this.adNetwork = adNetwork;
            return this;
        }

        @NonNull
        public Builder network(AdGlideNetwork network) {
            return network(network.getValue());
        }

        @Nullable
        public Builder backup(@Nullable String backupAdNetwork) {
            this.backupAdNetwork = backupAdNetwork;
            this.waterfallManager = new WaterfallManager(backupAdNetwork);
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
                this.backupAdNetwork = backupAdNetworks[0];
            }
            return this;
        }

        @Nullable
        public Builder backups(AdGlideNetwork... backupAdNetworks) {
            return backups(AdGlideNetwork.toStringArray(backupAdNetworks));
        }

        @NonNull
        public Builder adMobId(@NonNull String adMobNativeId) {
            this.adMobNativeId = adMobNativeId;
            return this;
        }

        @NonNull
        public Builder metaId(@NonNull String metaNativeId) {
            this.metaNativeId = metaNativeId;
            return this;
        }

        @NonNull
        public Builder appLovinId(@NonNull String appLovinNativeId) {
            this.appLovinNativeId = appLovinNativeId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
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
        public Builder setLegacyGDPR(boolean legacyGDPR) {
            this.legacyGDPR = legacyGDPR;
            return this;
        }

        @NonNull
        public Builder setNativeAdBackgroundColor(int colorLight, int colorDark) {
            this.nativeBackgroundLight = colorLight;
            this.nativeBackgroundDark = colorDark;
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

        @Override
        public boolean isDarkTheme() {
            return darkTheme;
        }

        @Override
        public boolean isLegacyGDPR() {
            return legacyGDPR;
        }

        public String getNativeAdStyle() {
            return nativeAdStyle;
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

        private void loadNativeAdMain(boolean isBackup) {
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Native Ad load (ViewPager).");
                        hideProgressBar();
                        return;
                    }

                    String network;
                    if (!isBackup) {
                        if (waterfallManager != null)
                            waterfallManager.reset();
                        network = adNetwork;
                    } else {
                        network = waterfallManager != null ? waterfallManager.getNext() : backupAdNetwork;
                    }

                    if (network == null || network.isEmpty() || network.equals("none")) {
                        Log.d(TAG, "No native ad network available or specified");
                        hideProgressBar();
                        return;
                    }

                    String adUnitId = getAdUnitIdForNetwork(network);

                    Runnable fallbackAction = () -> {
                        if (waterfallManager != null && waterfallManager.hasNext()) {
                            loadNativeAdMain(true);
                        } else {
                            hideProgressBar();
                        }
                    };

                    if (adUnitId == null || adUnitId.isEmpty() || adUnitId.equals("0")) {
                        fallbackAction.run();
                        return;
                    }

                    showProgressBar();

                    NativeProvider provider = NativeProviderFactory.getProvider(network);
                    if (provider != null) {
                        destroyAd();
                        currentProvider = provider;
                        provider.loadNativeAd(activity, adUnitId, this, new NativeProvider.NativeListener() {
                            @Override
                            public void onAdLoaded(View adView) {
                                displayAdView(adView);
                                hideProgressBar();
                            }

                            @Override
                            public void onAdFailedToLoad(String error) {
                                Log.e(TAG, "Native ViewPager Ad failed to load for " + network + ": " + error);
                                fallbackAction.run();
                            }
                        });
                    } else {
                        Log.w(TAG, "No NativeProvider available in ViewPager for " + network);
                        fallbackAction.run();
                    }
                } else {
                    hideProgressBar();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadNativeAdMain (ViewPager): " + e.getMessage());
                hideProgressBar();
            }
        }

        private String getAdUnitIdForNetwork(String network) {
            switch (network) {
                case ADMOB:
                case META_BIDDING_ADMOB:
                    return adMobNativeId;
                case META:
                    return metaNativeId;
                case APPLOVIN:
                case APPLOVIN_MAX:
                case META_BIDDING_APPLOVIN_MAX:
                    return appLovinNativeId;
                case WORTISE:
                    return wortiseNativeId;
                case STARTAPP:
                    return "startapp_native";
                default:
                    return "";
            }
        }

        private void displayAdView(View adView) {
            if (adContainerView != null && adView != null) {
                adContainerView.post(() -> {
                    try {
                        adContainerView.removeAllViews();

                        if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }

                        // If no generic layout was found, we simply make sure it's visible.
                        // Standardize on using `native_ad_view_container` everywhere.
                        adContainerView.addView(adView);
                        adContainerView.setVisibility(View.VISIBLE);

                        adView.setAlpha(0f);
                        adView.setVisibility(View.VISIBLE);
                        adView.animate().alpha(1f).setDuration(400).start();

                        currentAdView = adView;
                    } catch (Exception e) {
                        Log.e(TAG, "Error displaying ViewPager ad view: " + e.getMessage());
                    }
                });
            }
        }

        private void showProgressBar() {
            if (progressBarAd != null) {
                progressBarAd.setVisibility(View.VISIBLE);
            }
        }

        private void hideProgressBar() {
            if (progressBarAd != null) {
                progressBarAd.setVisibility(View.GONE);
            }
        }

        public void destroyAd() {
            if (currentProvider != null) {
                currentProvider.destroy();
                currentProvider = null;
            }
            if (adContainerView != null && currentAdView != null) {
                adContainerView.removeView(currentAdView);
                currentAdView = null;
            }
        }
    }
}
