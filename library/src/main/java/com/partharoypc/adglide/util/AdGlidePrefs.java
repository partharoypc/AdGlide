package com.partharoypc.adglide.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Internal preference helper for the AdGlide library.
 * Stores persistent states like ad cooldowns and consent status.
 */
public class AdGlidePrefs {
    private static final String PREF_NAME = "adglide_internal_prefs";
    private static final String KEY_APP_OPEN_LAST_SHOWN = "app_open_last_shown";
    
    private final SharedPreferences prefs;

    public AdGlidePrefs(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public long getAppOpenLastShown() {
        return prefs.getLong(KEY_APP_OPEN_LAST_SHOWN, 0);
    }

    public void setAppOpenLastShown(long timestamp) {
        prefs.edit().putLong(KEY_APP_OPEN_LAST_SHOWN, timestamp).apply();
    }
}
