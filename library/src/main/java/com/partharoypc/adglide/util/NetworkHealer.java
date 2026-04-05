package com.partharoypc.adglide.util;

import android.content.Context;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global optimization utility for the AdGlide SDK.
 * Manages network-wide cooldowns and rate limiting to protect Match Rate and Show Rate.
 * Now persistent via SharedPreferences for cross-session "Zero-Waste" resilience.
 */
public class NetworkHealer {
    private static final String TAG = "AdGlide.Healer";
    
    private static NetworkHealer instance;
    private final AdGlidePrefs prefs;
    
    // Configurable thresholds for "Match Rate Protection"
    private static final long DEFAULT_COOLDOWN_MS = 60000; // 60s
    private static final long HEAVY_COOLDOWN_MS = 300000;  // 5 mins
    private static final long MAX_HEALING_AGE_MS = 7200000; // 2 hours Max Age
    
    private final Map<String, Long> lastFailTimeMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> failCounterMap = new ConcurrentHashMap<>();

    private NetworkHealer(Context context) {
        this.prefs = new AdGlidePrefs(context);
    }

    public static synchronized NetworkHealer getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkHealer(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Checks if a request is allowed for a specific format to protect the overall Match Rate.
     */
    public boolean isRequestAllowed(String network, String format) {
        if (network == null || network.equals(Constant.HOUSE_AD)) return true; // House ads never skip
        
        String key = network + ":" + format;
        
        // Check memory first
        Long lastFailTime = lastFailTimeMap.get(key);
        Integer currentFailCount = failCounterMap.get(key);
        int failCount = currentFailCount != null ? currentFailCount : 0;

        // Fallback to Prefs if memory is empty (after app restart)
        if (lastFailTime == null) {
            lastFailTime = prefs.getHealerTime(key);
            failCount = prefs.getHealerCount(key);
            if (lastFailTime > 0) {
                lastFailTimeMap.put(key, lastFailTime);
                failCounterMap.put(key, failCount);
            }
        }
        
        if (lastFailTime != null && lastFailTime > 0) {
            long currentTime = System.currentTimeMillis();
            
            // Safety: Reset if the data is older than 2 hours
            if (currentTime - lastFailTime > MAX_HEALING_AGE_MS) {
                recordSuccess(network, format);
                return true;
            }

            long cooldownDuration = failCount >= 3 ? HEAVY_COOLDOWN_MS : DEFAULT_COOLDOWN_MS;
            long elapsedTime = currentTime - lastFailTime;
            
            if (elapsedTime < cooldownDuration) {
                long remainingSeconds = (cooldownDuration - elapsedTime) / 1000;
                AdGlideLog.d(TAG, "ZERO-WASTE: Protection triggered for [" + network.toUpperCase(java.util.Locale.ROOT) + "] " + format + " Format. Cooling down for " + remainingSeconds + "s");
                return false;
            }
        }
        return true;
    }

    public void recordSuccess(String network, String format) {
        if (format == null) return;
        String key = network + ":" + format;
        failCounterMap.remove(key);
        lastFailTimeMap.remove(key);
        prefs.clearHealer(key);
    }

    public void recordFailure(String network, String format) {
        if (format == null || format.isEmpty() || network.equals(Constant.HOUSE_AD)) return;
            
        String key = network + ":" + format;
        Integer currentFailCount = failCounterMap.get(key);
        int count = (currentFailCount != null ? currentFailCount : 0) + 1;
        long now = System.currentTimeMillis();
        
        failCounterMap.put(key, count);
        lastFailTimeMap.put(key, now);
        
        // Persist to SharedPreferences
        prefs.setHealer(key, count, now);
        
        AdGlideLog.d(TAG, "Recorded failure #" + count + " for [" + network.toUpperCase(java.util.Locale.ROOT) + "] " + format + " Format. Protective cooldown active.");
    }

    /**
     * Resets all healer state. Useful for testing or when user wants to force-clear cooldowns.
     */
    public void reset() {
        lastFailTimeMap.clear();
        failCounterMap.clear();
        prefs.clearAllHealer();
        AdGlideLog.i(TAG, "NetworkHealer state reset.");
    }
}
