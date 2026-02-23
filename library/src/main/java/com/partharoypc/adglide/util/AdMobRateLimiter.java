package com.partharoypc.adglide.util;

import android.os.SystemClock;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AdMobRateLimiter {

    private static final String TAG = "AdGlide";
    private static final long COOLDOWN_DURATION_MS = 60000; // 60 seconds

    private static final Map<String, Long> lastFailTimeMap = new ConcurrentHashMap<>();

    public static void recordFailure(String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty())
            return;
        lastFailTimeMap.put(adUnitId, SystemClock.elapsedRealtime());
        Log.d(TAG, "[AdMobRateLimiter] Recorded failure for ad unit: " + adUnitId + ". Cooldown started.");
    }

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
                lastFailTimeMap.remove(adUnitId);
                Log.d(TAG, "[AdMobRateLimiter] Cooldown expired for ad unit: " + adUnitId + ". Request allowed.");
            }
        }
        return true;
    }

    public static void resetCooldown(String adUnitId) {
        if (adUnitId != null) {
            lastFailTimeMap.remove(adUnitId);
        }
    }
}
