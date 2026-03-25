package com.partharoypc.adglide.util;

import android.util.Log;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;

/**
 * Premium logging utility for the AdGlide SDK.
 * Respects global debug flags and provides formatted output.
 */
public class AdGlideLog {
    private static final String TAG = "AdGlideSDK";
    private static final String PREFIX = "💎 ";

    public static void d(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.d(TAG, PREFIX + "[" + subTag + "] " + message);
        }
    }

    public static void e(String subTag, String message) {
        // Errors are always logged if they are critical, but we can filter by debug for premium feel
        if (isDebugEnabled()) {
            Log.e(TAG, PREFIX + "🛑 [" + subTag + "] " + message);
        }
    }
    
    public static void e(String subTag, String message, Throwable throwable) {
        if (isDebugEnabled()) {
            Log.e(TAG, PREFIX + "🛑 [" + subTag + "] " + message, throwable);
        }
    }

    public static void w(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.w(TAG, PREFIX + "⚠️ [" + subTag + "] " + message);
        }
    }

    public static void i(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.i(TAG, PREFIX + "💡 [" + subTag + "] " + message);
        }
    }

    private static boolean isDebugEnabled() {
        AdGlideConfig config = AdGlide.getConfig();
        return config == null || config.isDebug();
    }
}
