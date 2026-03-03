package com.partharoypc.adglide.util;

import android.app.Activity;
import android.util.Log;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.AdGlideNetwork;

/**
 * Centralized loader for all ad formats.
 * Handles network availability, status checks, and waterfall logic.
 */
public class AdLoader {
    private static final String TAG = "AdGlide";

    public interface AdLoadCallback {
        void onAdLoaded(String network);

        void onAdFailed(String error);
    }

    private final Activity activity;
    private final AdFormat format;
    private final WaterfallManager waterfallManager;
    private final String primaryNetwork;

    public AdLoader(Activity activity, AdFormat format) {
        this.activity = activity;
        this.format = format;
        AdGlideConfig config = AdGlide.getConfig();
        if (config != null) {
            this.primaryNetwork = config.getPrimaryNetwork();
            this.waterfallManager = new WaterfallManager(config.getBackupNetworks());
        } else {
            this.primaryNetwork = "";
            this.waterfallManager = new WaterfallManager();
        }
    }

    public boolean isEnabled() {
        AdGlideConfig config = AdGlide.getConfig();
        if (config == null || !config.getAdStatus())
            return false;

        return switch (format) {
            case BANNER -> config.isBannerEnabled();
            case INTERSTITIAL -> config.isInterstitialEnabled();
            case REWARDED -> config.isRewardedEnabled();
            case REWARDED_INTERSTITIAL -> config.isRewardedInterstitialEnabled();
            case NATIVE -> config.isNativeEnabled();
            case APP_OPEN -> config.isAppOpenEnabled();
        };
    }

    public void startLoading(AdLoadCallback callback) {
        if (!isEnabled()) {
            Log.d(TAG, format + " Ad is disabled globally or locally.");
            if (callback != null)
                callback.onAdFailed("Ad disabled");
            return;
        }

        if (!Tools.isNetworkAvailable(activity)) {
            Log.e(TAG, "Internet connection not available for " + format);
            if (callback != null)
                callback.onAdFailed("No internet");
            return;
        }

        waterfallManager.reset();
        loadNetwork(primaryNetwork, callback);
    }

    private void loadNetwork(String network, AdLoadCallback callback) {
        Log.d(TAG, "Loading " + format + " from [" + network + "]");
        // The actual provider-specific loading is still delegated back to the Format
        // class
        // but the DECISION to load is centralized here.
        if (callback != null)
            callback.onAdLoaded(network);
    }

    public void loadNext(AdLoadCallback callback) {
        if (waterfallManager.hasNext()) {
            String nextNetwork = waterfallManager.getNext();
            loadNetwork(nextNetwork, callback);
        } else {
            Log.d(TAG, "All waterfall backups exhausted for " + format);
            if (callback != null)
                callback.onAdFailed("Backup exhausted");
        }
    }
}
