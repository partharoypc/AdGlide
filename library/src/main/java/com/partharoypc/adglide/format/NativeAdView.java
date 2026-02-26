import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

        private String adMobNativeId = "";
        private String metaNativeId = "";
        private String appLovinNativeId = "";
        private String appLovinDiscMrecZoneId = "";
        private String wortiseNativeId = "";
        private String startAppNativeId = "";

        private int placementStatus = 1;
        private boolean darkTheme = false;
        private boolean legacyGDPR = false;

        private String nativeAdStyle = "";
        private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
        private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

        public Builder(Activity activity) {
            this.activity = activity;
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
            if (waterfallManager == null) {
                waterfallManager = new WaterfallManager(backupAdNetwork);
            } else {
                waterfallManager.getNetworks().add(backupAdNetwork);
            }
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
        public Builder zoneId(@NonNull String appLovinDiscMrecZoneId) {
            this.appLovinDiscMrecZoneId = appLovinDiscMrecZoneId;
            return this;
        }

        @NonNull
        public Builder wortiseId(@NonNull String wortiseNativeId) {
            this.wortiseNativeId = wortiseNativeId;
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
            try {
                if (adStatus && placementStatus != 0) {
                    if (!Tools.isNetworkAvailable(activity)) {
                        Log.e(TAG, "Internet connection not available. Skipping Primary Native Ad load.");
                        return;
                    }
                    if (waterfallManager != null) {
                        waterfallManager.reset();
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
                if (adStatus && placementStatus != 0) {
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
                String adUnitId = getAdUnitIdForNetwork(this, networkToLoad);
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

        private static String getAdUnitIdForNetwork(Builder builder, String network) {
            return switch (network) {
                case ADMOB, META_BIDDING_ADMOB -> builder.adMobNativeId;
                case META -> builder.metaNativeId;
                case APPLOVIN, APPLOVIN_MAX, META_BIDDING_APPLOVIN_MAX -> builder.appLovinNativeId;
                case WORTISE -> builder.wortiseNativeId;
                case STARTAPP -> "startapp_native"; // StartApp usually doesn't need unit IDs for Native
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
