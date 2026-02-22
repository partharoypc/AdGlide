package com.partharoypc.adglide.util;

import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to prevent excessive AdMob requests when NO_FILL is
 * encountered.
 * This safeguards the AdMob Match Rate from dropping drastically.
 */
public class AdMobRateLimiter {

    private static final String TAG = "AdGlide";
    private static final long COOLDOWN_DURATION_MS = 60000; // 60 seconds

    private static final Map<String, Long> lastFailTimeMap = new HashMap<>();

    /**
     * Records a failure for a specific ad unit ID.
     *
     * @param adUnitId The AdMob ad unit ID that failed to load (specifically
     *                 NO_FILL or error code 3).
     */
    public static void recordFailure(String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty())
            return;
        lastFailTimeMap.put(adUnitId, SystemClock.elapsedRealtime());
        Log.d(TAG, "[AdMobRateLimiter] Recorded failure for ad unit: " + adUnitId + ". Cooldown started.");
    }

    /**
     * Checks if a request is allowed for the specified ad unit ID.
     *
     * @param adUnitId The AdMob ad unit ID.
     * @return True if allowed to request, false if currently in cooldown.
     */
    public static boolean isRequestAllowed(String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty())
            return true;

        Long lastFailTime = lastFailTimeMap.get(adUnitId);
        if (lastFailTime != null) {
            long elapsedTime = SystemClock.elapsedRealtime() - lastFailTime;
            if (elapsedTime < COOLDOWN_DURATION_MS) {
                long remainingSeconds = (COOLDOWN_DURATION_MS - elapsedTime) / 1000;
                Log.d(TAG, "[AdMobRateLimiter] Request blocked for ad unit: " + adUnitId +
                        ". Cooldown active for " + remainingSeconds + " more seconds.");
                return false;
            } else {
                // Cooldown expired, clear it
                lastFailTimeMap.remove(adUnitId);
                Log.d(TAG, "[AdMobRateLimiter] Cooldown expired for ad unit: " + adUnitId + ". Request allowed.");
            }
        }
        return true;
    }

    /**
     * Resets the cooldown for a specific ad unit ID.
     * Useful if you want to bypass the cooldown manually or upon successful load
     * elsewhere.
     */
    public static void resetCooldown(String adUnitId) {
        if (adUnitId != null) {
            lastFailTimeMap.remove(adUnitId);
        }
    }
}
