package com.partharoypc.adglide.util;

import android.util.Log;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;

/**
 * Internal logging utility that respects the global debug flag.
 */
public class AdGlideLog {
    private static final String TAG = "AdGlide";

    public static void d(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.d(TAG, "[" + subTag + "] " + message);
        }
    }

    public static void e(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.e(TAG, "[" + subTag + "] " + message);
        }
    }

    public static void w(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.w(TAG, "[" + subTag + "] " + message);
        }
    }

    public static void i(String subTag, String message) {
        if (isDebugEnabled()) {
            Log.i(TAG, "[" + subTag + "] " + message);
        }
    }

    private static boolean isDebugEnabled() {
        AdGlideConfig config = AdGlide.getConfig();
        return config == null || config.isDebug();
    }
}
