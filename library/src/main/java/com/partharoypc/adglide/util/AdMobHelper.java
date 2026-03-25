package com.partharoypc.adglide.util;

import android.os.SystemClock;
import com.partharoypc.adglide.util.AdGlideLog;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized helper for AdMob specific logic across providers.
 * Handles rate limiting and paid event logging.
 */
public class AdMobHelper {
    private static final String TAG = "AdGlide.AdMob";
    private static final long COOLDOWN_DURATION_MS = 30000; // 30 seconds
    private static final Map<String, Long> lastFailTimeMap = new ConcurrentHashMap<>();

    private AdMobHelper() {
        // Utility class
    }



    /**
     * Checks if a request is allowed based on rate limiting.
     *
     * @param adUnitId the ad unit ID
     * @return true if allowed, false if rate limited
     */
    public static boolean isRequestAllowed(String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty())
            return true;

        Long lastFailTime = lastFailTimeMap.get(adUnitId);
        if (lastFailTime != null) {
            long elapsedTime = SystemClock.elapsedRealtime() - lastFailTime;
            if (elapsedTime < COOLDOWN_DURATION_MS) {
                long remainingSeconds = (COOLDOWN_DURATION_MS - elapsedTime) / 1000;
                
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                if (config != null) {
                    AdGlideLog.d(TAG, "Request blocked for ad unit: " + adUnitId +
                            ". Cooldown active for " + remainingSeconds + " more seconds.");
                }
                return false;
            } else {
                lastFailTimeMap.remove(adUnitId);
                
                com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
                if (config != null) {
                    AdGlideLog.d(TAG, "Cooldown expired for ad unit: " + adUnitId + ". Request allowed.");
                }
            }
        }
        return true;
    }

    /**
     * Records a failure for rate limiting calculations.
     *
     * @param adUnitId the ad unit ID
     */
    public static void recordFailure(String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty())
            return;
        lastFailTimeMap.put(adUnitId, SystemClock.elapsedRealtime());
        
        com.partharoypc.adglide.AdGlideConfig config = com.partharoypc.adglide.AdGlide.getConfig();
        if (config != null) {
            AdGlideLog.d(TAG, "Recorded failure for ad unit: " + adUnitId + ". Cooldown started.");
        }
    }

    /**
     * Resets any active cooldown for an ad unit.
     *
     * @param adUnitId the ad unit ID
     */
    public static void resetCooldown(String adUnitId) {
        if (adUnitId != null) {
            lastFailTimeMap.remove(adUnitId);
        }
    }
}
