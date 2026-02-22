package com.partharoypc.adglide.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Smart ad loading strategy with exponential backoff and NO_FILL cooldown.
 * <p>
 * Tracks failures per ad-network and applies intelligent retry delays to:
 * <ul>
 * <li>Avoid spamming networks that are temporarily unavailable</li>
 * <li>Protect AdMob match rate by cooling down after NO_FILL responses</li>
 * <li>Automatically recover when networks become available again</li>
 * </ul>
 * </p>
 */
public final class AdLoadStrategy {

    private static final String TAG = "AdGlide";

    /** Max consecutive failures before entering extended cooldown */
    private static final int MAX_FAILURES_BEFORE_COOLDOWN = 3;

    /** Cooldown duration in ms after MAX_FAILURES (5 minutes) */
    private static final long COOLDOWN_DURATION_MS = 5 * 60 * 1000L;

    /** Initial retry delay in ms */
    private static final long INITIAL_RETRY_DELAY_MS = 2000L;

    /** Maximum retry delay in ms */
    private static final long MAX_RETRY_DELAY_MS = 60000L;

    private static volatile AdLoadStrategy instance;

    /** Tracks consecutive failure count per network key */
    private final Map<String, Integer> failureCount = new ConcurrentHashMap<>();

    /** Tracks the timestamp when a network entered cooldown */
    private final Map<String, Long> cooldownUntil = new ConcurrentHashMap<>();

    /** Tracks the last retry delay per network for exponential backoff */
    private final Map<String, Long> lastRetryDelay = new ConcurrentHashMap<>();

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private AdLoadStrategy() {
    }

    /**
     * Returns the singleton instance.
     */
    @NonNull
    public static AdLoadStrategy getInstance() {
        if (instance == null) {
            synchronized (AdLoadStrategy.class) {
                if (instance == null) {
                    instance = new AdLoadStrategy();
                }
            }
        }
        return instance;
    }

    /**
     * Checks if a network is available (not in cooldown).
     *
     * @param networkKey The network identifier (e.g., "admob", "meta").
     * @return True if the network can be used for ad loading.
     */
    public boolean isNetworkAvailable(@NonNull String networkKey) {
        Long until = cooldownUntil.get(networkKey);
        if (until != null && System.currentTimeMillis() < until) {
            Log.d(TAG, "[Strategy] Network '" + networkKey + "' in cooldown. Skipping.");
            return false;
        }
        // Cooldown expired — remove it
        if (until != null) {
            cooldownUntil.remove(networkKey);
            failureCount.remove(networkKey);
            lastRetryDelay.remove(networkKey);
            Log.d(TAG, "[Strategy] Network '" + networkKey + "' cooldown expired. Resetting.");
        }
        return true;
    }

    /**
     * Reports a successful ad load. Resets all failure tracking for this network.
     *
     * @param networkKey The network identifier.
     */
    public void onAdLoaded(@NonNull String networkKey) {
        failureCount.remove(networkKey);
        cooldownUntil.remove(networkKey);
        lastRetryDelay.remove(networkKey);
        Log.d(TAG, "[Strategy] Network '" + networkKey + "' loaded successfully. State reset.");
    }

    /**
     * Reports a failed ad load. Increments failure count and may trigger cooldown.
     *
     * @param networkKey The network identifier.
     */
    public void onAdFailedToLoad(@NonNull String networkKey) {
        int count = failureCount.getOrDefault(networkKey, 0) + 1;
        failureCount.put(networkKey, count);

        if (count >= MAX_FAILURES_BEFORE_COOLDOWN) {
            cooldownUntil.put(networkKey, System.currentTimeMillis() + COOLDOWN_DURATION_MS);
            Log.w(TAG, "[Strategy] Network '" + networkKey + "' entered cooldown after "
                    + count + " failures. Will retry in " + (COOLDOWN_DURATION_MS / 1000) + "s.");
        } else {
            Log.d(TAG, "[Strategy] Network '" + networkKey + "' failure #" + count
                    + "/" + MAX_FAILURES_BEFORE_COOLDOWN);
        }
    }

    /**
     * Schedules a retry with exponential backoff.
     *
     * @param networkKey  The network identifier.
     * @param retryAction The action to execute on retry.
     */
    public void scheduleRetry(@NonNull String networkKey, @NonNull Runnable retryAction) {
        if (!isNetworkAvailable(networkKey)) {
            Log.d(TAG, "[Strategy] Skipping retry for '" + networkKey + "' (in cooldown).");
            return;
        }

        long delay = lastRetryDelay.getOrDefault(networkKey, INITIAL_RETRY_DELAY_MS / 2) * 2;
        delay = Math.min(delay, MAX_RETRY_DELAY_MS);
        lastRetryDelay.put(networkKey, delay);

        Log.d(TAG, "[Strategy] Scheduling retry for '" + networkKey + "' in " + delay + "ms");
        mainHandler.postDelayed(retryAction, delay);
    }

    /**
     * Gets the current retry delay for a network (useful for logging/debugging).
     *
     * @param networkKey The network identifier.
     * @return The current delay in ms, or 0 if no retry is scheduled.
     */
    public long getCurrentRetryDelay(@NonNull String networkKey) {
        return lastRetryDelay.getOrDefault(networkKey, 0L);
    }

    /**
     * Returns the remaining cooldown time in ms for a network, or 0 if not in
     * cooldown.
     */
    public long getRemainingCooldown(@NonNull String networkKey) {
        Long until = cooldownUntil.get(networkKey);
        if (until == null)
            return 0L;
        long remaining = until - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    /**
     * Resets all tracking state. Call this when the app is being destroyed.
     */
    public void reset() {
        failureCount.clear();
        cooldownUntil.clear();
        lastRetryDelay.clear();
        mainHandler.removeCallbacksAndMessages(null);
    }
}
