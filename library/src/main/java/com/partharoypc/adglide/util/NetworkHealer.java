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
     * Checks if a network is "healed" and ready for a new request.
     * Combined check for all units of a network.
     */
    public boolean isNetworkHealed(String network) {
        return isRequestAllowed(network, "GLOBAL");
    }

    /**
     * Checks if a request is allowed for a specific placement to protect the overall Match Rate.
     */
    public boolean isRequestAllowed(String network, String adUnitId) {
        if (network == null || network.equals(Constant.HOUSE_AD)) return true; // House ads never skip
        
        String key = network + ":" + adUnitId;
        
        // Check memory first
        Long lastFailTime = lastFailTimeMap.get(key);
        int failCount = failCounterMap.getOrDefault(key, 0);

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
                recordSuccess(network, adUnitId);
                return true;
            }

            long cooldownDuration = failCount >= 3 ? HEAVY_COOLDOWN_MS : DEFAULT_COOLDOWN_MS;
            long elapsedTime = currentTime - lastFailTime;
            
            if (elapsedTime < cooldownDuration) {
                long remainingSeconds = (cooldownDuration - elapsedTime) / 1000;
                AdGlideLog.d(TAG, "ZERO-WASTE: Protection triggered for [" + network.toUpperCase() + "]. Cooling down for " + remainingSeconds + "s");
                return false;
            }
        }
        return true;
    }

    public void recordSuccess(String network, String adUnitId) {
        if (adUnitId == null) return;
        String key = network + ":" + adUnitId;
        failCounterMap.remove(key);
        lastFailTimeMap.remove(key);
        prefs.clearHealer(key);
    }

    public void recordFailure(String network, String adUnitId) {
        if (adUnitId == null || adUnitId.isEmpty() || network.equals(Constant.HOUSE_AD)) return;
            
        String key = network + ":" + adUnitId;
        int count = failCounterMap.getOrDefault(key, 0) + 1;
        long now = System.currentTimeMillis();
        
        failCounterMap.put(key, count);
        lastFailTimeMap.put(key, now);
        
        // Persist to SharedPreferences
        prefs.setHealer(key, count, now);
        
        AdGlideLog.d(TAG, "Recorded failure #" + count + " for [" + network.toUpperCase() + "]. Protective cooldown active.");
    }
}
