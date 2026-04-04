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
    
    // House Ad Persistence
    private static final String KEY_HOUSE_BANNER_IMG = "house_banner_img";
    private static final String KEY_HOUSE_BANNER_URL = "house_banner_url";
    private static final String KEY_HOUSE_INTER_IMG = "house_inter_img";
    private static final String KEY_HOUSE_INTER_URL = "house_inter_url";
    private static final String KEY_HOUSE_NATIVE_TITLE = "house_native_title";
    private static final String KEY_HOUSE_NATIVE_DESC = "house_native_desc";
    private static final String KEY_HOUSE_NATIVE_IMG = "house_native_image";
    private static final String KEY_HOUSE_NATIVE_ICON = "house_native_icon";
    private static final String KEY_HOUSE_NATIVE_CTA = "house_native_cta";
    private static final String KEY_HOUSE_NATIVE_URL = "house_native_url";

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

    public void saveHouseAdMetadata(com.partharoypc.adglide.AdGlideConfig config) {
        if (config == null) return;
        prefs.edit()
            .putString(KEY_HOUSE_BANNER_IMG, config.getHouseAdBannerImage())
            .putString(KEY_HOUSE_BANNER_URL, config.getHouseAdBannerClickUrl())
            .putString(KEY_HOUSE_INTER_IMG, config.getHouseAdInterstitialImage())
            .putString(KEY_HOUSE_INTER_URL, config.getHouseAdInterstitialClickUrl())
            .putString(KEY_HOUSE_NATIVE_TITLE, config.getHouseAdNativeTitle())
            .putString(KEY_HOUSE_NATIVE_DESC, config.getHouseAdNativeDescription())
            .putString(KEY_HOUSE_NATIVE_IMG, config.getHouseAdNativeImage())
            .putString(KEY_HOUSE_NATIVE_ICON, config.getHouseAdNativeIcon())
            .putString(KEY_HOUSE_NATIVE_CTA, config.getHouseAdNativeCTA())
            .putString(KEY_HOUSE_NATIVE_URL, config.getHouseAdNativeClickUrl())
            .apply();
    }

    public com.partharoypc.adglide.AdGlideConfig.Builder applyStoredHouseAd(com.partharoypc.adglide.AdGlideConfig.Builder builder) {
        return builder
            .houseAdBannerImage(prefs.getString(KEY_HOUSE_BANNER_IMG, ""))
            .houseAdBannerClickUrl(prefs.getString(KEY_HOUSE_BANNER_URL, ""))
            .houseAdInterstitialImage(prefs.getString(KEY_HOUSE_INTER_IMG, ""))
            .houseAdInterstitialClickUrl(prefs.getString(KEY_HOUSE_INTER_URL, ""))
            .houseAdNativeTitle(prefs.getString(KEY_HOUSE_NATIVE_TITLE, ""))
            .houseAdNativeDescription(prefs.getString(KEY_HOUSE_NATIVE_DESC, ""))
            .houseAdNativeImage(prefs.getString(KEY_HOUSE_NATIVE_IMG, ""))
            .houseAdNativeIcon(prefs.getString(KEY_HOUSE_NATIVE_ICON, ""))
            .houseAdNativeCTA(prefs.getString(KEY_HOUSE_NATIVE_CTA, ""))
            .houseAdNativeClickUrl(prefs.getString(KEY_HOUSE_NATIVE_URL, ""));
    }

    // --- Network Healer Persistence ---

    public long getHealerTime(String key) {
        return prefs.getLong("healer_fail_time_" + key, 0);
    }

    public int getHealerCount(String key) {
        return prefs.getInt("healer_fail_count_" + key, 0);
    }

    public void setHealer(String key, int count, long timestamp) {
        prefs.edit()
            .putLong("healer_fail_time_" + key, timestamp)
            .putInt("healer_fail_count_" + key, count)
            .apply();
    }

    public void clearHealer(String key) {
        prefs.edit()
            .remove("healer_fail_time_" + key)
            .remove("healer_fail_count_" + key)
            .apply();
    }
}
