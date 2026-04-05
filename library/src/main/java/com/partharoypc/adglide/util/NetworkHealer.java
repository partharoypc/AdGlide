package com.partharoypc.adglide.util;

import android.content.Context;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkHealer {
    private static final String TAG = "AdGlide.Healer";

    private static NetworkHealer instance;
    private final AdGlidePrefs prefs;

    private static final long DEFAULT_COOLDOWN_MS = 60000; // 60s
    private static final long HEAVY_COOLDOWN_MS = 300000; // 5 mins
    private static final long MAX_HEALING_AGE_MS = 1800000; // 30 min Max Age

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

    public boolean isRequestAllowed(String network, String format) {
        if (network == null || network.equals(Constant.HOUSE_AD))
            return true;

        String key = network + ":" + format;

        Long lastFailTime = lastFailTimeMap.get(key);
        Integer currentFailCount = failCounterMap.get(key);
        int failCount = currentFailCount != null ? currentFailCount : 0;

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

            if (currentTime - lastFailTime > MAX_HEALING_AGE_MS) {
                recordSuccess(network, format);
                return true;
            }

            long cooldownDuration = failCount >= 3 ? HEAVY_COOLDOWN_MS : DEFAULT_COOLDOWN_MS;
            long elapsedTime = currentTime - lastFailTime;

            if (elapsedTime < cooldownDuration) {
                long remainingSeconds = (cooldownDuration - elapsedTime) / 1000;
                AdGlideLog.d(TAG, "ZERO-WASTE: Protection triggered for [" + network.toUpperCase(java.util.Locale.ROOT)
                        + "] " + format + " Format. Cooling down for " + remainingSeconds + "s");
                return false;
            }
        }
        return true;
    }

    public synchronized void recordSuccess(String network, String format) {
        if (format == null)
            return;
        String key = network + ":" + format;
        failCounterMap.remove(key);
        lastFailTimeMap.remove(key);
        prefs.clearHealer(key);
    }

    public synchronized void recordFailure(String network, String format) {
        recordFailure(network, format, true); // Default to technical error for backward compatibility
    }

    public synchronized void recordFailure(String network, String format, boolean isTechnicalError) {
        if (format == null || format.isEmpty() || network.equals(Constant.HOUSE_AD))
            return;

        if (!isTechnicalError) {
            AdGlideLog.d(TAG, "Non-technical failure (No-Fill) for [" + network.toUpperCase(java.util.Locale.ROOT) + "] "
                    + format + ". Skipping protective cooldown stay on high-CPM network.");
            return;
        }

        String key = network + ":" + format;
        Integer currentFailCount = failCounterMap.get(key);
        int count = (currentFailCount != null ? currentFailCount : 0) + 1;
        long now = System.currentTimeMillis();

        failCounterMap.put(key, count);
        lastFailTimeMap.put(key, now);

        prefs.setHealer(key, count, now);

        AdGlideLog.d(TAG, "Recorded technical failure #" + count + " for [" + network.toUpperCase(java.util.Locale.ROOT) + "] "
                + format + " Format. Protective cooldown active.");
    }

    public void reset() {
        lastFailTimeMap.clear();
        failCounterMap.clear();
        prefs.clearAllHealer();
        AdGlideLog.i(TAG, "NetworkHealer state reset.");
    }
}
