package com.partharoypc.adglide.util;

import android.content.Context;
import android.os.BatteryManager;
import android.os.PowerManager;

public class BatteryUtils {

    /**
     * Checks if the device is currently in Low Battery state (usually < 15% or 20%).
     */
    public static boolean isBatteryLow(Context context) {
        if (context == null) return false;
        try {
            android.content.IntentFilter ifilter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
            android.content.Intent batteryStatus = context.registerReceiver(null, ifilter);
            if (batteryStatus == null) return false;

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale;

            return batteryPct <= 0.15f; // Considered low at 15%
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the device is in Power Save Mode.
     */
    public static boolean isPowerSaveMode(Context context) {
        if (context == null) return false;
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                return powerManager.isPowerSaveMode();
            }
        } catch (Exception e) {
            // Log if needed
        }
        return false;
    }

    /**
     * Returns a multiplier for ad refresh intervals based on battery health.
     * 1.0 = Normal
     * 2.0 = Double interval (Slower refresh)
     * -1.0 = Disable refresh entirely
     */
    public static float getRefreshMultiplier(Context context) {
        if (isPowerSaveMode(context)) return -1.0f; // Disable refresh in power save
        if (isBatteryLow(context)) return 2.0f;      // Slow down in low battery
        return 1.0f;
    }
}
