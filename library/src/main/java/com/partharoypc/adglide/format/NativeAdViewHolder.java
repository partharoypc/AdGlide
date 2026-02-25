import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.partharoypc.adglide.R;
import com.partharoypc.adglide.provider.NativeProvider;
import com.partharoypc.adglide.provider.NativeProviderFactory;
import com.partharoypc.adglide.util.Tools;
import com.partharoypc.adglide.util.WaterfallManager;

import static com.partharoypc.adglide.util.Constant.*;

/**
 * RecyclerView ViewHolder for displaying native ads within a list.
 * Uses the Provider architecture for dynamic loading.
 */
public class NativeAdViewHolder extends RecyclerView.ViewHolder implements NativeProvider.NativeConfig {

    private static final String TAG = "AdGlide";
    private ViewGroup nativeAdViewContainer;
    private View currentNativeAdView;
    private NativeProvider currentProvider;

    private boolean darkTheme = false;
    private boolean legacyGDPR = false;
    private String nativeAdStyle = "";
    private int nativeBackgroundLight = R.color.adglide_color_native_background_light;
    private int nativeBackgroundDark = R.color.adglide_color_native_background_dark;

    private WaterfallManager waterfallManager;

    public NativeAdViewHolder(View view) {
        super(view);
        nativeAdViewContainer = view.findViewById(R.id.native_ad_view_container);
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

    public void loadNativeAd(Context context, boolean adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String metaNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight,
            int nativeBackgroundDark) {
        this.waterfallManager = new WaterfallManager(backupAdNetwork);
        loadNativeAdMain(context, adStatus, placementStatus, adNetwork, backupAdNetwork, adMobNativeId, metaNativeId,
                appLovinNativeId, appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR, nativeAdStyle,
                nativeBackgroundLight, nativeBackgroundDark, false);
    }

    public void loadBackupNativeAd(Context context, boolean adStatus, int placementStatus, String backupAdNetwork,
            String adMobNativeId, String metaNativeId, String appLovinNativeId,
            String appLovinDiscMrecZoneId, String wortiseNativeId, boolean darkTheme,
            boolean legacyGDPR, String nativeAdStyle, int nativeBackgroundLight, int nativeBackgroundDark) {
        loadNativeAdMain(context, adStatus, placementStatus, "", backupAdNetwork, adMobNativeId, metaNativeId,
                appLovinNativeId, appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR, nativeAdStyle,
                nativeBackgroundLight, nativeBackgroundDark, true);
    }

    private void loadNativeAdMain(Context context, boolean adStatus, int placementStatus, String adNetwork,
            String backupAdNetwork, String adMobNativeId, String metaNativeId,
            String appLovinNativeId, String appLovinDiscMrecZoneId, String wortiseNativeId,
            boolean darkTheme, boolean legacyGDPR, String nativeAdStyle, int backgroundLight,
            int backgroundDark, boolean isBackup) {

        this.darkTheme = darkTheme;
        this.legacyGDPR = legacyGDPR;
        this.nativeAdStyle = nativeAdStyle;
        this.nativeBackgroundLight = backgroundLight;
        this.nativeBackgroundDark = backgroundDark;

        try {
            if (adStatus && placementStatus != 0) {
                if (!Tools.isNetworkAvailable(context)) {
                    Log.e(TAG, "Internet connection not available.");
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
                    if (nativeAdViewContainer != null) {
                        nativeAdViewContainer.setVisibility(View.GONE);
                    }
                    return;
                }

                String adUnitId = getAdUnitIdForNetwork(network, adMobNativeId, metaNativeId, appLovinNativeId,
                        wortiseNativeId);

                Runnable fallbackAction = () -> {
                    if (waterfallManager != null && waterfallManager.hasNext()) {
                        loadBackupNativeAd(context, adStatus, placementStatus, backupAdNetwork,
                                adMobNativeId, metaNativeId, appLovinNativeId,
                                appLovinDiscMrecZoneId, wortiseNativeId, darkTheme, legacyGDPR,
                                nativeAdStyle, backgroundLight, backgroundDark);
                    } else if (nativeAdViewContainer != null) {
                        nativeAdViewContainer.setVisibility(View.GONE);
                    }
                };

                if (adUnitId == null || adUnitId.equals("0") || adUnitId.isEmpty()) {
                    fallbackAction.run();
                    return;
                }

                NativeProvider provider = NativeProviderFactory.getProvider(network);
                if (provider != null) {
                    destroyAd(); // Clear out pre-existing ads
                    currentProvider = provider;
                    provider.loadNativeAd((Activity) context, adUnitId, this, new NativeProvider.NativeListener() {
                        @Override
                        public void onAdLoaded(View adView) {
                            displayAdView(adView);
                        }

                        @Override
                        public void onAdFailedToLoad(String error) {
                            Log.e(TAG, "Native ViewHolder Ad failed to load for " + network + ": " + error);
                            fallbackAction.run();
                        }
                    });
                } else {
                    Log.w(TAG, "No NativeProvider available in ViewHolder for " + network);
                    fallbackAction.run();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadNativeAdMain (ViewHolder): " + e.getMessage());
        }
    }

    private String getAdUnitIdForNetwork(String network, String adMobNativeId, String metaNativeId,
            String appLovinNativeId, String wortiseNativeId) {
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
        if (nativeAdViewContainer != null && adView != null) {
            nativeAdViewContainer.post(() -> {
                try {
                    nativeAdViewContainer.removeAllViews();

                    if (adView.getParent() != null && adView.getParent() instanceof ViewGroup) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    nativeAdViewContainer.addView(adView);
                    nativeAdViewContainer.setVisibility(View.VISIBLE);
                    animateIn(adView);
                    currentNativeAdView = adView;
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying ad view in ViewHolder: " + e.getMessage());
                }
            });
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
        if (nativeAdViewContainer != null) {
            nativeAdViewContainer.setPadding(left, top, right, bottom);
        }
    }

    public void setNativeAdMargin(int left, int top, int right, int bottom) {
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
        if (nativeAdViewContainer != null) {
            nativeAdViewContainer.setBackgroundResource(drawableBackground);
        }
    }

    public void setNativeAdBackgroundColor(Context context, boolean darkTheme, int nativeBackgroundLight,
            int nativeBackgroundDark) {
        if (nativeAdViewContainer != null) {
            if (darkTheme) {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundDark));
            } else {
                nativeAdViewContainer.setBackgroundColor(ContextCompat.getColor(context, nativeBackgroundLight));
            }
        }
    }

    public void destroyAd() {
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
