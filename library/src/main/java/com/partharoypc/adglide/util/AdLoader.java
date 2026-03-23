package com.partharoypc.adglide.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;

import java.util.List;

/**
 * Centralized loader for all ad formats.
 * Handles network availability, status checks, secure waterfall logic, and timeouts.
 */
public class AdLoader {
    private static final String TAG = "AdGlide";

    /**
     * Interface to be implemented by individual Ad Formats to execute the actual provider load.
     */
    public interface NetworkExecutor {
        void loadNetwork(String network, LoadResultCallback resultCallback);
    }

    /**
     * Callback passed to format executors.
     */
    public interface LoadResultCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final Activity activity;
    private final AdFormat format;
    private final WaterfallManager waterfallManager;
    private final String primaryNetwork;
    private final long timeoutMs;
    private long startTime;

    public AdLoader(@NonNull Activity activity, AdFormat format) {
        this.activity = activity;
        this.format = format;
        AdGlideConfig config = AdGlide.getConfig();
        if (config != null) {
            String primary = config.getPrimaryNetwork();
            this.primaryNetwork = (primary != null) ? primary : "";
            List<String> backups = config.getBackupNetworks();
            this.waterfallManager = new WaterfallManager((backups != null) ? backups : new java.util.ArrayList<>());
            
            // Use format-specific default if config is at its base default of 3500ms
            long configTimeout = config.getAdResponseTimeoutMs();
            if (configTimeout == 3500 && (format == AdFormat.REWARDED || format == AdFormat.REWARDED_INTERSTITIAL)) {
                this.timeoutMs = 10000; // 10 seconds for video ads
            } else {
                this.timeoutMs = configTimeout;
            }
        } else {
            this.primaryNetwork = "";
            this.waterfallManager = new WaterfallManager();
            this.timeoutMs = (format == AdFormat.REWARDED || format == AdFormat.REWARDED_INTERSTITIAL) ? 10000 : 3500;
        }
    }

    public boolean isEnabled() {
        AdGlideConfig config = AdGlide.getConfig();
        if (config == null || !config.getAdStatus()) return false;

        return switch (format) {
            case BANNER -> config.isBannerEnabled();
            case INTERSTITIAL -> config.isInterstitialEnabled();
            case REWARDED -> config.isRewardedEnabled();
            case REWARDED_INTERSTITIAL -> config.isRewardedInterstitialEnabled();
            case NATIVE -> config.isNativeEnabled();
            case APP_OPEN -> config.isAppOpenEnabled();
            default -> false;
        };
    }

    /**
     * Initiates the loading process starting from the primary network.
     */
    public void startLoading(NetworkExecutor executor, AdGlideCallback finalCallback) {
        if (!isEnabled()) {
            Log.d(TAG, format + " Ad is disabled globally or locally.");
            if (finalCallback != null) finalCallback.onAdDismissed();
            return;
        }

        boolean hasInternet = Tools.isNetworkAvailable(activity);
        if (!hasInternet) {
            Log.d(TAG, "No internet connection detected for " + format + ". Checking for offline-capable House Ads.");
            AdGlideConfig config = AdGlide.getConfig();
            if (config != null && config.isHouseAdEnabled()) {
                // If offline but House Ads are enabled, we force the waterfall to ONLY include house_ad.
                // This allows the SDK to show a cached/offline-ready house ad if available.
                waterfallManager.setNetworks(java.util.Collections.singletonList(com.partharoypc.adglide.util.Constant.HOUSE_AD));
                waterfallManager.reset();
                this.startTime = System.currentTimeMillis();
                executeNetwork(waterfallManager.getNext(), executor, finalCallback);
                return;
            } else {
                Log.e(TAG, "Internet connection not available and House Ads not enabled for " + format);
                if (finalCallback != null) finalCallback.onAdFailedToLoad("No internet");
                return;
            }
        }

        waterfallManager.reset();
        this.startTime = System.currentTimeMillis();
        executeNetwork(primaryNetwork, executor, finalCallback);
    }

    private void executeNetwork(String network, NetworkExecutor executor, AdGlideCallback finalCallback) {
        if (network == null || network.isEmpty()) {
            executeNext(executor, finalCallback);
            return;
        }

        Log.d(TAG, "Initiating load for " + format + " on [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
        final boolean[] responded = {false};
        Handler handler = new Handler(Looper.getMainLooper());
        
        Runnable timeoutRunnable = () -> {
            if (!responded[0]) {
                responded[0] = true;
                Log.e(TAG, "Timeout (" + timeoutMs + "ms) reached waiting for " + format + " on [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
                executeNext(executor, finalCallback);
            }
        };
        
        handler.postDelayed(timeoutRunnable, timeoutMs);

        executor.loadNetwork(network, new LoadResultCallback() {
            @Override
            public void onSuccess() {
                if (responded[0]) return;
                responded[0] = true;
                handler.removeCallbacks(timeoutRunnable);

                long duration = System.currentTimeMillis() - startTime;
                com.partharoypc.adglide.AdGlide.AdGlideListener listener = com.partharoypc.adglide.AdGlide.getListener();
                if (listener != null) {
                    listener.onPerformanceMetrics(format != null ? format.name() : "UNKNOWN", duration);
                    listener.onAdStatusChanged(format != null ? format.name() : "UNKNOWN", network, "LOADED");
                }

                if (finalCallback != null) finalCallback.onAdLoaded(network);
            }

            @Override
            public void onFailure(String error) {
                if (responded[0]) return;
                responded[0] = true;
                handler.removeCallbacks(timeoutRunnable);
                Log.d(TAG, format + " load failed on [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                
                com.partharoypc.adglide.AdGlide.AdGlideListener listener = com.partharoypc.adglide.AdGlide.getListener();
                if (listener != null) {
                    listener.onAdStatusChanged(format != null ? format.name() : "UNKNOWN", network, "FAILED: " + error);
                }

                executeNext(executor, finalCallback);
            }
        });
    }

    private void executeNext(NetworkExecutor executor, AdGlideCallback finalCallback) {
        if (waterfallManager.hasNext()) {
            String nextNetwork = waterfallManager.getNext();
            executeNetwork(nextNetwork, executor, finalCallback);
        } else {
            Log.d(TAG, "All waterfall backups exhausted for " + format);
            if (finalCallback != null) finalCallback.onAdFailedToLoad("Backup exhausted for " + format);
        }
    }
}
