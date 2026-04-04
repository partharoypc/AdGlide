package com.partharoypc.adglide.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;


import androidx.annotation.NonNull;

import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.util.AdGlideLog;
import com.partharoypc.adglide.util.PerformanceLogger;
import com.partharoypc.adglide.util.WaterfallManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Centralized loader for all ad formats.
 * Handles network availability, status checks, secure waterfall logic, and timeouts.
 */
public class AdLoader {
    private static final String TAG = "AdGlide";
    private static final java.util.Set<String> sessionBlacklist = java.util.Collections.synchronizedSet(new java.util.HashSet<>());
    private static final java.util.Map<AdFormat, Boolean> inFlightRequests = new java.util.concurrent.ConcurrentHashMap<>();

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

    private final WeakReference<Activity> activityRef;
    private final AdFormat format;
    private final WaterfallManager waterfallManager;
    private final String primaryNetwork;
    private final long timeoutMs;
    private long startTime;
    private boolean isTimedOut = false;
    private boolean isFinished = false;

    public AdLoader(@NonNull android.content.Context context, AdFormat format) {
        if (context instanceof Activity) {
            this.activityRef = new WeakReference<>((Activity) context);
        } else {
            this.activityRef = null;
        }
        this.format = format;
        AdGlideConfig config = AdGlide.getConfig();
        if (config != null) {
            String primary = config.getPrimaryNetwork();
            this.primaryNetwork = (primary != null) ? primary : "";
            List<String> backups = config.getBackupNetworks();
            List<String> filteredBackups = new java.util.ArrayList<>();
            if (backups != null) {
                for (String b : backups) {
                    if (!b.equals(this.primaryNetwork)) filteredBackups.add(b);
                }
            }
            if (config.isHouseAdEnabled() && !filteredBackups.contains(com.partharoypc.adglide.util.Constant.HOUSE_AD) && !this.primaryNetwork.equals(com.partharoypc.adglide.util.Constant.HOUSE_AD)) {
                filteredBackups.add(com.partharoypc.adglide.util.Constant.HOUSE_AD);
            }
            this.waterfallManager = new WaterfallManager(filteredBackups);
            
            // ADAPTIVE TIMEOUTS: Higher for primary to ensure match rate, lower for backups
            long configTimeout = config.getAdResponseTimeoutMs();
            this.timeoutMs = (this.primaryNetwork != null && !this.primaryNetwork.isEmpty()) ? (configTimeout + 2000) : (configTimeout / 2);
        } else {
            this.primaryNetwork = "";
            this.waterfallManager = new WaterfallManager();
            this.timeoutMs = 4000;
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

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Initiates the loading process starting from the primary network.
     */
    public void startLoading(NetworkExecutor executor, AdGlideCallback finalCallback) {
        if (!isEnabled()) {
            AdGlideLog.d(TAG, format + " Ad is disabled globally or locally.");
            if (finalCallback != null) finalCallback.onAdDismissed();
            return;
        }

        if (Boolean.TRUE.equals(inFlightRequests.get(format))) {
            AdGlideLog.d(TAG, "A load request for " + format + " is already in progress. Skipping redundant call to maximize match rate.");
            return;
        }
        inFlightRequests.put(format, true);

        Activity activity = activityRef.get();
        if (activity == null) {
            AdGlideLog.e(TAG, "Activity for " + format + " load is null (already destroyed). Aborting.");
            inFlightRequests.put(format, false);
            if (finalCallback != null) finalCallback.onAdFailedToLoad("Activity destroyed");
            return;
        }

        AdGlide.notifyAdRequested(format.toString(), primaryNetwork);

        boolean hasInternet = Tools.isNetworkAvailable(activity);
        if (!hasInternet) {
            AdGlideLog.d(TAG, "No internet connection detected for " + format + ". Checking for offline-capable House Ads.");
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
                AdGlideLog.e(TAG, "Internet connection not available and House Ads not enabled for " + format);
                inFlightRequests.put(format, false);
                if (finalCallback != null) finalCallback.onAdFailedToLoad("No internet");
                return;
            }
        }

        waterfallManager.reset();
        this.startTime = System.currentTimeMillis();
        executeNetwork(primaryNetwork, executor, finalCallback);
    }

    private void executeNetwork(String network, NetworkExecutor executor, AdGlideCallback finalCallback) {
        if (network == null || network.isEmpty() || sessionBlacklist.contains(network)) {
            AdGlideLog.d(TAG, "Skipping " + network + " for " + format + " (session blacklisted or empty)");
            new Handler(Looper.getMainLooper()).post(() -> executeNext(executor, finalCallback));
            return;
        }

        android.content.Context context = AdGlide.getContext();
        if (context != null && !com.partharoypc.adglide.util.NetworkHealer.getInstance(context).isNetworkHealed(network)) {
            AdGlideLog.d(TAG, "ZERO-WASTE: Skipping " + network + " for " + format + " (Network currently unhealthy/healing)");
            AdGlide.notifyHealerSkip(format != null ? format.name() : "UNKNOWN", network);
            new Handler(Looper.getMainLooper()).post(() -> executeNext(executor, finalCallback));
            return;
        }
        AdGlideLog.d(TAG, "Initiating load for " + format + " on [" + network.toUpperCase(java.util.Locale.ROOT) + "]");
        final boolean[] responded = {false};
        Handler handler = new Handler(Looper.getMainLooper());
        
        Runnable timeoutRunnable = () -> {
            if (!isFinished) {
                isTimedOut = true;
                AdGlideLog.d(TAG, "Timeout (" + timeoutMs + "ms) reached for [" + network + "]. Attempting fallback to maximize match rate.");
                executeNext(executor, finalCallback);
            }
        };
        
        handler.postDelayed(timeoutRunnable, timeoutMs);

        executor.loadNetwork(network, new LoadResultCallback() {
            @Override
            public void onSuccess() {
                if (isFinished) return;
                handler.removeCallbacksAndMessages(null);
                responded[0] = true;
                handler.removeCallbacks(timeoutRunnable);

                long duration = System.currentTimeMillis() - startTime;
                PerformanceLogger.log(format != null ? format.name() : "UNKNOWN", "Loaded in " + duration + "ms from [" + network + "]");

                if (isTimedOut && !isFinished) {
                    AdGlideLog.d(TAG, "LATE MATCH: [" + network.toUpperCase(java.util.Locale.ROOT) + "] " + format + " filled after timeout. Saving to bucket for Zero-Waste delivery.");
                    // The loader logic allows providing the builder itself for caching
                    // This is handled by the higher-level format builder which implement cacheLateFill
                }

                isFinished = true;
                if (finalCallback != null) finalCallback.onAdLoaded(network);
                AdGlide.notifyAdLoaded(format != null ? format.name() : "UNKNOWN", network);
                inFlightRequests.put(format, false);


            }

            @Override
            public void onFailure(String error) {
                if (isFinished) return;
                handler.removeCallbacksAndMessages(null);
                responded[0] = true;
                handler.removeCallbacks(timeoutRunnable);
                AdGlideLog.d(TAG, format + " load failed on [" + network.toUpperCase(java.util.Locale.ROOT) + "]: " + error);
                
                PerformanceLogger.error(format != null ? format.name() : "UNKNOWN", "Failed to load from [" + network + "]: " + error);
                
                // Internal notification for global listener
                AdGlide.notifyAdFailedToLoad(format != null ? format.name() : "UNKNOWN", network, error);



                // If error indicates a configuration issue, blacklist for session to avoid 3.5s timeouts
                if (error != null && (error.toLowerCase(java.util.Locale.ROOT).contains("invalid") || error.toLowerCase(java.util.Locale.ROOT).contains("not found"))) {
                    sessionBlacklist.add(network);
                }

                new Handler(Looper.getMainLooper()).post(() -> executeNext(executor, finalCallback));
            }
        });
    }

    private void executeNext(NetworkExecutor executor, AdGlideCallback finalCallback) {
        String nextNetwork = waterfallManager.getNext();
        if (nextNetwork != null && !nextNetwork.isEmpty()) {
            executeNetwork(nextNetwork, executor, finalCallback);
        } else {
            AdGlideLog.d(TAG, "All waterfall backups exhausted for " + format);
            inFlightRequests.put(format, false);
            if (finalCallback != null) finalCallback.onAdFailedToLoad("Backup exhausted for " + format);
        }
    }
}
